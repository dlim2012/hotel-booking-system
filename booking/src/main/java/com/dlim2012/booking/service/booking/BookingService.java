package com.dlim2012.booking.service.booking;

import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.booking.service.common.CacheService;
import com.dlim2012.booking.service.common.DatesService;
import com.dlim2012.booking.service.common.PriceService;
import com.dlim2012.booking.service.common.RoomService;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.mysql_booking.entity.*;
import com.dlim2012.clients.mysql_booking.repository.*;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingStatusService bookingStatusService;
    private final PayService payService;
    private final RoomService roomService;
    private final DatesService datesService;
    private final CacheService cacheService;

    private final BookingRepository bookingRepository;
    private final RoomsRepository roomsRepository;
    private final RoomRepository roomRepository;

    /*
    Requests from Rest API (User Authentication needed)
     */

    public Booking reserve(
            Integer userId, Hotel hotel, Map<Integer, Long> roomsPriceMap,
            BookingRequest request, Map<Integer, List<BookingRequest.BookingRequestRooms>> requestRoomsMap
    ) {

        // make Booking entity
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
                .hotelId(hotel.getId())
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

        List<Room> modifiedRoomList = new ArrayList<>();
        Set<BookingRooms> bookingRoomsSet = new HashSet<>();
        for (Rooms rooms : hotel.getRoomsSet()) {
            List<BookingRequest.BookingRequestRooms> bookingRequestRoomsList = requestRoomsMap.get(rooms.getId());
            if (bookingRequestRoomsList == null) continue;
            Long roomsPrice = roomsPriceMap.get(rooms.getId());
            if (roomsPrice == null) {
                throw new RuntimeException("Rooms price not found.");
            }

            // Remove date ranges
            List<Room> _modifiedRoomList = datesService.removeDatesFromRooms(rooms, request.getStartDate(), request.getEndDate(), bookingRequestRoomsList.size());
            if (_modifiedRoomList.size() != bookingRequestRoomsList.size()) {
                throw new RuntimeException("Number of modified 'room' does not match with requested number.");
            }
            modifiedRoomList.addAll(_modifiedRoomList);

            BookingRooms bookingRooms = BookingRooms.builder()
                    .booking(booking)
                    .roomsId(rooms.getId())
                    .roomsDisplayName(rooms.getDisplayName())
                    .roomsShortName(rooms.getShortName())
                    .prepayUntil(Collections.max(bookingRequestRoomsList.stream()
                            .map(BookingRequest.BookingRequestRooms::getNoPrepaymentUntil).toList()))
                    .freeCancellationUntil(Collections.max(bookingRequestRoomsList.stream()
                            .map(BookingRequest.BookingRequestRooms::getFreeCancellationUntil).toList()))
                    .pricePerRoomInCents(roomsPrice)
                    .build();

            Set<BookingRoom> bookingRoomSet = new HashSet<>();
            for (int i = 0; i < bookingRequestRoomsList.size(); i++) {
                Room room = _modifiedRoomList.get(i);
                BookingRequest.BookingRequestRooms bookingRequestRooms = bookingRequestRoomsList.get(i);
                bookingRoomSet.add(
                        BookingRoom.builder()
                                .bookingRooms(bookingRooms)
                                .roomId(room.getId())
                                .startDateTime(startDateTime)
                                .endDateTime(endDateTime)
                                .status(BookingStatus.RESERVED)
                                .guestName(bookingRequestRooms.getGuestName())
                                .guestEmail(bookingRequestRooms.getGuestEmail())
                                .build()
                );
            }
            bookingRooms.setBookingRoomSet(bookingRoomSet);
            bookingRoomsSet.add(bookingRooms);
        }

        booking.setBookingRooms(bookingRoomsSet);
        bookingRepository.save(booking);

        datesService.sendRoomToEs(hotel.getId(), modifiedRoomList);

        return booking;
    }

    public Booking getBookingWithLock(
            UserRole userRole, Integer userId,
            Long bookingId
    ) {
        if (userRole.equals(UserRole.HOTEL_MANAGER)) {
            return bookingRepository.findByIdAndHotelManagerIdWithLock(bookingId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        } else if (userRole.equals(UserRole.APP_USER)) {
            return bookingRepository.findByIdAndUserIdWithLock(bookingId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        } else {
            throw new RuntimeException("Invalid UserRole.");
        }
    }

    public Booking getBookingWithLock(
            Long bookingId
    ) {
        return bookingRepository.findByIdWithLock(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }

    public void cancel(
            UserRole userRole, Integer userId,
            Long bookingId
    ) {
        Booking booking = getBookingWithLock(userRole, userId, bookingId);
        BookingStatus newBookingStatus = bookingStatusService.getCancelStatus(userRole);
        booking.setStatus(newBookingStatus);
        booking.setMainStatus(BookingMainStatus.CANCELLED);

        Map<Long, Room> roomMap = roomService.getRoomMapWithLock(booking.getHotelId());

        List<Room> modifiedRoomList = new ArrayList<>();
        for (BookingRooms bookingRooms : booking.getBookingRooms()) {
            for (BookingRoom bookingRoom : bookingRooms.getBookingRoomSet()) {
                if (bookingStatusService.isActiveStatus(bookingRoom.getStatus())) {
                    bookingRoom.setStatus(newBookingStatus);

                    // add dates
                    Room room = roomMap.get(bookingRoom.getRoomId());
                    datesService.addDateRange(room, bookingRoom.getStartDateTime().toLocalDate(), bookingRoom.getEndDateTime().toLocalDate());
                    room.setDatesVersion(room.getDatesVersion() + 1L);
                    modifiedRoomList.add(room);
                }
            }
        }

        bookingRepository.save(booking);
        datesService.sendRoomToEs(booking.getHotelId(), modifiedRoomList);
        cacheService.putBooking(booking);
    }

    public void addRoom(
            UserRole userRole, Integer userId,
            Integer hotelId, Integer roomsId, Long roomId,
            Long bookingId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean payed,
            String guestName, String guestEmail
    ) {
        Rooms rooms = roomsRepository.findByIdWithLock(roomsId)
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found."));
        Booking booking = getBookingWithLock(userRole, userId, bookingId);


        Room modifiedRoom = null;
        for (Room room : rooms.getRoomSet()) {
            if (room.getId().equals(roomId)) {
                List<Dates> newDatesList = datesService.removeDateRange(room, startDateTime.toLocalDate(), endDateTime.toLocalDate());
                datesService.saveNewDates(List.of(room), newDatesList);
                room.setDatesVersion(room.getDatesVersion() + 1L);
                modifiedRoom = room;
                break;
            }
        }

        if (modifiedRoom == null) {
            throw new ResourceNotFoundException("Room not found.");
        }

        BookingRoom bookingRoom = BookingRoom.builder()
                .roomId(roomId)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .status(payed ? BookingStatus.BOOKED : BookingStatus.RESERVED)
                .guestName(guestName != null && !guestName.isEmpty() ? guestName : booking.getFirstName())
                .guestEmail(guestEmail != null && !guestEmail.isEmpty() ? guestEmail : booking.getEmail())
                .build();

        BookingRooms bookingRooms = null;
        for (BookingRooms _bookingRooms : booking.getBookingRooms()) {
            if (_bookingRooms.getRoomsId().equals(roomsId)) {
                bookingRooms = _bookingRooms;
                break;
            }
        }
        if (bookingRooms == null) {
            bookingRooms = BookingRooms.builder()
                    .booking(booking)
                    .roomsId(roomsId)
                    .roomsDisplayName(rooms.getDisplayName())
                    .roomsShortName(rooms.getShortName())
                    .build();
            booking.getBookingRooms().add(bookingRooms);
        }
        bookingRoom.setBookingRooms(bookingRooms);
        bookingRooms.getBookingRoomSet().add(bookingRoom);


        bookingRepository.save(booking);
        datesService.sendRoomToEs(hotelId, List.of(modifiedRoom));
        cacheService.putBooking(booking);

    }

    public void editRoom(
            UserRole userRole, Integer userId,
            Long bookingId, Long BookingRoomsId, Long bookingRoomId,
            Long newRoomId, LocalDateTime newStartDateTime, LocalDateTime newEndDateTime
    ) {
        Booking booking = getBookingWithLock(userRole, userId, bookingId);
        // todo
    }

    public void cancelRoom(
            UserRole userRole, Integer userId,
            Long bookingId, Long bookingRoomsId, Long bookingRoomId
    ) {
        Booking booking = getBookingWithLock(userRole, userId, bookingId);
        BookingStatus newBookingStatus = bookingStatusService.getCancelStatus(userRole);
        for (BookingRooms bookingRooms : booking.getBookingRooms()) {
            if (!bookingRooms.getId().equals(bookingRoomsId)) {
                continue;
            }
            for (BookingRoom bookingRoom : bookingRooms.getBookingRoomSet()) {
                if (bookingRoom.getId().equals(bookingRoomId) && bookingStatusService.isActiveStatus(bookingRoom.getStatus())) {
                    bookingRoom.setStatus(newBookingStatus);

                    Room room = roomRepository.findByIdWithLock(bookingRoom.getRoomId())
                            .orElseThrow(() -> new ResourceNotFoundException("Room not found."));
                    datesService.addDateRange(room, bookingRoom.getStartDateTime().toLocalDate(), bookingRoom.getEndDateTime().toLocalDate());
                    room.setDatesVersion(room.getDatesVersion() + 1L);
                    datesService.sendRoomToEs(booking.getHotelId(), List.of(room));
                    break;
                }
            }
        }
        cacheService.putBooking(booking);
    }


    /*
    Internal requests
     */

    public String createPaypalPayment(Booking booking) throws PayPalRESTException {
        if (booking == null) {
            return null;
        }
        Payment payment = payService.createPaypalPayment(booking);
        for (Links link : payment.getLinks()) {
            if (link.getRel().equals("approval_url")) {
                booking.setInvoiceId(payment.getId());
                bookingRepository.save(booking);
                return link.getHref();
            }
        }
        bookingRepository.save(booking); // release lock
        throw new RuntimeException("Paypal approval url not found.");

    }

    public void cancelIfStatusEquals(Long bookingId, BookingStatus prevBookingStatus, BookingStatus newBookingStatus) {
        Booking booking = bookingRepository.findByIdWithLock(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        if (booking.getStatus().equals(prevBookingStatus)) {
            booking.setStatus(newBookingStatus);
        }
        bookingRepository.save(booking);
        cacheService.putBooking(booking);
    }
}
