package com.dlim2012.booking.service.booking_entity.hotel_entity;

import com.dlim2012.booking.dto.internal.DateRange;
import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.booking.service.booking_entity.PriceService;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDetails;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingInActivateRequest;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchVersion;
import com.dlim2012.clients.mysql_booking.entity.*;
import com.dlim2012.clients.mysql_booking.repository.*;
import jakarta.persistence.EntityManager;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomsEntityService {

    private final PriceService priceService;
    private final HotelEntityService hotelEntityService;

    private final RoomsRepository roomsRepository;
    private final RoomRepository roomRepository;
    private final DatesRepository datesRepository;
    private final PriceRepository priceRepository;
    private final BookingRoomRepository bookingRoomRepository;

    private final KafkaTemplate<String, RoomsSearchDetails> roomsSearchKafkaTemplate;
    private final KafkaTemplate<String, RoomsSearchDeleteRequest> roomsSearchDeleteKafkaTemplate;
    private final KafkaTemplate<String, RoomsSearchVersion> roomsSearchVersionKafkaTemplate;

    private final Integer MAX_BOOKING_DAYS = 90;
    private final ModelMapper modelMapper = new ModelMapper();
    private final EntityManager entityManager;


    public void _registerRooms(RoomsBookingDetails details){
        if (details.getQuantity()<=0 || !details.getActivate()){
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
                .isActive(true)
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


        // Save 'room' rows
        List<Room> roomList = new ArrayList<>();
        for (int i=0; i<details.getQuantity(); i++){
            Room room = Room.builder()
                    .rooms(entityManager.getReference(Rooms.class, details.getRoomsId()))
                    .roomNumber(i+1)
                    .build();
            roomList.add(room);
        }
        roomList = roomRepository.saveAll(roomList);

        if (!rooms.getIsActive()){
            return;
        }

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

    public Rooms __updateRoomsInfo(RoomsBookingDetails details, List<Room> roomArrayList,
                                     List<Room> remainingRoomList, RoomsData prevRooms, LocalDate addedUntil){
        Optional<Rooms> optionalRooms = roomsRepository.findById(details.getRoomsId());
        if (optionalRooms.isEmpty()){
            return null;
        }
        Rooms rooms = optionalRooms.get();

        prevRooms.setIsActive(rooms.getIsActive());
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

        return roomsRepository.save(rooms);
    }



    public void _updateRoomsDates(
            Integer roomsId,
            List<Room> remainingRoomList,
            LocalDate prevStartDate,
            LocalDate prevEndDate,
            LocalDate startDate,
            LocalDate endDate
    ){

        Set<Dates> datesSet = datesRepository.findByRoomsIdWithLock(roomsId);
        datesRepository.deleteAll(datesSet);

        // Add new days
        int numDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        Map<Long, Boolean[]> roomDateMap = new HashMap<>();
        for (Room room: remainingRoomList){
            Long roomId = room.getId();
            Boolean[] boolArray = new Boolean[numDays];
            Arrays.fill(boolArray, Boolean.FALSE);

            int i = 0;
            for (LocalDate date=startDate; date.isBefore(endDate); date = date.plusDays(1)){
                if (date.isBefore(prevStartDate) || !date.isBefore(prevEndDate)){
                    boolArray[i] = true;
                }
                i += 1;
            }
            roomDateMap.put(roomId, boolArray);
        }
        for (Map.Entry<Long, Boolean[]> entry: roomDateMap.entrySet()){
            System.out.println(entry.getKey() + " " + Arrays.toString(entry.getValue()));
        }

        // Add previous days
        for (Dates dates: datesSet){
            Long roomId = dates.getRoom().getId();
            Boolean[] boolArray = roomDateMap.getOrDefault(roomId, null);
            for (LocalDate date = dates.getStartDate(); date.isBefore(dates.getEndDate()); date = date.plusDays(1)){
                int index = (int) ChronoUnit.DAYS.between(startDate, date);
                if (index >= numDays){
                    break;
                }
                if (index < 0){
                    continue;
                }
                boolArray[index] = true;
            }
        }
        for (Map.Entry<Long, Boolean[]> entry: roomDateMap.entrySet()){
            System.out.println(entry.getKey() + " " + Arrays.toString(entry.getValue()));
        }

        // Remove booked days
        Set<BookingRoom> bookingRooms = bookingRoomRepository.findByRoomsId(roomsId);
        for (BookingRoom bookingRoom: bookingRooms){
            Long roomId = bookingRoom.getRoomId();
            Boolean[] boolArray = roomDateMap.getOrDefault(roomId, null);
            if (boolArray == null){
                continue;
            }
            int start = (int) ChronoUnit.DAYS.between(startDate, bookingRoom.getStartDateTime().toLocalDate());
            int numBookedDays = (int) ChronoUnit.DAYS.between(bookingRoom.getStartDateTime().toLocalDate(), bookingRoom.getEndDateTime().toLocalDate());
            for (int i = Math.max(start, 0); i<numBookedDays; i++){
                if (i < 0){
                    continue;
                }
                if (i >=numDays){
                    break;
                }
                boolArray[i] = false;
            }
        }

//        for (Map.Entry<Long, Boolean[]> entry: roomDateMap.entrySet()){
//            System.out.println(entry.getKey() + " " + Arrays.toString(entry.getValue()));
//        }

        // Make dates
        List<Dates> newDates = new ArrayList<>();
        for (Map.Entry<Long, Boolean[]> entry: roomDateMap.entrySet()){
            Long roomId = entry.getKey();
            Boolean[] boolArray = entry.getValue();
            int start = 0;
            for (int i=0; i<numDays; i++){
                if (!boolArray[i]) {
                    if (start < i){
                        newDates.add(Dates.builder()
                                .room(entityManager.getReference(Room.class, roomId))
                                .startDate(startDate.plusDays(start))
                                .endDate(startDate.plusDays(i))
                                .build());
                    }
                    start = i + 1;
                }
            }
            if (start < numDays) {
                newDates.add(Dates.builder()
                        .room(entityManager.getReference(Room.class, roomId))
                        .startDate(startDate.plusDays(start))
                        .endDate(startDate.plusDays(numDays))
                        .build());
            }
        }

        newDates = datesRepository.saveAll(newDates);
    }

    public void _removeDates(Integer hotelId, Integer roomsId){
        datesRepository.deleteByRoomsId(roomsId);
    }

    public void _removePrices(Integer hotelId, Integer roomsId){
        priceRepository.deleteAllByRoomsId(roomsId);
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
        Rooms rooms = __updateRoomsInfo(details, roomArrayList, remainingRoomList, prevRooms, endDate);
        if (rooms == null){
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
        if (prevRooms.isActive) {
            _updateRoomsDates(details.getRoomsId(), remainingRoomList, prevStartDate, prevEndDate, startDate, endDate);
        }

        /* adjust room: add */
        if (prevRooms.getQuantity() < details.getQuantity()){
            roomArrayList = roomRepository.saveAll(roomArrayList);
            // todo: does this fetch roomIds as well?
            System.out.println("roomArrayList " + roomArrayList);
            if (prevRooms.isActive) {
                activateRoomList(details.getRoomsId(), roomArrayList);
            }
        }

        /* 4) update price */
        if (prevRooms.isActive) {
            priceService.updatePriceNewDateRange(
                    details.getRoomsId(), details.getPriceMin(), details.getPriceMax(),
                    prevStartDate, prevEndDate, startDate, endDate
            );
        }

        if (!prevRooms.isActive && details.getActivate()){
            activateRoomList(details.getRoomsId(), null);
        }
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

//        System.out.println(rooms.getRoomSet());
//        System.out.println(datesMap);

        RoomsSearchDetails roomsSearchDetails = RoomsSearchDetails.builder()
                .roomsId(details.getRoomsId())
                .hotelId(details.getHotelId())
                .version(hotelEntityService.getNewHotelVersion(details.getHotelId()))
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


    public void inactivateRooms(RoomsBookingInActivateRequest request) {
        Rooms rooms = roomsRepository.findById(request.getRoomsId())
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found."));
        _removeDates(request.getHotelId(), request.getRoomsId());
        _removePrices(request.getHotelId(), request.getRoomsId());
        rooms.setIsActive(false);
        roomsRepository.save(rooms);
    }

    public void activateRoomList(Integer roomsId, List<Room> roomList) {
        Rooms rooms = roomsRepository.findByIdWithLock(roomsId)
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found."));
        if (roomList == null){
            roomList = new ArrayList<>(rooms.getRoomSet());
        }

        // get start and end dates
        LocalDate minBookingDate = LocalDate.now();
        LocalDate maxBookingDate = minBookingDate.plusDays(MAX_BOOKING_DAYS);

        LocalDate startDate = rooms.getAvailableFrom().isAfter(minBookingDate) ? rooms.getAvailableFrom() : minBookingDate;
        LocalDate endDate = rooms.getAvailableUntil() == null || rooms.getAvailableUntil().isAfter(maxBookingDate) ?
                maxBookingDate : rooms.getAvailableUntil();

        // get booked dates
        Set<BookingRoom> bookingRooms = bookingRoomRepository.findByRoomsId(roomsId);
        Map<Long, List<BookingRoom>> bookingRoomMap = new HashMap<>();
        for (BookingRoom bookingRoom: bookingRooms){
            Long roomId = bookingRoom.getRoomId();
            List<BookingRoom> roomsBookingRoomList = bookingRoomMap.getOrDefault(roomId, new ArrayList<>());
            roomsBookingRoomList.add(bookingRoom);
            bookingRoomMap.put(roomId, roomsBookingRoomList);
        }

        List<Dates> datesToAdd = new ArrayList<>();
        for (Room room: roomList){
            Long roomId = room.getId();
            List<BookingRoom> roomsBookingRoomList = bookingRoomMap.getOrDefault(roomId, null);
            if (roomsBookingRoomList == null){
                datesToAdd.add(Dates.builder()
                        .room(room)
                        .startDate(startDate)
                        .endDate(endDate)
                        .build());
            } else {
                roomsBookingRoomList.sort((o1, o2) -> o1.getStartDateTime().compareTo(o2.getStartDateTime()));
                LocalDate date = startDate;
                for (BookingRoom bookingRoom: roomsBookingRoomList){
                    LocalDate bookingRoomStartDate = bookingRoom.getStartDateTime().toLocalDate();
                    LocalDate bookingRoomEndDate  = bookingRoom.getEndDateTime().toLocalDate();
                    if (!bookingRoomEndDate.isAfter(date)){
                        continue;
                    } else if (!bookingRoomStartDate.isAfter(date)){
                        if (!bookingRoomEndDate.isBefore(endDate)){
                            date = endDate;
                            break;
                        }
                        date = bookingRoomEndDate;
                    } else if (!bookingRoomStartDate.isAfter(endDate)){
                        datesToAdd.add(Dates.builder().room(room).startDate(date).endDate(bookingRoomStartDate).build());
                        if (!bookingRoomEndDate.isBefore(endDate)){
                            date = endDate;
                            break;
                        }
                        date = bookingRoomEndDate;
                    } else {
                        datesToAdd.add(Dates.builder().room(room).startDate(date).endDate(endDate).build());
                        date = endDate;
                        break;
                    }
                }
                if (date.isBefore(endDate)){
                    datesToAdd.add(Dates.builder().room(room).startDate(date).endDate(endDate).build());
                }
            }
        }


        datesRepository.saveAll(datesToAdd);

        rooms.setDatesAddedUntil(maxBookingDate);
        roomsRepository.save(rooms);

    }


    public void deleteRooms(RoomsBookingDeleteRequest request) {
        roomsRepository.deleteById(request.getRoomsId());
    }

    public void roomsVersionUp(Integer hotelId, Set<Integer> roomsIds, Map<Integer, Rooms> roomsMap){
        Long hotelVersion = hotelEntityService.getNewHotelVersion(hotelId);

        List<Dates> datesList = datesRepository.findByHotelIdWithLock(hotelId);
        Map<Long, List<Dates>> roomDatesMap = new HashMap<>();
        for (Dates dates: datesList){
            Long roomId = dates.getRoom().getId();
            List<Dates> roomDatesList = roomDatesMap.getOrDefault(roomId, new ArrayList<>());
            roomDatesList.add(dates);
            roomDatesMap.put(roomId, roomDatesList);
        }

        List<Rooms> roomsToUpdate = new ArrayList<>();
        List<RoomsSearchVersion> roomsSearchVersionList = new ArrayList<>();
        for (Integer roomsId: roomsIds){
            Rooms rooms = roomsMap.getOrDefault(roomsId, null);
            if (rooms == null){
                log.error("Rooms not found during roomsVersionUp. (hotel {}, rooms {})", hotelId, roomsId);
            }
            roomsToUpdate.add(rooms);
            RoomsSearchVersion roomsSearchVersion = RoomsSearchVersion.builder()
                    .roomsId(roomsId)
                    .hotelId(hotelId)
                    .version(hotelVersion)
                    .freeCancellationDays(rooms.getFreeCancellationDays())
                    .noPrepaymentDays(rooms.getNoPrepaymentDays())
                    .priceDto(priceRepository.findByRoomsId(roomsId)
                            .stream()
                            .map(price -> RoomsSearchVersion.PriceDto.builder()
                                    .priceId(price.getId())
                                    .priceInCents(price.getPriceInCents())
                                    .date(price.getDate())
                                    .build())
                            .toList()
                    )
                    .roomDto(rooms.getRoomSet().stream()
                            .map(room -> RoomsSearchVersion.RoomDto.builder()
                                    .roomId(room.getId())
                                    .datesDtoList(
                                            roomDatesMap.get(room.getId()).stream()
                                                    .map(dates -> modelMapper.map(dates, RoomsSearchDetails.DatesDto.class))
                                                    .toList()
                                    )
                                    .build())
                            .toList())
                    .build();
            roomsSearchVersionList.add(roomsSearchVersion);
        }
        roomsRepository.saveAll(roomsToUpdate);
        for (RoomsSearchVersion roomsSearchVersion: roomsSearchVersionList){
            roomsSearchVersionKafkaTemplate.send("rooms-search-version", roomsSearchVersion);
        }

    }

    public Set<Integer> validateBookingRequest(
            Map<Integer, Integer> roomNumMap, BookingRequest request,
            Map<Integer, Rooms> roomsMap, Map<Long, Integer> roomsIdMap, Set<Dates> datesSet, Set<Price> priceSet,
            Map<Integer, Long> roomsPrice
    ){
        Set<Integer> mismatchRoomsId = new HashSet<>();

        // check available room number
        Map<Integer, Integer> roomNumCount = new HashMap<>();
        for (Dates dates: datesSet){
            Integer roomsId = roomsIdMap.get(dates.getRoom().getId());
            roomNumCount.put(roomsId, roomNumCount.getOrDefault(roomsId, 0) + 1);
        }
        for (Map.Entry<Integer, Integer> entry: roomNumMap.entrySet()){
            if (!roomNumCount.containsKey(entry.getKey()) || entry.getValue() > roomNumCount.get(entry.getKey())){
                log.info("Booking request invalid not enough room left.");
                mismatchRoomsId.add(entry.getKey());
            }
        }

        // check check-in/check-out time
        for (Map.Entry<Integer, Rooms> entry: roomsMap.entrySet()){
            Rooms rooms = entry.getValue();
            if (roomNumMap.containsKey(rooms.getId())){
                if (rooms.getCheckInTime() > request.getCheckInTime()){
                    log.info("Booking request invalid check-in time");
                    mismatchRoomsId.add(rooms.getId());
                }
                if (rooms.getCheckOutTime() < request.getCheckOutTime()){
                    log.info("Booking request invalid check-out time");
                    mismatchRoomsId.add(rooms.getId());
                }
            }
        }

        LocalDate today = LocalDate.now();
        for (BookingRequest.BookingRequestRooms requestRooms: request.getRooms()){
            Rooms rooms = roomsMap.getOrDefault(requestRooms.getRoomsId(), null);
            if (rooms == null){
                log.info("Rooms not found.");
                return mismatchRoomsId;
            }
            // todo: consider timezones
            if (requestRooms.getNoPrepaymentUntil() != null && requestRooms.getNoPrepaymentUntil().isAfter(today.minusDays(rooms.getNoPrepaymentDays()-1))){
                log.info("No prepayment day is invalid. ({} (requested), {} (actual)", requestRooms.getNoPrepaymentUntil(), today.minusDays(rooms.getNoPrepaymentDays()-1));
                mismatchRoomsId.add(rooms.getId());
            }
            if (requestRooms.getFreeCancellationUntil() != null && requestRooms.getFreeCancellationUntil().isAfter(today.minusDays(rooms.getFreeCancellationDays()-1))){
                log.info("Free cancellation day is invalid. ({} (requested), {} (actual)", requestRooms.getFreeCancellationUntil(), today.minusDays(rooms.getFreeCancellationDays()-1));
                mismatchRoomsId.add(rooms.getId());
            }
        }

        // check price
        for (Price price: priceSet){
//            System.out.println(price);
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
            for (BookingRequest.BookingRequestRooms rooms: request.getRooms()){
                mismatchRoomsId.add(rooms.getRoomsId());
            }
            return mismatchRoomsId;
        }

        return null;
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

//    public Map<Integer, Long> getRoomsVersions(Integer hotelId){
//        Set<Rooms> roomsList = roomsRepository.findByHotelId(hotelId);
//        Map<Integer, Long> roomsVersions = new HashMap<>();
//        for (Rooms rooms: roomsList){
//            roomsVersions.put(rooms.getId(), rooms.getVersion());
//        }
//        return roomsVersions;
//    }


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
        Boolean isActive;
        LocalDate availableFrom;
        LocalDate availableUntil;
        Integer quantity;
        Long priceMin;
        Long priceMax;
    }

}
