package com.dlim2012.booking.service.booking_entity;

import com.dlim2012.booking.config.PayPalConfig;
import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.booking.dto.reserve.BookingResponse;
import com.dlim2012.booking.service.CacheService;
import com.dlim2012.booking.service.booking_entity.hotel_entity.RoomsEntityService;
import com.dlim2012.booking.service.booking_entity.utils.PaypalService;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.archive.BookingIdArchiveRequest;
import com.dlim2012.clients.kafka.dto.notification.BookingNotification;
import com.dlim2012.clients.kafka.dto.notification.PaymentNotification;
import com.dlim2012.clients.mysql_booking.entity.*;
import com.dlim2012.clients.mysql_booking.repository.*;
import com.dlim2012.clients.utils.PriceService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.dlim2012.booking.config.PayPalConfig.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final CacheService cacheService;
    private final PaypalService paypalService;
    private final RoomsEntityService roomsEntityService;
    private final DatesService datesService;
    private final PriceService priceService;

    private final BookingRepository bookingRepository;
    private final BookingRoomsRepository bookingRoomsRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;
    private final RoomRepository roomRepository;
    private final DatesRepository datesRepository;
    private final PriceRepository priceRepository;

    private final PayPalConfig payPalConfig;

    private final KafkaTemplate<String, BookingIdArchiveRequest> bookingIdArchiveKafkaTemplate;
    private final KafkaTemplate<String, BookingNotification> bookingNotificationKafkaTemplate;
    private final KafkaTemplate<String, PaymentNotification> paymentNotificationKafkaTemplate;

    private final EntityManager entityManager;


    // Whichever case -> change booking status only if RESERVED
        /* - Save in MySQL -> get booking ID
           - Save in Redis with TTL : { BookingID : "" }
               1) Payment success
                   evict key
                   1) RESERVED -> BOOKED
                   2) CANCELLED -> cancel payment
               2) Payment cancelled
                   evict key from REDIS
                   1) RESERVED -> CANCELLED; cancel reservation
                   2) ELSE -> IGNORE
               3) Payment timeout -> change status if RESERVED
                   1) RESERVED -> CANCELLED; cancel reservation
                   2) ELSE -> IGNORE
        * */

    private final Map<BookingStatus, BookingMainStatus> statusMap = Map.ofEntries(
            Map.entry(BookingStatus.RESERVED, BookingMainStatus.RESERVED),
            Map.entry(BookingStatus.BOOKED, BookingMainStatus.BOOKED),
            Map.entry(BookingStatus.CANCELLED_PAYMENT, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_PAYMENT_TIME_EXPIRED, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_NO_PREPAYMENT, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_BY_APP_USER, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_BY_HOTEL_MANAGER, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_BY_ADMIN, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.COMPLETED, BookingMainStatus.COMPLETED)
    );


    public Map<Integer, List<Long>> reserveHotelRooms(
            Integer hotelId,
            Integer userId,
            BookingRequest request,
            Set<Rooms> roomsSet,
            Map<Integer, Long> roomsPrice
    ){

        // gather number of rooms
        Map<Integer, Integer> roomNumMap = new HashMap<>();
        for (BookingRequest.BookingRequestRooms bookingRequestRooms: request.getRooms()){
            Integer roomsId = bookingRequestRooms.getRoomsId();
            roomNumMap.put(roomsId, roomNumMap.getOrDefault(roomsId, 0) + 1);
        }

        // todo: optimize fetch
//        Set<Rooms> roomsSet = roomsRepository.findByHotelId(hotelId);
        Set<Room> roomSet = new HashSet<>();
        for (Rooms rooms: roomsSet){
            roomSet.addAll(rooms.getRoomSet());
        }

        Set<Price> priceSet = priceRepository.findByHotelIdAndDates(
                hotelId, request.getStartDate(), request.getEndDate());
        Set<Dates> datesSet = datesRepository.findByHotelIdAndDatesContainsWithLock(
                hotelId, request.getStartDate(), request.getEndDate());

        Map<Long, Integer> roomsIdMap = new HashMap<>();
        for (Room room: roomSet){
            roomsIdMap.put(room.getId(), room.getRooms().getId());
        }


        Map<Integer, Rooms> roomsMap = new HashMap<>();
        for (Rooms rooms: roomsSet){
            roomsMap.put(rooms.getId(), rooms);
        }

        // validate request
        Set<Integer> invalidRoomsId = roomsEntityService.validateBookingRequest(roomNumMap, request, roomsMap, roomsIdMap, datesSet, priceSet, roomsPrice);
        System.out.println("invalidRoomsIds " + invalidRoomsId);
        if (invalidRoomsId != null){
            // release lock
            datesRepository.saveAll(datesSet);
            // update data in elasticsearch for possible inconsistencies
            roomsEntityService.roomsVersionUp(hotelId, invalidRoomsId, roomsMap);
            return null;
        }

        // remove date ranges and release lock
        Map<Integer, List<Long>> roomsIdToroomIds = datesService._removeDatesSameRange(hotelId, roomNumMap, request, roomsIdMap, datesSet);

        // adjust reservedDates
        List<Rooms> roomsList = roomsRepository.findByHotelIdWithLock(hotelId);
        int numDates = Math.toIntExact(ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()));
        for (Rooms rooms: roomsList){
            List<Long> roomIds = roomsIdToroomIds.getOrDefault(rooms.getId(), null);
            if (roomIds != null){
                rooms.setDatesReserved(rooms.getDatesReserved() + roomIds.size() * numDates);
            }
        }
        roomsRepository.saveAll(roomsList);

        return roomsIdToroomIds;
    }

    public void unReserveHotelRooms(Booking booking){
        datesService._addDateRanges(booking);
    }

    public Booking reserveHotel(Integer hotelId, Integer userId, BookingRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));

        if (request.getCheckInTime() < 0){
            throw new IllegalArgumentException("Check-In time has to be at least 0.");
        }
        if (request.getCheckOutTime() >= 1440){
            throw new IllegalArgumentException("Check-Out time has to be smaller than 1440.");
        }

        Map<Integer, Long> roomsPrice = new HashMap<>();
        Map<Integer, List<Long>> roomsIdToroomIds = reserveHotelRooms(hotelId, userId, request, hotel.getRoomsSet(), roomsPrice);

        if (roomsIdToroomIds == null){
            return null;
        }
//        datesRepository.saveAll(datesToUpdate);
//        datesRepository.deleteAll(datesToDelete);

        // Map to save noPrepaymentUntil, freeCancellation until
        Map<Integer, BookingRequest.BookingRequestRooms> bookingRequestRoomsMap = new HashMap<>();
        for (BookingRequest.BookingRequestRooms bookingRequestRooms: request.getRooms()){
            bookingRequestRoomsMap.put(bookingRequestRooms.getRoomsId(), bookingRequestRooms);
        }


        LocalDateTime startDateTime = LocalDateTime.of(
                request.getStartDate().getYear(),
                request.getStartDate().getMonthValue(),
                request.getStartDate().getDayOfMonth(),
                request.getCheckInTime() / 60,
                request.getCheckInTime() % 60
        );
        LocalDateTime endDateTime = LocalDateTime.of(
                request.getEndDate().getYear(),
                request.getEndDate().getMonthValue(),
                request.getEndDate().getDayOfMonth(),
                request.getCheckOutTime() / 60,
                request.getCheckOutTime() % 60
        );

        // Save booking information
        Booking booking = Booking.builder()
                .userId(userId)
                .hotelId(hotelId)
                .hotelManagerId(hotel.getHotelManagerId())
                .reservationTime(LocalDateTime.now())
                .hotelName(request.getHotelName())
                .neighborhood(request.getNeighborhood())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .specialRequests(request.getSpecialRequests())
                .estimatedArrivalHour(request.getEstimatedArrivalHour())
                .mainStatus(BookingMainStatus.RESERVED)
                .status(BookingStatus.RESERVED)
                .priceInCents(request.getPriceInCents())
                .build();

        booking = bookingRepository.save(booking);

        Map<Integer, Rooms> roomsMap = new HashMap<>();
        for (Rooms rooms: hotel.getRoomsSet()){
            roomsMap.put(rooms.getId(), rooms);
        }

        List<BookingRooms> bookingRoomsList = new ArrayList<>();
        for (Map.Entry<Integer, List<Long>> entry: roomsIdToroomIds.entrySet()){
            Rooms rooms = roomsMap.get(entry.getKey());
            BookingRequest.BookingRequestRooms bookingRequestRooms = bookingRequestRoomsMap.get(entry.getKey());



            BookingRooms bookingRooms = BookingRooms.builder()
                    .booking(booking)
                    .roomsId(entry.getKey())
                    .roomsDisplayName(rooms.getDisplayName())
                    .roomsShortName(rooms.getShortName())
                    .prepayUntil(bookingRequestRooms.getNoPrepaymentUntil())
                    .freeCancellationUntil(bookingRequestRooms.getFreeCancellationUntil())
                    .pricePerRoomInCents(roomsPrice.get(entry.getKey()))
                    .build();
            bookingRoomsList.add(bookingRooms);
        }
        bookingRoomsList = bookingRoomsRepository.saveAll(bookingRoomsList);

        Map<Integer, BookingRooms> bookingRoomsMap = new HashMap<>();
        for (BookingRooms bookingRooms: bookingRoomsList){
            bookingRoomsMap.put(bookingRooms.getRoomsId(), bookingRooms);
        }

        List<BookingRoom> bookingRoomList = new ArrayList<>();
        for (BookingRequest.BookingRequestRooms bookingRequestRooms: request.getRooms()){
            Integer roomsId = bookingRequestRooms.getRoomsId();
            List<Long> roomIdList = roomsIdToroomIds.get(roomsId);
            BookingRoom bookingRoom = BookingRoom.builder()
                    .roomId(roomIdList.get(roomIdList.size()-1))
                    .startDateTime(startDateTime)
                    .endDateTime(endDateTime)
                    .bookingRooms(bookingRoomsMap.get(roomsId))
                    .guestName(bookingRequestRooms.getGuestName())
                    .guestEmail(bookingRequestRooms.getGuestEmail())
                    .status(BookingStatus.RESERVED)
                    .build();
            bookingRoomList.add(bookingRoom);
            roomIdList.remove(roomIdList.size()-1);
        }
        bookingRoomRepository.saveAll(bookingRoomList);

        BookingNotification bookingNotification = BookingNotification.builder()
                .bookingId(booking.getId())
                .hotelId(booking.getHotelId())
                .userId(booking.getUserId())
                .hotelManagerId(booking.getHotelManagerId())
                .build();
        bookingNotificationKafkaTemplate.send("booking-notification", bookingNotification);

        return booking;
    }

    public BookingResponse createBookingPayment(Booking booking, String description){

        Payment payment;
        try{
            payment = paypalService.createPayment(
                    booking.getPriceInCents(),
                    CONCURRENCY,
                    METHOD,
                    INTENT,
                    description,
                    payPalConfig.getCancelUrl(booking.getId()),
                    payPalConfig.getSuccessUrl(booking.getId())
            );
            for(Links link: payment.getLinks()) {
                if (link.getRel().equals("approval_url")){
                    // save invoice ID and redirect for user approval
                    booking.setInvoiceId(payment.getId());
                    bookingRepository.save(booking);
                    return new BookingResponse(booking.getId(), true, link.getHref());
                }
            }
        } catch (PayPalRESTException e){
            log.error(e.getMessage());
            return new BookingResponse(booking.getId(), true, "");
        }
        return new BookingResponse(booking.getId(), true, "");
    }

    public BookingResponse bookHotel(Integer hotelId, Integer userId, BookingRequest request) {
        Booking booking = reserveHotel(hotelId, userId, request);
        if (!booking.getStatus().equals(BookingStatus.RESERVED)){
            return new BookingResponse(-1L, false, "");
        }
        String description = String.format("Booking rooms from hotel %d.", booking.getHotelId());
        BookingResponse bookingResponse = createBookingPayment(booking, description);
        if (!bookingResponse.getRedirectUrl().isEmpty()) {
            booking.setStatus(BookingStatus.RESERVED_FOR_TIMEOUT);
        }
        bookingRepository.save(booking);
        cacheService.cacheBookingIdForTTL(booking.getId());
        System.out.println(bookingResponse);
        return bookingResponse;
    }

    public boolean processPaymentSuccess(Long bookingId, String paymentId, String payerId) {
        cacheService.cacheBookingIdEvict(bookingId);
        Optional<Booking> optionalBooking = bookingRepository.findByIdWithLock(bookingId);
        if (optionalBooking.isEmpty()){
            return false;
        }
        Booking booking = optionalBooking.get();
//        if (!booking.getMainStatus().equals(BookingMainStatus.RESERVED)){
//            paypalService.cancelPayment(booking.getInvoiceId());
//            return false;
//        }

        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
        } catch(PayPalRESTException e){
            log.error(e.getMessage());
            return false;
        }

        booking.setMainStatus(BookingMainStatus.BOOKED);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setInvoiceConfirmTime(LocalDateTime.now());
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                bookingRoom.setStatus(BookingStatus.BOOKED);
            }
        }

        bookingRepository.save(booking);
//        System.out.println(bookingRepository.findById(bookingId));


        // adjust bookingDates
        Map<Integer, Integer> bookingNumRoom = new HashMap<>();
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            bookingNumRoom.put(bookingRooms.getRoomsId(), bookingRooms.getBookingRoomList().size());
        }

        List<Rooms> roomsList = roomsRepository.findByHotelIdWithLock(booking.getHotelId());
        int numDates = 0;
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                numDates += Math.toIntExact(
                        ChronoUnit.DAYS.between(
                                bookingRoom.getStartDateTime().toLocalDate(),
                                bookingRoom.getEndDateTime().toLocalDate()
                        )
                );
            }
        }

        // adjust rooms statistics
        for (Rooms rooms: roomsList){
            Integer numRoom = bookingNumRoom.getOrDefault(rooms.getId(), 0);
            if (numRoom > 0) {
                rooms.setDatesReserved(rooms.getDatesReserved() - numRoom * numDates);
                rooms.setDatesBooked(rooms.getDatesBooked() + numRoom * numDates);
            }
        }
        roomsRepository.saveAll(roomsList);

        PaymentNotification paymentNotification = PaymentNotification.builder()
                .bookingId(booking.getId())
                .hotelId(booking.getHotelId())
                .userId(booking.getUserId())
                .hotelManagerId(booking.getHotelManagerId())
                .priceInCents(booking.getPriceInCents())
                .build();
        paymentNotificationKafkaTemplate.send("payment-notification", paymentNotification);
        return true;
    }


    public void processPaymentCancelledIfStatusReservedForTimeOut(Long bookingId, BookingStatus status) {
        cacheService.cacheBookingIdEvict(bookingId);
        Optional<Booking> optionalBooking = bookingRepository.findByIdWithLock(bookingId);
        if (optionalBooking.isEmpty()){
            return;
        }
        Booking booking = optionalBooking.get();
        if (!booking.getStatus().equals(BookingStatus.RESERVED_FOR_TIMEOUT)){
            paypalService.cancelPayment(booking.getInvoiceId());
            return;
        }
        booking.setMainStatus(statusMap.get(status));
        booking.setStatus(status);
        booking = bookingRepository.save(booking);

        unReserveHotelRooms(booking);

        BookingIdArchiveRequest bookingIdArchiveRequest = BookingIdArchiveRequest.builder().bookingIds(List.of(bookingId)).build();
        bookingIdArchiveKafkaTemplate.send("booking-archive", bookingIdArchiveRequest);
    }


    public void cancelBooking(Booking booking, BookingStatus status){
        if (!booking.getMainStatus().equals(BookingMainStatus.RESERVED)){
            if (!booking.getMainStatus().equals(BookingMainStatus.BOOKED)){
                log.error("Booking {} to be cancelled is not RESERVED or BOOKED.", booking.getId());
            }
            paypalService.cancelPayment(booking.getInvoiceId());
        }

        booking.setMainStatus(BookingMainStatus.CANCELLED);
        booking.setStatus(status);
        cacheService.putBooking(booking);
        bookingRepository.save(booking);

        unReserveHotelRooms(booking);

        BookingIdArchiveRequest bookingIdArchiveRequest = BookingIdArchiveRequest.builder().bookingIds(List.of(booking.getId())).build();
        bookingIdArchiveKafkaTemplate.send("booking-archive", bookingIdArchiveRequest);
    }


}
