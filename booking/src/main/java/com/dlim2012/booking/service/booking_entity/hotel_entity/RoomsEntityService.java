package com.dlim2012.booking.service.booking_entity.hotel_entity;

import com.dlim2012.booking.dto.internal.DateRange;
import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.booking.service.booking_entity.PriceService;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDetails;
import com.dlim2012.clients.mysql_booking.entity.*;
import com.dlim2012.clients.mysql_booking.repository.*;
import jakarta.persistence.EntityManager;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomsEntityService {

    private final PriceService priceService;

    private final RoomsRepository roomsRepository;
    private final RoomRepository roomRepository;
    private final DatesRepository datesRepository;
    private final PriceRepository priceRepository;

    private final KafkaTemplate<String, RoomsSearchDetails> roomsSearchKafkaTemplate;
    private final KafkaTemplate<String, RoomsSearchDeleteRequest> roomsSearchDeleteKafkaTemplate;

    private final Integer MAX_BOOKING_DAYS = 30;
    private final ModelMapper modelMapper = new ModelMapper();
    private final EntityManager entityManager;


    public void _registerRooms(RoomsBookingDetails details){
        if (details.getQuantity()<=0){
            return;
        }
        // get start and end dates
        LocalDate minBookingDate = LocalDate.now();
        LocalDate startDate = details.getAvailableFrom().isAfter(minBookingDate) ? details.getAvailableFrom() : minBookingDate;
        LocalDate maxBookingDate = minBookingDate.plusDays(MAX_BOOKING_DAYS);
        LocalDate endDate =
                details.getAvailableUntil() == null || details.getAvailableUntil().isAfter(maxBookingDate) ?
                        maxBookingDate : details.getAvailableUntil();

        // Save 'rooms' table row
        Rooms rooms = Rooms.builder()
                .id(details.getRoomsId())
                .hotel(entityManager.getReference(Hotel.class, details.getHotelId()))
                .quantity(details.getQuantity())
                .displayName(details.getDisplayName())
                .shortName(details.getShortName())
                .priceMin(details.getPriceMin())
                .priceMax(details.getPriceMax())
                .datesAddedUntil(endDate)
                .checkInTime(details.getCheckInTime())
                .checkOutTime(details.getCheckOutTime())
                .availableFrom(details.getAvailableFrom())
                .availableUntil(details.getAvailableUntil())
                .freeCancellationDays(details.getFreeCancellationDays())
                .noPrepaymentDays(details.getNoPrepaymentDays())
                .datesReserved(0)
                .datesBooked(0)
                .build();
        roomsRepository.save(rooms);


        // Save 'room' and 'dates' rows
        List<Room> roomList = new ArrayList<>();
        for (int i=0; i<details.getQuantity(); i++){
            Room room = Room.builder()
                    .rooms(entityManager.getReference(Rooms.class, details.getRoomsId()))
                    .roomNumber(i+1)
                    .build();
            roomList.add(room);
        }
        roomList = roomRepository.saveAll(roomList);

        // save 'dates' table rows
        List<Dates> datesList = new ArrayList<>();
        for (int i=0; i<details.getQuantity(); i++){
            Dates dates = Dates.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .room(roomList.get(i))
                    .build();
            datesList.add(dates);
        }
        datesRepository.saveAll(datesList);

        // save 'price' table rows
        priceRepository.saveAll(priceService.getDefaultPrice(details.getRoomsId(),
                startDate, endDate, details.getPriceMin(), details.getPriceMax(), MAX_BOOKING_DAYS));

    }

    public Boolean __updateRoomsInfo(RoomsBookingDetails details, List<Room> roomArrayList,
                                     List<Room> remainingRoomList, RoomsData prevRooms, LocalDate addedUntil){
        Optional<Rooms> optionalRooms = roomsRepository.findById(details.getRoomsId());
        if (optionalRooms.isEmpty()){
            return false;
        }
        Rooms rooms = optionalRooms.get();

        prevRooms.setAvailableFrom(rooms.getAvailableFrom());
        prevRooms.setAvailableUntil(rooms.getAvailableUntil());
        prevRooms.setPriceMax(rooms.getPriceMax());
        prevRooms.setPriceMin(rooms.getPriceMin());
        prevRooms.setQuantity(rooms.getQuantity());;

        rooms.setId(details.getRoomsId());
        rooms.setHotel(entityManager.getReference(Hotel.class, details.getHotelId()));
        rooms.setQuantity(details.getQuantity());
        rooms.setDisplayName(details.getDisplayName());
        rooms.setShortName(details.getShortName());
        rooms.setPriceMin(details.getPriceMin());
        rooms.setPriceMax(details.getPriceMax());
        rooms.setCheckInTime(details.getCheckInTime());
        rooms.setCheckOutTime(details.getCheckOutTime());
        rooms.setAvailableFrom(details.getAvailableFrom());
        rooms.setAvailableUntil(details.getAvailableUntil());
        rooms.setDatesAddedUntil(addedUntil);

        // gather rooms to delete
        if (prevRooms.getQuantity() > details.getQuantity()) {
            int diff = prevRooms.getQuantity() - details.getQuantity();
            List<Room> roomList = new ArrayList<>(rooms.getRoomSet());
            roomList.sort((o1, o2) -> (int) (-o1.getRoomNumber() + o2.getRoomNumber()));
            for (int i=0; i<diff; i++){
                roomArrayList.add(roomList.get(i));
            }
            for (int i=diff; i< prevRooms.quantity; i++){
                remainingRoomList.add(roomList.get(i));
            }
        } else {
            for (int i=prevRooms.getQuantity(); i<details.getQuantity(); i++){
                Room newRoom = Room.builder()
                        .rooms(rooms)
                        .roomNumber(i+1)
                        .datesSet(new HashSet<>())
                        .build();
                roomArrayList.add(newRoom);
            }

            remainingRoomList.addAll(rooms.getRoomSet());
        }

        roomsRepository.save(rooms);
        return true;
    }

    public void __updateRoomsDates(
            RoomsBookingDetails details,
            List<Room> remainingRoomList,
            LocalDate prevStartDate,
            LocalDate prevEndDate,
            LocalDate startDate,
            LocalDate endDate
    ){
        List<Dates> datesToAdd = new ArrayList<>();
        List<Dates> datesToDelete = new ArrayList<>();

        Set<Dates> datesSet = datesRepository.findByRoomsIdWithLock(details.getRoomsId());

        Map<Long, List<Dates>> datesRoomMap = new HashMap<>();
        for (Dates dates: datesSet){
            Long roomId = dates.getRoom().getId();
            List<Dates> datesList = datesRoomMap.getOrDefault(roomId, new ArrayList<>());
            datesList.add(dates);
            datesRoomMap.put(roomId, datesList);
        }

        for (Room room: remainingRoomList){
            Long roomId = room.getId();
            List<Dates> datesList = datesRoomMap.getOrDefault(roomId, null);
            if (datesList == null){
                if (startDate.isBefore(prevStartDate)){
                    datesToAdd.add(Dates.builder().room(room).startDate(startDate).endDate(prevStartDate).build());
                }
                if (endDate.isAfter(prevEndDate)){
                    datesToAdd.add(Dates.builder().room(room).startDate(prevEndDate).endDate(endDate).build());
                }
            } else {
                datesList.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
                if (startDate.isBefore(prevStartDate)){
                    if (datesList.get(0).getStartDate().isEqual(prevStartDate)){
                        datesList.get(0).setStartDate(startDate);
                    } else {
                        datesToAdd.add(Dates.builder().room(room).startDate(startDate).endDate(prevStartDate).build());
                    }
                }
                if (endDate.isAfter(prevEndDate)){
                    if (datesList.get(datesList.size()-1).getEndDate().isEqual(prevEndDate)){
                        datesList.get(datesList.size()-1).setEndDate(endDate);
                    } else {
                        datesToAdd.add(Dates.builder().room(room).startDate(prevEndDate).endDate(endDate).build());
                    }
                }
                for (Dates dates: datesSet){
                    if (!dates.getEndDate().isAfter(startDate)){
                        datesToDelete.add(dates);
                        continue;
                    }
                    if (!dates.getStartDate().isBefore(endDate)){
                        datesToDelete.add(dates);
                        continue;
                    }

                    if (dates.getStartDate().isBefore(startDate)){
                        dates.setStartDate(startDate);
                    }
                    if (dates.getEndDate().isAfter(endDate)){
                        dates.setEndDate(endDate);
                    }
                }
            }
        }
        datesSet.addAll(datesToAdd);
        datesRepository.saveAll(datesSet);
        datesRepository.deleteAll(datesToDelete);
    }

    public void _updateRooms(
            RoomsBookingDetails details
    ){
        List<Room> roomArrayList = new ArrayList<>(); // delete or add depending on quantity change
        List<Room> remainingRoomList = new ArrayList<>();
        RoomsData prevRooms = RoomsData.builder().build();


        // get start and end dates
        LocalDate minBookingDate = LocalDate.now();
        LocalDate maxBookingDate = minBookingDate.plusDays(MAX_BOOKING_DAYS);

        LocalDate startDate = details.getAvailableFrom().isAfter(minBookingDate) ? details.getAvailableFrom() : minBookingDate;
        LocalDate endDate = details.getAvailableUntil() == null || details.getAvailableUntil().isAfter(maxBookingDate) ?
                maxBookingDate : details.getAvailableUntil();

        /* 1) save room information */
        if (!__updateRoomsInfo(details, roomArrayList, remainingRoomList, prevRooms, endDate)){
            _registerRooms(details);
            return;
        }


        LocalDate prevStartDate = prevRooms.getAvailableFrom().isAfter(minBookingDate) ? prevRooms.getAvailableFrom() : minBookingDate;
        LocalDate prevEndDate = prevRooms.getAvailableUntil() == null || prevRooms.getAvailableUntil().isAfter(maxBookingDate) ?
                maxBookingDate : prevRooms.getAvailableUntil();



        /* adjust room: remove */
        if (prevRooms.getQuantity() > details.getQuantity()){
            roomRepository.deleteAll(roomArrayList);
        }

        /* 3) reset date ranges while considering booking */
        __updateRoomsDates(details, remainingRoomList, prevStartDate, prevEndDate, startDate, endDate);


        /* adjust room: add */
        if (prevRooms.getQuantity() < details.getQuantity()){
            roomRepository.saveAll(roomArrayList);
        }


        /* 4) update price */
        priceService.updatePriceNewDateRange(
                details.getRoomsId(), details.getPriceMin(), details.getPriceMax(),
                prevStartDate, prevEndDate, startDate, endDate
        );
    }


    public void updateRooms(RoomsBookingDetails details) {
        // todo: input validation (startDate < endDate)

        _updateRooms(details);

        Rooms rooms = roomsRepository.findById(details.getRoomsId())
                .orElseThrow(() -> new RuntimeException("Rooms not found."));
        List<Price> priceList = priceRepository.findByRoomsId(details.getRoomsId());
        List<Dates> datesList = datesRepository.findByRoomsId(details.getRoomsId());

        Map<Long, List<Dates>> datesMap = new HashMap<>();
        for (Dates dates: datesList){
            Long roomId = dates.getRoom().getId();
            List<Dates> roomDatesList = datesMap.getOrDefault(roomId, new ArrayList<>());
            roomDatesList.add(dates);
            datesMap.put(roomId, roomDatesList);
        }


        RoomsSearchDetails roomsSearchDetails = RoomsSearchDetails.builder()
                .roomsId(details.getRoomsId())
                .hotelId(details.getHotelId())
                .displayName(details.getDisplayName())
                .maxAdult(details.getMaxAdult())
                .maxChild(details.getMaxChild())
                .quantity(details.getQuantity())
                .priceMin(details.getPriceMin())
                .priceMax(details.getPriceMax())
                .availableFrom(details.getAvailableFrom())
                .availableUntil(details.getAvailableUntil())
                .freeCancellationDays(details.getFreeCancellationDays())
                .noPrepaymentDays(details.getNoPrepaymentDays())
                .facilityDto(details.getFacilityDto().stream().map(
                        facilityDto ->  RoomsSearchDetails.FacilityDto.builder()
                                .id(facilityDto.getId())
                                .displayName(facilityDto.getDisplayName())
                                .build()
                ).toList())
                .bedDto(details.getBedDto().stream().map(
                        bedInfoDto -> RoomsSearchDetails.BedInfoDto.builder()
                                .id(bedInfoDto.getId())
                                .size(bedInfoDto.getSize())
                                .quantity(bedInfoDto.getQuantity())
                                .build()
                ).toList())
                .roomDto(rooms.getRoomSet().stream()
                        .map(room -> RoomsSearchDetails.RoomDto.builder()
                                .roomId(room.getId())
                                .datesDtoList(
                                        datesMap.get(room.getId()).stream()
                                                .map(dates -> modelMapper.map(dates, RoomsSearchDetails.DatesDto.class))
                                                .toList()
                                )
                                .build())
                        .toList())
                .priceDto(priceList.stream()
                        .map(price -> modelMapper.map(price, RoomsSearchDetails.PriceDto.class))
                        .toList())
                .build();

        roomsSearchKafkaTemplate.send("rooms-search", roomsSearchDetails);
    }



    public void deleteRooms(RoomsBookingDeleteRequest request) {
        roomsRepository.deleteById(request.getRoomsId());
    }

    public boolean validateBookingRequest(
            Map<Integer, Integer> roomNumMap, BookingRequest request,
            Set<Rooms> roomsSet, Map<Long, Integer> roomsIdMap, Set<Dates> datesSet, Set<Price> priceSet,
            Map<Integer, Long> roomsPrice
    ){
        Map<Integer, Rooms> roomsMap = new HashMap<>();
        for (Rooms rooms: roomsSet){
            roomsMap.put(rooms.getId(), rooms);
        }

        // check available room number
        Map<Integer, Integer> roomNumCount = new HashMap<>();
        for (Dates dates: datesSet){
            Integer roomsId = roomsIdMap.get(dates.getRoom().getId());
            roomNumCount.put(roomsId, roomNumCount.getOrDefault(roomsId, 0) + 1);
        }
        for (Map.Entry<Integer, Integer> entry: roomNumMap.entrySet()){
            if (!roomNumCount.containsKey(entry.getKey()) || entry.getValue() > roomNumCount.get(entry.getKey())){
                log.info("Booking request invalid not enough room left.");
                return false;
            }
        }

        // check check-in/check-out time
        for (Rooms rooms: roomsSet){
            if (roomNumMap.containsKey(rooms.getId())){
                if (rooms.getCheckInTime() > request.getCheckInTime()){
                    log.info("Booking request invalid check-in time");
                    return false;
                }
                if (rooms.getCheckOutTime() < request.getCheckOutTime()){
                    log.info("Booking request invalid check-out time");
                    return false;
                }
            }
        }

        LocalDate today = LocalDate.now();
        for (BookingRequest.BookingRequestRooms requestRooms: request.getRooms()){
            Rooms rooms = roomsMap.getOrDefault(requestRooms.getRoomsId(), null);
            if (rooms == null){
                log.info("Rooms not found.");
                return false;
            }
            // todo: consider timezones
            if (requestRooms.getNoPrepaymentUntil() != null && requestRooms.getNoPrepaymentUntil().isBefore(today.minusDays(rooms.getNoPrepaymentDays()-1))){
                return false;
            }
            if (requestRooms.getFreeCancellationUntil() != null && requestRooms.getFreeCancellationUntil().isBefore(today.minusDays(rooms.getFreeCancellationDays()-1))){
                return false;
            }
        }

        // check price
        for (Price price: priceSet){
            System.out.println(price);
            Integer roomsId = price.getRooms().getId();
            roomsPrice.put(roomsId, roomsPrice.getOrDefault(roomsId, 0L) + price.getPriceInCents());
        }
        Long totalPrice = 0L;
        for (Map.Entry<Integer, Long> entry: roomsPrice.entrySet()){
            totalPrice += entry.getValue() * roomNumMap.getOrDefault(entry.getKey(), 0);
        }
        if (!totalPrice.equals(request.getPriceInCents())){
//            System.out.println(roomsPrice);
            log.info("Booking request invalid due to price mismatch. ({calculated: {}} != {requested: {}})", totalPrice, request.getPriceInCents());
            return false;
        }

        return true;
    }

    public DateRange getRoomsAvailableDateRange(Rooms rooms){
        LocalDate today = LocalDate.now();
        DateRange dateRange = DateRange.builder()
                .startDate(rooms.getAvailableFrom().isBefore(today) ? today : rooms.getAvailableFrom())
                .endDate(rooms.getAvailableUntil() == null || rooms.getAvailableUntil().isAfter(today.plusDays(MAX_BOOKING_DAYS)) ?
                        today.plusDays(MAX_BOOKING_DAYS) : rooms.getAvailableUntil())
                .build();
        return dateRange;
    }

    public DateRange adjustToDateRange(LocalDate startDate, LocalDate endDate, DateRange roomsDateRange){


        if (!endDate.isAfter(roomsDateRange.getStartDate())){
            return null;
        }
        if (!startDate.isBefore(roomsDateRange.getEndDate())){
            return null;
        }
        if (startDate.isBefore(roomsDateRange.getStartDate())){
            startDate = roomsDateRange.getStartDate();
        }
        if (endDate.isAfter(roomsDateRange.getEndDate())){
            endDate = roomsDateRange.getEndDate();
        }
        return DateRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }


//    public HotelAvailabilityResponse getRoomInfo(
//            Integer hotelId,
//            HotelAvailabilityRequest request
//    ) {
//        Hotel hotel = hotelRepository.findById(hotelId)
//                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
//        Set<Price> priceList = priceRepository.findByHotelIdAndDates(hotelId, request.getStartDate(), request.getEndDate());
//        List<Dates> datesList = datesRepository.findByHotelIdAndDatesIncludes(hotelId, request.getStartDate(), request.getStartDate());
//
//        Map<Long, List<Dates>> datesMap = new HashMap<>();
//        for (Dates dates: datesList){
//            Long roomId = dates.getRoom().getId();
//            List<Dates> roomDatesList = datesMap.getOrDefault(roomId, new ArrayList<>());
//            roomDatesList.add(dates);
//            datesMap.put(roomId, roomDatesList);
//        }
//
//        for (Rooms rooms: hotel.getRoomsSet()){
//            Integer quantity = 0;
//            for (Room room: rooms.getRoomSet()){
//                List<Dates> roomDatesList = datesMap.getOrDefault(room.getId(), null);
//                if (roomDatesList != null){
//                    roomDatesList.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
//                }
//                LocalDate date = request.getStartDate();
//                for (Dates dates: roomDatesList){
//                    if (!dates.getStartDate().isAfter(date) && dates.getEndDate().isAfter(date)){
//                        date = dates.getEndDate();
//                    }
//                    if (dates.getEndDate().isBefore(request.getEndDate())){
//                        quantity += 1;
//                        break;
//                    }
//                }
//            }
//
//        }
//
//
//
//
//        Map<Integer, HotelAvailabilityResponse.RoomsAvailability> roomsAvailability = new HashMap<>();
//
//
//
//    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomsData {
        LocalDate availableFrom;
        LocalDate availableUntil;
        Integer quantity;
        Long priceMin;
        Long priceMax;
    }

}
