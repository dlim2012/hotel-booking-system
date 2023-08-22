package com.dlim2012.booking.service.booking_entity;

import com.dlim2012.booking.dto.dates.booking.AddBookingRoomRequest;
import com.dlim2012.booking.dto.dates.booking.EditBookingRoomRequest;
import com.dlim2012.booking.dto.internal.DateRange;
import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.booking.service.CacheService;
import com.dlim2012.booking.service.booking_entity.hotel_entity.HotelEntityService;
import com.dlim2012.booking.service.booking_entity.hotel_entity.RoomsEntityService;
import com.dlim2012.booking.service.booking_entity.utils.BookingUtilsService;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.mysql_booking.entity.*;
import com.dlim2012.clients.mysql_booking.repository.*;
import jakarta.persistence.EntityManager;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatesService {

    private final BookingUtilsService bookingUtilsService;
    private final HotelEntityService hotelEntityService;
    private final RoomsEntityService roomsEntityService;
    private final CacheService cacheService;

    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;
    private final DatesRepository datesRepository;
    private final BookingRepository bookingRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final EntityManager entityManager;
    private final KafkaTemplate<String, DatesUpdateDetails> roomsSearchDatesUpdateKafkaTemplate;

    /* This is for new reservation */
    public Map<Integer, List<Long>> _removeDatesSameRange(
            Integer hotelId,
            Map<Integer, Integer> roomNumMap, BookingRequest request,
            Map<Long, Integer> roomsIdMap, Set<Dates> datesSet
    ){

        // For MYSQL
        List<Dates> datesToDelete = new ArrayList<>();
        List<Dates> datesToUpdate = new ArrayList<>();

        // For Kafka - Search
//        Map<Long, Set<Long>> datesIdsToDeleteMap = new HashMap<>();

        // get scores for each date range
        PriorityQueue<DatesScore> priorityQueue = new PriorityQueue<>(
                datesSet.size(), (o1, o2) -> o2.score - o1.score);
        for (Dates dates: datesSet) {
            priorityQueue.add(DatesScore.builder()
                    .dates(dates)
                    .score((dates.getStartDate().isEqual(request.getStartDate()) ? 2 : 0) +
                            (dates.getEndDate().isEqual(request.getEndDate()) ? 1 : 0))
                    .build());
        }

        // get dates based on scores
        Map<Integer, List<Long>> roomsIdToroomIds = new HashMap<>();
        while (!priorityQueue.isEmpty() && !roomNumMap.isEmpty()){
            DatesScore datesScore = priorityQueue.poll();
            Integer roomsId = roomsIdMap.get(datesScore.dates.getRoom().getId());
            Long roomId = datesScore.dates.getRoom().getId();
            Integer roomNum = roomNumMap.getOrDefault(roomsId, 0);
//            System.out.println("datesScore: " + datesScore.toString());
            if (roomNum <= 0){
                continue;
            }
            datesSet.remove(datesScore.dates);
            if (datesScore.score == 3){
                datesToDelete.add(datesScore.dates);
//                addToDetailsMapDelete(roomId, datesScore.dates, datesIdsToDeleteMap);
            } else if (datesScore.score == 2){
                datesScore.dates.setStartDate(request.getEndDate());
                datesToUpdate.add(datesScore.dates);
//                addToDetailsMapUpdate(roomsId, roomId, datesScore.dates, datesToUpdateMap);
            } else if (datesScore.score == 1){
                datesScore.dates.setEndDate(request.getStartDate());
                datesToUpdate.add(datesScore.dates);
//                addToDetailsMapUpdate(roomsId, roomId, datesScore.dates, datesToUpdateMap);
            } else {
                Dates dates = Dates.builder()
                        .room(datesScore.dates.getRoom())
                        .startDate(request.getEndDate())
                        .endDate(datesScore.dates.getEndDate())
                        .build();
                datesScore.dates.setEndDate(request.getStartDate());
                datesToUpdate.add(dates);
                datesToUpdate.add(datesScore.dates);
//                addToDetailsMapUpdate(roomsId, roomId, dates, datesToUpdateMap);
//                System.out.println("datesToUpdateMap 1: " + datesToUpdateMap.toString());
//                addToDetailsMapUpdate(roomsId, roomId, datesScore.dates, datesToUpdateMap);
//                System.out.println("datesToUpdateMap 2: " + datesToUpdateMap.toString());
            }
            roomNumMap.put(roomsId, roomNum-1);
            List<Long> roomIdList = roomsIdToroomIds.getOrDefault(roomsId, new ArrayList<>());
            roomIdList.add(datesScore.dates.getRoom().getId());
            roomsIdToroomIds.put(roomsId, roomIdList);
        }
//        System.out.println("roomsIdToRoomIds");
//        System.out.println(roomsIdToroomIds);

//        System.out.println(datesToAdd);
//        System.out.println(datesToDelete);
//        System.out.println(datesSet);

        // save updated


        updateDates(hotelId, datesSet, datesToUpdate, datesToDelete);


        return roomsIdToroomIds;
    }

    /*
    This is for new cancel booking
    Out of dates will not be added
    *  */
    public void _addDateRanges(Booking booking){

        // todo: validate date range (rooms.availableFrom, rooms.availableUntil, rooms.addedUntil)
        Hotel hotel = hotelEntityService.getHotel(booking.getHotelId());
        Map<Integer, Rooms> roomsMap = new HashMap<>();
        for (Rooms rooms: hotel.getRoomsSet()){
            roomsMap.put(rooms.getId(), rooms);
        }

//        Set<BookingRoom> bookingRoomSet = bookingRoomRepository.findRoomIdsByBookingId(bookingId);
        Integer hotelId = booking.getHotelId();
        LocalDate minStartDate = LocalDate.MAX;
        LocalDate maxEndDate = LocalDate.MIN;

        Set<Long> roomIdSet = new HashSet<>();

        Map<Long, List<DateRange>> dateRangeListMap = new HashMap<>(); // { roomId: list(bookingRoom) }
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            Rooms rooms = roomsMap.getOrDefault(bookingRooms.getRoomsId(), null);
            if (rooms == null){
                continue;
            }
            DateRange roomsDateRange = roomsEntityService.getRoomsAvailableDateRange(rooms);
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                if (bookingRoom.getStatus().equals(BookingStatus.RESERVED)
                        || bookingRoom.getStatus().equals(BookingStatus.BOOKED)
                    || bookingRoom.getStatus().equals(BookingStatus.RESERVED_FOR_TIMEOUT)
                ) {
                    DateRange dateRange = roomsEntityService.adjustToDateRange(
                            bookingRoom.getStartDateTime().toLocalDate(),
                            bookingRoom.getEndDateTime().toLocalDate(),
                            roomsDateRange
                    );
                    if (dateRange == null){
                        continue;
                    }

                    if (dateRange.getStartDate().isBefore(minStartDate)) {
                        minStartDate = dateRange.getStartDate();
                    }
                    if (dateRange.getEndDate().isAfter(maxEndDate)) {
                        maxEndDate = dateRange.getEndDate();
                    }

                    Long roomId = bookingRoom.getRoomId();
                    List<DateRange> dateRangeList = dateRangeListMap.getOrDefault(roomId, new ArrayList<>());
                    dateRangeList.add(dateRange);
                    dateRangeListMap.put(roomId, dateRangeList);
                    roomIdSet.add(roomId);
                }
            }
        }

        Set<Dates> datesSet = datesRepository.findByHotelIdAndIntersectDatesWithLock(
                hotelId,
                minStartDate,
                maxEndDate
        );

        Map<Long, List<Dates>> datesMap = new HashMap<>(); // { roomId: list(dates) }
        for (Dates dates: datesSet){
            Long roomId = dates.getRoom().getId();
            List<Dates> datesList = datesMap.getOrDefault(roomId, new ArrayList<>());
            datesList.add(dates);
            datesMap.put(roomId, datesList);
            roomIdSet.add(roomId);
        }

        // assuming there is no dates overlap
        List<Dates> datesToUpdate = new ArrayList<>();
        List<Dates> datesToKeep = new ArrayList<>();
        List<Dates> datesToDelete = new ArrayList<>();
        Map<Long, Set<Long>> dateIdsToDeleteMap = new HashMap<>();
        for (Long roomId: roomIdSet){
            List<DateRange> dateRangeList = dateRangeListMap.getOrDefault(roomId, new ArrayList<>());
            List<Dates> datesList = datesMap.getOrDefault(roomId, new ArrayList<>());
            Set<Long> dateIdsToDeleteSet = new HashSet<>();

            for (DateRange dateRange: dateRangeList){
                datesList.add(Dates.builder()
                        .id(null)
                        .room(entityManager.getReference(Room.class, roomId))
                        .startDate(dateRange.getStartDate())
                        .endDate(dateRange.getEndDate())
                        .build());
            }

            datesList.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));

            for (int i=0; i<datesList.size(); ){
                int j = i+1;
                Long datesIdToUpdate = null;
                LocalDate endDate = datesList.get(i).getEndDate();
                while (j < datesList.size() && datesList.get(j).getStartDate().isEqual(endDate)) {
                    endDate = datesList.get(j).getEndDate();
                    if (datesIdToUpdate == null){
                        datesIdToUpdate = datesList.get(j).getId();
                    }
                    j += 1;
                }
                if (datesIdToUpdate == null) {
                    Dates dates = datesList.get(i);
                    dates.setEndDate(endDate);
                    datesToUpdate.add(dates);
                } else {
                    if (j == i + 1){
                        datesToKeep.add(datesList.get(i));
                    } else {
                        for (int k = i; k < j; k++) {
                            Dates dates = datesList.get(k);
                            if (dates.getId() == null){
                                continue;
                            }
                            if (dates.getId().equals(datesIdToUpdate)) {
                                dates.setStartDate(datesList.get(i).getStartDate());
                                dates.setEndDate(endDate);
                                datesToUpdate.add(dates);
                            } else {
                                datesToDelete.add(dates);
                                dateIdsToDeleteSet.add(dates.getId());
                            }
                        }
                    }
                }
                i = j;
            }
            if (!dateIdsToDeleteSet.isEmpty()){
                dateIdsToDeleteMap.put(roomId, dateIdsToDeleteSet);
            }
        }


        /*=========================*/
        updateDates(hotelId, datesToKeep, datesToUpdate, datesToDelete);

    }

    public void _addDateRange(Set<Dates> datesSet, List<Dates> datesToDelete, List<Dates> datesToUpdate,
                              Long roomId, LocalDate startDate, LocalDate endDate){

        Rooms rooms = roomsRepository.findByRoomId(roomId);

        DateRange roomsDateRange = roomsEntityService.getRoomsAvailableDateRange(rooms);
        DateRange dateRange = roomsEntityService.adjustToDateRange(
                startDate, endDate, roomsDateRange
        );
        if (dateRange == null){
            throw new IllegalArgumentException("Dates cannot be added due to rooms available dates constraints.");
        }

        startDate = dateRange.getStartDate();
        endDate = dateRange.getEndDate();

        Dates datesBefore = null;
        Dates datesAfter = null;
        for (Dates dates: datesSet){

            if (dates.getEndDate().isAfter(startDate)
                    && dates.getStartDate().isBefore(endDate)
            ){
                throw new IllegalArgumentException("Dates not available.");
            }
            if (dates.getStartDate().isEqual(endDate)){
                datesAfter = dates;
            } else if (dates.getEndDate().isEqual(startDate)){
                datesBefore = dates;
            }
        }


        if (datesBefore != null){
            if (datesAfter != null){
                datesBefore.setEndDate(datesAfter.getEndDate());
                datesToUpdate.add(datesBefore);
                datesToDelete.add(datesAfter);
                datesSet.remove(datesBefore);
                datesSet.remove(datesAfter);
            } else {
                datesBefore.setEndDate(endDate);
                datesToUpdate.add(datesBefore);
                datesSet.remove(datesBefore);
            }
        } else if (datesAfter != null){
            datesAfter.setStartDate(startDate);
            datesToUpdate.add(datesAfter);
            datesSet.remove(datesAfter);
        } else {
            datesToUpdate.add(Dates.builder()
                    .room(entityManager.getReference(Room.class, roomId))
                    .startDate(startDate)
                    .endDate(endDate)
                    .build());
        }
    }

    public void _removeDatesRange(Set<Dates> datesSet, List<Dates> datesToDelete, List<Dates> datesToUpdate,
                                  Long roomId, LocalDate startDate, LocalDate endDate){

        for (Dates dates: datesSet){
            if (!dates.getStartDate().isAfter(startDate) &&
                    !dates.getEndDate().isBefore(endDate)
            ){
                if (dates.getStartDate().isEqual(startDate)){
                    if (dates.getEndDate().isEqual(endDate)){
                        datesSet.remove(dates);
                        datesToDelete.add(dates);
                    } else {
                        dates.setStartDate(endDate);
                        datesToUpdate.add(dates);
                        datesSet.remove(dates);
                    }
                } else if (dates.getEndDate().isEqual(endDate)){
                    dates.setEndDate(startDate);
                    datesToUpdate.add(dates);
                    datesSet.remove(dates);
                } else {
                    Dates newDates = Dates.builder()
                            .room(entityManager.getReference(Room.class, roomId))
                            .startDate(dates.getStartDate())
                            .endDate(startDate)
                            .build();
                    dates.setStartDate(endDate);
                    datesSet.remove(dates);
                    datesToUpdate.add(dates);
                    datesToUpdate.add(newDates);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Dates to remove are not available");

    }

    public void addDates(
            UserRole userRole, Integer userId,
            Integer hotelId, Long roomId,
            LocalDate startDate, LocalDate endDate){
        Set<Dates> datesSet = null;
        if (userRole.equals(UserRole.HOTEL_MANAGER)){
            datesSet = datesRepository.findByRoomIdAndHotelManagerIdWithLock(roomId, userId);
        } else if (userRole.equals(UserRole.APP_USER)) {
            throw new RuntimeException();
        } else if (userRole.equals(UserRole.ADMIN)){
            datesSet = datesRepository.findByRoomIdWithLock(roomId);
        } else {
            throw new IllegalArgumentException("");
        }

        List<Dates> datesToDelete = new ArrayList<>();
        List<Dates> datesToUpdate = new ArrayList<>();
        _addDateRange(datesSet, datesToDelete, datesToUpdate, roomId, startDate, endDate);
        updateDates(hotelId, datesSet, datesToUpdate, datesToDelete);
    }

    public void editDates(
            UserRole userRole, Integer userId,
            Integer hotelId, Long roomId, Long datesId,
            LocalDate startDate, LocalDate endDate
            ) {

        Set<Dates> datesSet;
        if (userRole.equals(UserRole.HOTEL_MANAGER)){
            datesSet = datesRepository.findByRoomIdAndHotelManagerIdWithLock(roomId, userId);
        } else if (userRole.equals(UserRole.APP_USER)){
            throw new RuntimeException();
        } else {
            throw new IllegalArgumentException("");
        }

        Dates datesToEdit = null;

        // find the 'dates' to modify
        for (Dates dates: datesSet){
            if (dates.getId().equals(datesId)){
                datesToEdit = dates;
                break;
            }
        }
        if (datesToEdit == null){
            // release lock
            datesRepository.saveAll(datesSet);
            throw new ResourceNotFoundException("Dates not found.");
        }

        // edit datesSet
        List<Dates> datesToDelete = new ArrayList<>();
        List<Dates> datesToUpdate = new ArrayList<>();
        datesSet.remove(datesToEdit);
        datesToDelete.add(datesToEdit);
        _addDateRange(datesSet, datesToDelete, datesToUpdate, roomId, startDate, endDate);

        // save results and release lock
        updateDates(hotelId, datesSet, datesToUpdate, datesToDelete);
    }

    public void deleteDate(
            UserRole userRole, Integer userId,
            Integer hotelId, Long datesId) {
        if (userRole.equals(UserRole.HOTEL_MANAGER)){
            Integer deleted = datesRepository.deleteByIDAndHotelManagerId(datesId, userId);
            if (deleted == 0){
                throw new ResourceNotFoundException("Dates not found.");
            }
        } else {
            throw new RuntimeException("Not implemented.");
        }

        // send results to Kafka
        updateDates(hotelId, new ArrayList<>(), new ArrayList<>(), List.of(Dates.builder().id(datesId).build()));
//        DatesUpdateDetails datesUpdateDetails = DatesUpdateDetails.builder()
//                .hotelId(hotelId)
//                .hotelVersion(hotelEntityService.getNewHotelVersion(hotelId))
////                .roomsVersions(roomsEntityService.getRoomsVersions(hotelId))
//                .datesToUpdate(new HashMap<>())
//                .datesIdsToDelete(getDatesIdsToDelete(List.of(Dates.builder().id(datesId).build())))
//                .build();
//        roomsSearchDatesUpdateKafkaTemplate.send("rooms-search-dates-update", datesUpdateDetails);
    }



    public void addBookingRoom(
            UserRole userRole, Integer userId,
            Integer hotelId, Long bookingId,
            AddBookingRoomRequest request
    ) {


        Set<Dates> datesSet = null;
        Booking booking = null;
        BookingRooms bookingRooms = null;
        if (userRole.equals(UserRole.HOTEL_MANAGER)){
            booking = bookingRepository.findByIdAndHotelManagerIdWithLock(bookingId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
            boolean bookingRoomsExists = false;
            for (BookingRooms bookingRooms1: booking.getBookingRooms()){
                if (bookingRooms1.getId().equals(request.getBookingRoomsId())){
                    bookingRooms = bookingRooms1;
                    break;
                }
            }
            if (bookingRooms == null){
                throw new ResourceNotFoundException("Booking Rooms not found.");
            }

            datesSet = datesRepository.findByRoomIdAndHotelManagerIdWithLock(request.getRoomId(), userId);
        } else {
            throw new RuntimeException("Not implemented.");
        }


        List<Dates> datesToDelete = new ArrayList<>();
        List<Dates> datesToUpdate = new ArrayList<>();
        _removeDatesRange(datesSet, datesToDelete, datesToUpdate,request.getRoomId(), request.getStartDate(), request.getEndDate());


        Integer checkInTimeHour = Integer.valueOf((request.getCheckInTime().substring(0, 2)));
        Integer checkInTimeMinute = Integer.valueOf((request.getCheckInTime().substring(3, 5)));
        Integer checkOutTimeHour = Integer.valueOf((request.getCheckOutTime().substring(0, 2)));
        Integer checkOutTimeMinute = Integer.valueOf((request.getCheckOutTime().substring(3, 5)));
        BookingRoom bookingRoom = BookingRoom.builder()
                .bookingRooms(entityManager.getReference(BookingRooms.class, request.getBookingRoomsId()))
                .roomId(request.getRoomId())
                .startDateTime(request.getStartDate().atTime(checkInTimeHour, checkInTimeMinute))
                .endDateTime(request.getEndDate().atTime(checkOutTimeHour, checkOutTimeMinute))
                .status(request.getPayed() ? BookingStatus.BOOKED : BookingStatus.RESERVED)
                .build();

        // save booking Room
        bookingRooms.getBookingRoomList().add(bookingRoom);

        // save results and release lock
        updateDates(hotelId, datesSet, datesToUpdate, datesToDelete);

        cacheService.putBooking(booking);
        bookingUtilsService.recalculateBookingPriceTimeAndSave(booking);
    }

    public void editBookingRoom(
            UserRole userRole, Integer userId,
            Integer hotelId,
            EditBookingRoomRequest request
    ) {
        Booking booking = null;
        BookingRoom bookingRoom = null;
        if (userRole.equals(UserRole.HOTEL_MANAGER)){
            booking = bookingRepository.findByIdAndHotelManagerIdWithLock(request.getBookingId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));

            for (BookingRooms bookingRooms: booking.getBookingRooms()){
                for (BookingRoom bookingRoom1: bookingRooms.getBookingRoomList()){
                    if (bookingRoom1.getId() == request.getBookingRoomId()){
                        bookingRoom = bookingRoom1;
                        break;
                    }
                }
            }
            if (bookingRoom == null){
                throw new ResourceNotFoundException("Booking room not found.");
            }
//            bookingRoom = bookingRoomRepository.findByIdAndHotelManagerId(
//                    request.getBookingRoomId(), userId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Booking room not found."));
        } else {
            throw new RuntimeException("Not implmented.");
        }


        if (request.getRoomId().equals(bookingRoom.getRoomId())
//            && request.getStartDate().isBefore(bookingRoom.getEndDateTime().toLocalDate())
//                && request.getEndDate().isAfter(bookingRoom.getStartDateTime().toLocalDate())
        ){

            Set<Dates> datesSet = null;
            if (userRole.equals(UserRole.HOTEL_MANAGER)){
                datesSet = datesRepository.findByRoomIdAndHotelManagerIdWithLock(request.getRoomId(), userId);
            } else {
                throw new RuntimeException("Not implmented.");
            }
            List<Dates> datesList = new ArrayList<>(datesSet);
            datesList.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
            Dates datesBefore = null;
            Dates datesAfter = null;

            List<Dates> datesToDelete = new ArrayList<>();
            Set<Dates> datesToUpdate = new HashSet<>();

            /* Add date range */
            for (Dates dates: datesSet){
                if (dates.getEndDate().isEqual(bookingRoom.getStartDateTime().toLocalDate())){
                    datesBefore = dates;
                } else if (dates.getStartDate().isEqual(bookingRoom.getEndDateTime().toLocalDate())){
                    datesAfter = dates;
                }
            }

            if (datesBefore != null){
                if (datesAfter != null){
                    datesBefore.setEndDate(datesAfter.getEndDate());
                    datesToDelete.add(datesAfter);
                    datesSet.remove(datesAfter);
                    datesToUpdate.add(datesBefore);
                } else {
                    datesBefore.setEndDate(bookingRoom.getEndDateTime().toLocalDate());
                    datesToUpdate.add(datesBefore);
                }
            } else if (datesAfter != null){
                datesAfter.setStartDate(bookingRoom.getStartDateTime().toLocalDate());
                datesToUpdate.add(datesAfter);
            } else {
                Dates newDates = Dates.builder()
                        .room(entityManager.getReference(Room.class, bookingRoom.getRoomId()))
                        .startDate(bookingRoom.getStartDateTime().toLocalDate())
                        .endDate(bookingRoom.getEndDateTime().toLocalDate())
                        .build();
                datesToUpdate.add(newDates);
            }


            /* Remove date range */
            Dates datesToModify = null;
            for (Dates dates: datesSet){
                if (!dates.getStartDate().isAfter(request.getStartDate()) && !dates.getEndDate().isBefore(request.getEndDate())){
                    datesToModify = dates;
                    break;
                }
            }

            if (datesToModify.getStartDate().isEqual(request.getStartDate())){
                if (datesToModify.getEndDate().isEqual(request.getEndDate())){
                    datesToDelete.add(datesToModify);
                    datesSet.remove(datesToModify);
                } else {
                    datesToModify.setStartDate(request.getEndDate());
                    datesToUpdate.add(datesToModify);
                }
            } else if (datesToModify.getEndDate().isEqual(request.getEndDate())){
                datesToModify.setEndDate(request.getStartDate());
                datesToUpdate.add(datesToModify);
            } else {
                Dates newDates = Dates.builder()
                        .room(entityManager.getReference(Room.class, bookingRoom.getRoomId()))
                        .startDate(request.getEndDate())
                        .endDate(datesToModify.getEndDate())
                        .build();
                datesToModify.setEndDate(request.getStartDate());
                datesToUpdate.add(datesToModify);
                datesToUpdate.add(newDates);
            }

            for (Dates dates: datesToUpdate){
                datesSet.remove(dates);
            }


            /*=========================*/
            updateDates(hotelId, datesSet, new ArrayList<>(datesToUpdate), datesToDelete);


        } else {

            Set<Dates> datesSet1 = null;
            Set<Dates> datesSet2 = null;
            if (userRole.equals(UserRole.HOTEL_MANAGER)){
                // todo: merge these two calls
                datesSet1 = datesRepository.findByRoomIdAndHotelManagerIdWithLock(request.getRoomId(), userId);
                datesSet2 = datesRepository.findByRoomIdAndHotelManagerIdWithLock(bookingRoom.getRoomId(), userId);
            } else {
                throw new RuntimeException("Not implmented");
            }

            /* 1) remove timeslot availability for booking */
            List<Dates> datesToDelete1 = new ArrayList<>();
            List<Dates> datesToUpdate1 = new ArrayList<>();
            _removeDatesRange(datesSet1, datesToDelete1, datesToUpdate1, request.getRoomId(), request.getStartDate(), request.getEndDate());


            /* 2) add time availability  */
            List<Dates> datesToDelete2 = new ArrayList<>();
            List<Dates> datesToUpdate2 = new ArrayList<>();
            _addDateRange(datesSet2, datesToDelete2, datesToUpdate2,
                    bookingRoom.getRoomId(), bookingRoom.getStartDateTime().toLocalDate(), bookingRoom.getEndDateTime().toLocalDate());

            /* 3) update dates */
            List<Dates> datesList = new ArrayList<>();
            datesList.addAll(datesSet1);
            datesList.addAll(datesSet2);
            datesToUpdate1.addAll(datesToUpdate2);
            datesToDelete1.addAll(datesToDelete2);
            updateDates(hotelId, datesList, datesToUpdate1, datesToDelete1);
        }

        /* Edit booking room */
        Integer checkInTimeHour = Integer.valueOf((request.getCheckInTime().substring(0, 2)));
        Integer checkInTimeMinute = Integer.valueOf((request.getCheckInTime().substring(3, 5)));
        Integer checkOutTimeHour = Integer.valueOf((request.getCheckOutTime().substring(0, 2)));
        Integer checkOutTimeMinute = Integer.valueOf((request.getCheckOutTime().substring(3, 5)));
        bookingRoom.setRoomId(request.getRoomId());
        bookingRoom.setStartDateTime(request.getStartDate().atTime(checkInTimeHour, checkInTimeMinute));
        bookingRoom.setEndDateTime(request.getEndDate().atTime(checkOutTimeHour, checkOutTimeMinute));

        bookingRepository.save(booking);

        cacheService.putBooking(booking);
    }

    public boolean cancelBookingRoom(
            UserRole userRole, Integer userId,
            Long bookingId, Long bookingRoomId) {

        Booking booking = null;
        BookingStatus newBookingStatus;
        if (userRole.equals(UserRole.HOTEL_MANAGER)){
            booking = bookingRepository.findByIdAndHotelManagerId(bookingId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
            newBookingStatus = BookingStatus.CANCELLED_BY_HOTEL_MANAGER;
        } else if (userRole.equals(UserRole.APP_USER)) {
            booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
            newBookingStatus = BookingStatus.CANCELLED_BY_APP_USER;
        }else {
            throw new RuntimeException("Not implemented.");
        }

        // find bookingRoom
        BookingRooms bookingRooms = null;
        BookingRoom bookingRoom = null;
        for (BookingRooms bookingRooms1: booking.getBookingRooms()){
            for (BookingRoom bookingRoom1: bookingRooms1.getBookingRoomList()){
                if (bookingRoom1.getId().equals(bookingRoomId)){
                    bookingRooms = bookingRooms1;
                    bookingRoom = bookingRoom1;
                    break;
                }
            }
            if (bookingRoom != null){
                break;
            }
        }
        if (bookingRoom == null){
            throw new ResourceNotFoundException("Booking room not found.");
        }

        /* add time availability  */
        try {
            Set<Dates> datesSet = datesRepository.findByRoomIdWithLock(
                    bookingRoom.getRoomId());
            List<Dates> datesToDelete = new ArrayList<>();
            List<Dates> datesToUpdate = new ArrayList<>();
            _addDateRange(datesSet, datesToDelete, datesToUpdate,
                    bookingRoom.getRoomId(), bookingRoom.getStartDateTime().toLocalDate(), bookingRoom.getEndDateTime().toLocalDate());

            updateDates(booking.getHotelId(), datesSet, datesToUpdate, datesToDelete);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        bookingRoom.setStatus(newBookingStatus);

        if (!bookingUtilsService.bookingIsActive(booking)){
            booking.setMainStatus(BookingMainStatus.CANCELLED);
            booking.setStatus(newBookingStatus);
        }

        cacheService.putBooking(booking);
        bookingUtilsService.recalculateBookingPriceTimeAndSave(booking);


        return booking.getMainStatus().equals(BookingMainStatus.CANCELLED);
    }



    public DatesUpdateDetails getDatesUpdateDetailsWithOutVersion(
            Integer hotelId,
            List<Dates> updatedDates,
            List<Dates> deletedDates
    ){


        Set<Long> roomIds = new HashSet<>();
        for (Dates dates: updatedDates){
            roomIds.add(dates.getRoom().getId());
        }
        for (Dates dates: deletedDates){
            roomIds.add(dates.getRoom().getId());
        }

        List<Dates> datesList = datesRepository.findByRoomIds(roomIds);


        Hotel hotel = hotelRepository.findByIdWithLock(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));

        Map<Long, Long> datesVersions = new HashMap<>();
        for (Rooms rooms: hotel.getRoomsSet()){
            for (Room room: rooms.getRoomSet()){
                if (roomIds.contains(room.getId())){
                    room.setDatesVersion(room.getDatesVersion()+1);
                    datesVersions.put(room.getId(), room.getDatesVersion());
                }
            }
        }

        Map<Long, List<DatesUpdateDetails.DatesDto>> datesMap = new HashMap<>();
        for (Dates dates: datesList){
            Long roomId = dates.getRoom().getId();
            List<DatesUpdateDetails.DatesDto> roomDatesList = datesMap.getOrDefault(roomId, new ArrayList<>());
            roomDatesList.add(DatesUpdateDetails.DatesDto.builder()
                    .Id(dates.getId())
                    .startDate(dates.getStartDate())
                    .endDate(dates.getEndDate())
                    .build());
            datesMap.put(dates.getRoom().getId(), roomDatesList);
        }

        return DatesUpdateDetails.builder()
                .hotelId(hotelId)
                .datesVersions(datesVersions)
                .datesMap(datesMap)
                .build();

    }

    public void updateDates(
            Integer hotelId,
            List<Dates> datesToKeep,
            List<Dates> datesToUpdate,
            List<Dates> datesToDelete
    ){
        Set<Long> datesIdToKeep = new HashSet<>(datesToKeep.stream().map(Dates::getId).toList());
        datesToKeep.addAll(datesToUpdate);

        List<Dates> savedDates = datesRepository.saveAll(datesToKeep);
        datesRepository.deleteAll(datesToDelete);

        List<Dates> updatedDates = new ArrayList<>();
        for (Dates dates: savedDates){
            if (!datesIdToKeep.contains(dates.getId())){
                updatedDates.add(dates);
            }
        }

        DatesUpdateDetails datesUpdateDetails = getDatesUpdateDetailsWithOutVersion(
                hotelId,
                updatedDates,
                datesToDelete
        );

        roomsSearchDatesUpdateKafkaTemplate.send("rooms-search-dates-update", datesUpdateDetails);


//        DatesUpdateDetails datesUpdateDetails = DatesUpdateDetails.builder()
//                .hotelId(hotelId)
//                .hotelVersion(hotelEntityService.getNewHotelVersion(hotelId))
////                .roomsVersions(roomsEntityService.getRoomsVersions(hotelId))
//                .datesToUpdate(getDatesToUpdate(updatedDates))
//                .datesIdsToDelete(getDatesIdsToDelete(datesToDelete))
//                .build();
//        roomsSearchDatesUpdateKafkaTemplate.send("rooms-search-dates-update", datesUpdateDetails);
    }


    public void updateDates(
            Integer hotelId,
            Set<Dates> datesSet,
            List<Dates> datesToUpdate,
            List<Dates> datesToDelete
    ){
        updateDates(hotelId, new ArrayList<>(datesSet), datesToUpdate, datesToDelete);
    }

//    void addToDetailsMapUpdate(
//            Integer roomsId, Long roomId, Dates dates,
//            Map<Integer, Map<Long, Map<Long, DatesUpdateDetails.DatesDto>>> datesToUpdate){
//
//        Map<Long, Map<Long, DatesUpdateDetails.DatesDto>> datesDtoRoomsMap = datesToUpdate.getOrDefault(roomsId, new HashMap<>());
//        Map<Long, DatesUpdateDetails.DatesDto> datesDtoRoomMap = datesDtoRoomsMap.getOrDefault(roomId, new HashMap<>());
//        datesDtoRoomMap.put(roomId, DatesUpdateDetails.DatesDto.builder()
//                .startDate(dates.getStartDate())
//                .endDate(dates.getEndDate())
//                .build());
//        datesDtoRoomsMap.put(roomId, datesDtoRoomMap);
//        datesToUpdate.put(roomsId, datesDtoRoomsMap);
//    }

//    Map<Long, Map<Long, DatesUpdateDetails.DatesDto>> getDatesToUpdate(List<Dates> datesUpdated){
//        Map<Long, Map<Long, DatesUpdateDetails.DatesDto>> datesToUpdate = new HashMap<>();
//        for (Dates dates: datesUpdated){
//            Long roomId = dates.getRoom().getId();
//            Map<Long, DatesUpdateDetails.DatesDto> datesDtoMap = datesToUpdate.getOrDefault(roomId, new HashMap<>());
//            datesDtoMap.put(dates.getId(), DatesUpdateDetails.DatesDto.builder()
//                    .startDate(dates.getStartDate())
//                    .endDate(dates.getEndDate())
//                    .build());
//            datesToUpdate.put(roomId, datesDtoMap);
//        }
//        return datesToUpdate;
//    }

//    Map<Long, Set<Long>> getDatesIdsToDelete(List<Dates> deletedDatesList){
//        Map<Long, Set<Long>> datesIdsToDelete = new HashMap<>();
//        for (Dates dates: deletedDatesList){
//            Long roomId = dates.getRoom().getId();
//            Set<Long> datesIdsSet = datesIdsToDelete.getOrDefault(roomId, new HashSet<>());
//            datesIdsSet.add(dates.getId());
//            datesIdsToDelete.put(roomId, datesIdsSet);
//        }
//        return datesIdsToDelete;
//    }
//
//    void addToDetailsMapDelete(
//            Long roomId, Dates dates,
//            Map<Long, Set<Long>> datesIdsToDelete
//    ){
//        Set<Long> datesIdSet = datesIdsToDelete.getOrDefault(roomId, new HashSet<>());
//        datesIdSet.add(dates.getId());
//        datesIdsToDelete.put(roomId, datesIdSet);
//    }



    @Data
    @Builder
    static class DatesScore{
        Dates dates;
        Integer score;
    }

    @Data
    @Builder
    static class DatesPair{
        Dates datesBefore;
        Dates datesAfter;
        Integer size = 0;
    }


}
