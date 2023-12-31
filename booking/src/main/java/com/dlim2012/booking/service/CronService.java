package com.dlim2012.booking.service;

import com.dlim2012.booking.service.booking_entity.DatesService;
import com.dlim2012.booking.service.booking_entity.PriceService;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.kafka.dto.archive.BookingIdArchiveRequest;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelsNewDayDetails;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.dlim2012.clients.mysql_booking.entity.*;
import com.dlim2012.clients.mysql_booking.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CronService {
    private final PriceService priceService;
    private final DatesService datesService;

    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;
    private final DatesRepository datesRepository;
    private final PriceRepository priceRepository;

    private final BookingRepository bookingRepository;

    private final KafkaTemplate<String, HotelsNewDayDetails> hotelNewDayKafkaTemplate;
    private final KafkaTemplate<String, BookingIdArchiveRequest> bookingIdArchiveKafkaTemplate;


    private final EntityManager entityManager;

    private final Integer MAX_BOOKING_DAYS = 90;
    private final Integer CRON_MAX_HOTELS_FETCH = 100;


    @Scheduled(cron = "0 0 0 * * ?")
    public void newDay(){
        System.out.println("============================== cron =================================");
        /* This function assumes that there are no duplicate available dates for each room */


//        LocalDate minBookingDate = testDay;
//        testDay = testDay.plusDays(1);


        LocalDate minBookingDate = LocalDate.now();

        LocalDate maxBookingDate = minBookingDate.plusDays(MAX_BOOKING_DAYS);

        System.out.println("minBookingDate " + minBookingDate);
        System.out.println("maxBookingDate " + maxBookingDate);

        priceRepository.deleteByMaxDate(minBookingDate);


        Integer maxId = hotelRepository.findMaxId();
        for (int hotelStartId=0; hotelStartId<=maxId; hotelStartId += CRON_MAX_HOTELS_FETCH) {
            long start = System.currentTimeMillis();

            int hotelEndId = hotelStartId + CRON_MAX_HOTELS_FETCH;
//            System.out.println(hotelStartId + " " + hotelEndId);


//            System.out.println("reading entities..." + ((System.currentTimeMillis() - start)));
            List<Hotel> hotels = hotelRepository.findByIdGreaterThanEqualAndIdLessThanWithLock(hotelStartId, hotelEndId);
//            Set<Rooms> roomsSet = roomsRepository.findByHotelIdRangeWithLock(hotelStartId, hotelEndId);
            log.info("Cron job - new Day. Processing {} hotels with hotel id between {} and {}",
                    hotels.size(), hotelStartId, hotelEndId-1);
            Map<Integer, Long> hotelVersions = new HashMap<>();

            Set<Dates> datesSet = datesRepository.findByHotelIdRangeWithLock(
                    hotelStartId, hotelEndId);

//            System.out.println("mapping entities..." + ((System.currentTimeMillis() - start)));
//            for (Rooms rooms : roomsSet) {
//                Integer hotelId = rooms.getHotel().getId();
//                List<Rooms> roomsList = roomsMap.getOrDefault(hotelId, new ArrayList<>());
//                roomsList.add(rooms);
//                roomsMap.put(hotelId, roomsList);
//            }

            Map<Long, List<Dates>> datesMap = new HashMap<>();
            for (Dates dates : datesSet) {
                Long roomId = dates.getRoom().getId();
                List<Dates> datesList = datesMap.getOrDefault(roomId, new ArrayList<>());
                datesList.add(dates);
                datesMap.put(roomId, datesList);
            }


            List<Dates> datesToUpdate = new ArrayList<>();
            List<Dates> datesToDelete = new ArrayList<>();

            Map<Long, List<Dates>> datesToUpdateMap = new HashMap<>(); // { roomId : List<Dates> }
            Map<Long, List<Dates>> datesToDeleteMap = new HashMap<>(); // { roomId : List<Dates> }

            List<Price> priceList = new ArrayList<>();

            // Get all dates to update or delete for each hotel
            System.out.println("updating entities..." + ((System.currentTimeMillis() - start)));

            for (Hotel hotel: hotels){
//            for (Map.Entry<Integer, List<Rooms>> entry : roomsMap.entrySet()) {

                for (Rooms rooms : hotel.getRoomsSet()) {
                    if (!rooms.getIsActive()){
                        continue;
                    }
                    Integer roomsId = rooms.getId();

                    /* rooms dates */
                    boolean addEndDate = (
                            rooms.getAvailableUntil() == null
                                    || !rooms.getAvailableUntil().isBefore(maxBookingDate)
                    ) && rooms.getDatesAddedUntil().isBefore(maxBookingDate);
                    LocalDate newRoomsEndDate = (rooms.getAvailableUntil() == null || maxBookingDate.isBefore(rooms.getAvailableUntil())) ?
                            maxBookingDate : rooms.getAvailableUntil();

                    for (Room room : rooms.getRoomSet()) {

                        List<Dates> roomDatesToUpdate = new ArrayList<>();
                        List<Dates> roomDatesToDelete = new ArrayList<>();

                        Long roomId = room.getId();
                        boolean roomAddEndDate = addEndDate;
                        List<Dates> datesList = datesMap.getOrDefault(roomId, new ArrayList<>());

                        for (Dates dates : datesList) {
                            if (dates.getStartDate().isBefore(minBookingDate)) {
                                if (!dates.getEndDate().isAfter(minBookingDate)) {
                                    datesSet.remove(dates);
                                    roomDatesToDelete.add(dates);
                                } else if (dates.getEndDate().isBefore(rooms.getDatesAddedUntil())) {
                                    dates.setStartDate(minBookingDate);
                                    roomDatesToUpdate.add(dates);
                                } else {
                                    dates.setStartDate(minBookingDate);
                                    roomDatesToUpdate.add(dates);
                                    if (roomAddEndDate) {
                                        dates.setEndDate(newRoomsEndDate);
                                        roomAddEndDate = false;
                                    }
                                }
                            } else {
                                if (!dates.getEndDate().isBefore(rooms.getDatesAddedUntil())) {
                                    if (roomAddEndDate) {
                                        dates.setEndDate(newRoomsEndDate);
                                        roomDatesToUpdate.add(dates);
                                        roomAddEndDate = false;
                                    }
                                }
                            }
                        }
                        if (roomAddEndDate) {
                            Dates newDates = Dates.builder()
                                    .room(entityManager.getReference(Room.class, roomId))
                                    .startDate(rooms.getDatesAddedUntil())
                                    .endDate(newRoomsEndDate)
                                    .build();
                            roomDatesToUpdate.add(newDates);
                        }

                        datesToDeleteMap.put(roomId, roomDatesToDelete);
                        datesToUpdateMap.put(roomId, roomDatesToUpdate);

                        datesToDelete.addAll(roomDatesToDelete);
                        datesToUpdate.addAll(roomDatesToUpdate);
                    }
                    /* rooms price */
                    LocalDate endDate = rooms.getAvailableUntil() == null || rooms.getAvailableUntil().isAfter(maxBookingDate) ?
                            maxBookingDate : rooms.getDatesAddedUntil();


                    for (LocalDate date = rooms.getDatesAddedUntil(); date.isBefore(endDate); date = date.plusDays(1)) {
                        Price price = Price.builder()
                                .rooms(entityManager.getReference(Rooms.class, roomsId))
                                .date(date)
                                .priceInCents(priceService.getPrice(rooms.getPriceMin(), rooms.getPriceMax()))
                                .build();
                        priceList.add(price);
                    }

                    rooms.setDatesAddedUntil(newRoomsEndDate);

                }
            }

            System.out.println("updating repository..." + ((System.currentTimeMillis() - start)));
            //  update repository
//            Set<Long> datesIdToKeep = new HashSet<>(datesSet.stream().map(Dates::getId).toList());

            // todo: make this part async
            datesSet.addAll(datesToUpdate);
            List<Dates> updatedDates = datesRepository.saveAll(datesToUpdate);
            datesRepository.deleteAll(datesToDelete);
            List<Price> updatedPrices = priceRepository.saveAll(priceList);



            List<Dates> newDates = datesRepository.findByHotelIdRange(hotelStartId, hotelEndId);
            List<Price> newPrices = priceRepository.findByHotelIdRange(hotelStartId, hotelEndId);
            Map<Long, List<Dates>> newDatesMap = new HashMap<>();
            Map<Integer, List<Price>> newPriceMap = new HashMap<>();
            for (Dates dates: newDates){
                Long roomId = dates.getRoom().getId();
                List<Dates> roomDatesList = newDatesMap.getOrDefault(roomId, new ArrayList<>());
                roomDatesList.add(dates);
                newDatesMap.put(roomId, roomDatesList);
            }

            for (Price price: newPrices){
                Integer roomsId = price.getRooms().getId();
                List<Price> roomPriceList = newPriceMap.getOrDefault(roomsId, new ArrayList<>());
                roomPriceList.add(price);
                newPriceMap.put(roomsId, roomPriceList);
            }


            Map<Integer, DatesUpdateDetails> datesUpdateDetailsMap = new HashMap<>();
            Map<Integer, List<PriceUpdateDetails>> priceUpdateDetailsMap = new HashMap<>();
            for (Hotel hotel: hotels){
                Map<Long, Long> datesVersions = new HashMap<>();
                Map<Long, List<DatesUpdateDetails.DatesDto>> datesDtoMap = new HashMap<>();
                List<PriceUpdateDetails> priceUpdateDetailsList = new ArrayList<>();
                for (Rooms rooms: hotel.getRoomsSet()){
                    for (Room room: rooms.getRoomSet()){
                        List<Dates> roomDatesList = newDatesMap.getOrDefault(room.getId(), null);
                        if (roomDatesList == null){
                            continue;
                        }

                        room.setDatesVersion(room.getDatesVersion()+1);
                        datesVersions.put(room.getId(), room.getDatesVersion());
                        datesDtoMap.put(room.getId(), roomDatesList.stream()
                                        .map(dates -> DatesUpdateDetails.DatesDto.builder()
                                                .Id(dates.getId())
                                                .startDate(dates.getStartDate())
                                                .endDate(dates.getEndDate())
                                                .build())
                                .toList());
                    }

                    List<Price> roomPriceList = newPriceMap.getOrDefault(rooms.getId(), null);
                    rooms.setPriceVersion(rooms.getPriceVersion()+1);
                    priceUpdateDetailsList.add(
                            PriceUpdateDetails.builder()
                                    .hotelId(hotel.getId())
                                    .roomsId(rooms.getId())
                                    .priceVersion(rooms.getPriceVersion())
                                    .priceDtoList(rooms.getPriceList().stream()
                                            .map(price -> PriceUpdateDetails.PriceDto.builder()
                                                    .priceId(price.getId())
                                                    .date(price.getDate())
                                                    .priceInCents(price.getPriceInCents())
                                                    .build())
                                            .toList())
                                    .build()
                    );
                }

                datesUpdateDetailsMap.put(hotel.getId(), DatesUpdateDetails.builder()
                                .hotelId(hotel.getId())
                                .datesVersions(datesVersions)
                                .datesMap(datesDtoMap)
                                .build());
                priceUpdateDetailsMap.put(hotel.getId(), priceUpdateDetailsList);
            }

            hotelRepository.saveAll(hotels);
//
//            // Map entities to generate Kafka message
//            Map<Long, List<Dates>> roomUpdatedDatesMap = new HashMap<>();
//            for (Dates datesAfterUpdate : updatedDates) {
//                Long roomId = datesAfterUpdate.getRoom().getId();
//                List<Dates> roomDatesToUpdate = datesToUpdateMap.getOrDefault(roomId, null);
//                List<Dates> roomUpdatedDates = roomUpdatedDatesMap.getOrDefault(roomId, new ArrayList<>());
//                if (roomDatesToUpdate != null) {
//                    // there will be at most two dates in the roomDatesToUpdate list
//                    for (Dates datesBeforeUpdate : roomDatesToUpdate) {
//                        if (datesBeforeUpdate.getStartDate().isEqual(datesAfterUpdate.getStartDate())) {
//                            roomUpdatedDates.add(datesAfterUpdate);
//                        }
//                    }
//                }
//                roomUpdatedDatesMap.put(roomId, roomUpdatedDates);
//            }
//
//            Map<Integer, List<Price>> roomsUpdatedPriceMap = new HashMap<>();
//            for (Price price: updatedPrices){
//                Integer roomsId = price.getRooms().getId();
//                List<Price> roomsUpdatedPrice = roomsUpdatedPriceMap.getOrDefault(roomsId, new ArrayList<>());
//                roomsUpdatedPrice.add(price);
//                roomsUpdatedPriceMap.put(roomsId, roomsUpdatedPrice);
//            }
//
//            Map<Integer, DatesUpdateDetails> datesUpdateDetailsMap = new HashMap<>();
//            Map<Integer, Map<Integer, List<PriceDto>>> priceUpdateDetailsMap = new HashMap<>();
////            for (Map.Entry<Integer, List<Rooms>> entry : roomsMap.entrySet()) {
//            for (Hotel hotel: hotels){
//                Integer hotelId = hotel.getId();
//
//                // Map dates
//                Map<Long, Map<Long, DatesUpdateDetails.DatesDto>> EsDatesToUpdate = new HashMap<>();
//                Map<Long, Set<Long>> EsDatesToDelete = new HashMap<>();
//                for (Rooms rooms : hotel.getRoomsSet()) {
//                    for (Room room : rooms.getRoomSet()) {
//                        Long roomId = room.getId();
//                        List<Dates> roomUpdatedDates = roomUpdatedDatesMap.getOrDefault(roomId, null);
//                        if (roomUpdatedDates != null) {
//                            Map<Long, DatesUpdateDetails.DatesDto> EsRoomDatesToUpdateMap = new HashMap<>();
//                            for (Dates dates : roomUpdatedDates) {
//                                EsRoomDatesToUpdateMap.put(dates.getId(), DatesUpdateDetails.DatesDto.builder()
//                                        .startDate(dates.getStartDate())
//                                        .endDate(dates.getEndDate())
//                                        .build());
//                            }
//                            EsDatesToUpdate.put(roomId, EsRoomDatesToUpdateMap);
//                        }
//                        List<Dates> roomDeletedDates = datesToDeleteMap.getOrDefault(roomId, null);
//                        if (roomDeletedDates != null) {
//                            EsDatesToDelete.put(roomId, roomDeletedDates.stream().map(Dates::getId).collect(Collectors.toSet()));
//                        }
//                    }
//                }
//                DatesUpdateDetails datesUpdateDetails = datesService.getDatesUpdateDetailsWithOutVersion(hotelId, datesToUpdate, datesToDelete);
//                datesUpdateDetailsMap.put(hotelId, datesUpdateDetails);
////                System.out.println(datesUpdateDetailsMap);
//
//                // Map prices
//                Map<Integer, List<PriceDto>> hotelPriceUpdateDetailsMap = new HashMap<>();
//                for (Rooms rooms: hotel.getRoomsSet()){
//                    Integer roomsId = rooms.getId();
//                    List<Price> roomsUpdatedPrice = roomsUpdatedPriceMap.getOrDefault(roomsId, null);
//                    if (roomsUpdatedPrice != null) {
//                        hotelPriceUpdateDetailsMap.put(roomsId,
//                                roomsUpdatedPrice.stream().map(price -> PriceDto.builder()
//                                        .priceId(price.getId())
//                                        .date(price.getDate())
//                                        .priceInCents(price.getPriceInCents())
//                                        .build()).toList()
//                        );
//                    }
//                }
//                priceUpdateDetailsMap.put(hotelId, hotelPriceUpdateDetailsMap);
//            }


            HotelsNewDayDetails hotelsNewDayDetails = HotelsNewDayDetails.builder()
                    .startId(hotelStartId)
                    .endId(hotelEndId)
                    .datesUpdateDetailsMap(datesUpdateDetailsMap)
                    .priceUpdateDetailsMap(priceUpdateDetailsMap)
                    .build();


            System.out.println("sending message..." + ((System.currentTimeMillis() - start)));
            hotelNewDayKafkaTemplate.send("hotel-new-day", hotelsNewDayDetails);
            System.out.println("sent message..." + ((System.currentTimeMillis() - start)));
        }
    }

//    @Scheduled(cron = "0 0 4 * * ?")
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(fixedRate = 1000000)
    public void noPrepayment() throws InterruptedException {
        log.info("Cron job - processing no prepayments.");
        // partition by hotel id

        LocalDate today = LocalDate.now();


//        TimeUnit.MILLISECONDS.sleep(10000);


        Integer maxId = hotelRepository.findMaxId();
        List<Long> bookingIdsToCancel = new ArrayList<>();
        for (int hotelStartId=0; hotelStartId<=maxId; hotelStartId += CRON_MAX_HOTELS_FETCH) {

            int hotelEndId = hotelStartId + CRON_MAX_HOTELS_FETCH;

            List<Booking> bookingList = bookingRepository
                    .findByNoPrepaymentAndHotelRange(hotelStartId, hotelEndId, today, BookingStatus.RESERVED);
            Set<Rooms> roomsSet = roomsRepository.findByHotelIdRangeWithLock(hotelStartId, hotelEndId);

            Map<Integer, Rooms> roomsMap = new HashMap<>();
            for (Rooms rooms: roomsSet){
                roomsMap.put(rooms.getId(), rooms);
            }

            log.info("Cron job - processing no prepayments. found {} bookings with hotel ids in {} ~ {}",
                    bookingList.size(), hotelStartId, hotelEndId - 1);


            for (Booking booking: bookingList){
                boolean allCancelled = true;
                Long priceInCents = booking.getPriceInCents();
                for (BookingRooms bookingRooms: booking.getBookingRooms()){
                    Rooms rooms = roomsMap.getOrDefault(bookingRooms.getRoomsId(), null);

                    if (bookingRooms.getPrepayUntil() != null && bookingRooms.getPrepayUntil().isBefore(today)){
                        for (BookingRoom bookingRoom : bookingRooms.getBookingRoomList()) {
                            if (bookingRoom.getStatus().equals(BookingStatus.RESERVED)){
                                log.info("Cancelling booking {} - booking room {} (hotel {}, room {}) due to no prepayment.",
                                        booking.getId(), bookingRoom.getId(), booking.getHotelId(), bookingRooms.getRoomsId());
                                bookingRoom.setStatus(BookingStatus.CANCELLED_NO_PREPAYMENT);
                                priceInCents -= bookingRooms.getPricePerRoomInCents();

                                if (rooms != null){
                                    rooms.setDatesReserved(rooms.getDatesReserved() -
                                            (int) ChronoUnit.DAYS.between(
                                                    bookingRoom.getStartDateTime().toLocalDate(),
                                                    booking.getEndDateTime().toLocalDate()));
                                    Long roomId = bookingRoom.getRoomId();
                                    for (Room room: rooms.getRoomSet()){
                                        if (room.getId().equals(roomId)){

                                            datesService.addDates(
                                                    UserRole.ADMIN, null, booking.getHotelId(),
                                                    bookingRoom.getRoomId(), bookingRoom.getStartDateTime().toLocalDate(),
                                                    bookingRoom.getEndDateTime().toLocalDate()
                                            );
//                                            datesToAdd.add(Dates.builder().room(room).startDate(bookingRoom.getStartDateTime().toLocalDate())
//                                                    .endDate(bookingRoom.getEndDateTime().toLocalDate()).build());
                                            break;
                                        }
                                    }
                                }

                            }
                        }
                    } else {
                        if (!allCancelled){
                            for (BookingRoom bookingRoom : bookingRooms.getBookingRoomList()) {
                                if (bookingRoom.getStatus().equals(BookingStatus.RESERVED) || bookingRoom.getStatus().equals(BookingStatus.BOOKED)) {
                                    allCancelled = false;
                                }
                            }
                        }
                    }
                }
                booking.setPriceInCents(priceInCents);
                if (allCancelled){
                    booking.setMainStatus(BookingMainStatus.CANCELLED);
                    booking.setStatus(BookingStatus.CANCELLED_NO_PREPAYMENT);
                    bookingIdsToCancel.add(booking.getId());
                }
            }

//            datesRepository.saveAll(datesToAdd);
            bookingRepository.saveAll(bookingList);
            roomsRepository.saveAll(roomsSet);
            bookingIdArchiveKafkaTemplate.send("booking-archive", BookingIdArchiveRequest.builder()
                    .bookingIds(bookingIdsToCancel).build());


        }
    }


}
