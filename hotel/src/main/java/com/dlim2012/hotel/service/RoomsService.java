package com.dlim2012.hotel.service;

import com.dlim2012.clients.entity.Bed;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDetails;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingInActivateRequest;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDeleteRequest;
import com.dlim2012.hotel.dto.hotel.profile.RoomsNameItem;
import com.dlim2012.hotel.dto.rooms.RoomsInfo;
import com.dlim2012.hotel.dto.rooms.profile.RoomsFacilityItem;
import com.dlim2012.hotel.dto.rooms.profile.RoomsGeneralInfoItem;
import com.dlim2012.hotel.dto.rooms.profile.RoomsIsActiveItem;
import com.dlim2012.hotel.dto.rooms.registration.BedInfo;
import com.dlim2012.hotel.dto.rooms.registration.RoomsRegisterRequest;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.Rooms;
import com.dlim2012.hotel.entity.facility.RoomsBed;
import com.dlim2012.hotel.entity.facility.RoomsFacility;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.RoomsRepository;
import com.dlim2012.hotel.repository.facility.RoomFacilityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomsService {

    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;
    private final RoomFacilityRepository roomFacilityRepository;

    private final LocalityService localityService;
    private final FacilityService facilityService;

    private final KafkaTemplate<String, RoomsBookingDetails> roomsBookingKafkaTemplate;
    private final KafkaTemplate<String, RoomsBookingDeleteRequest> roomsBookingDeleteKafkaTemplate;
    private final KafkaTemplate<String, RoomsSearchDeleteRequest> roomsSearchDeleteKafkaTemplate;
    private final KafkaTemplate<String, RoomsBookingInActivateRequest> roomsBookingInActivateKafkaTemplate;

    private final ModelMapper modelMapper = new ModelMapper();
    private final EntityManager entityManager;
    private final Integer MAX_BOOKING_DAYS = 90;


    public List<RoomsInfo> getRoomsInfo(Integer hotelId){

        return roomsRepository.findByHotelId(hotelId)
                .stream()
                .map(room -> RoomsInfo.builder()
                        .id(room.getId())
                        .displayName(room.getDisplayName())
                        .description(room.getDescription())
                        .maxAdult(room.getMaxAdult())
                        .maxChild(room.getMaxChild())
                        .quantity(room.getQuantity())
                        .checkInTime(room.getCheckInTime())
                        .checkOutTime(room.getCheckOutTime())
                        .freeCancellationDays(room.getFreeCancellationDays())
                        .noPrepaymentDays(room.getNoPrepaymentDays())
                        .breakfast(roomFacilityRepository.existsByRoomsIdAndFacilityId(room.getId(), 29))
                        .facilityList(room.getRoomFacilities().stream()
                                .map(roomsFacility -> roomsFacility.getFacility().getDisplayName())
                                .toList()
                        )
                        .bedInfoList(room.getRoomsBeds().stream()
                                .map(roomsBed -> BedInfo.builder().size(roomsBed.getBed().name())
                                        .quantity(roomsBed.getQuantity()).build())
                                .toList())
                        .build())
                .toList();
    }

    public List<RoomsNameItem> getRoomsNames(Integer hotelId, Integer userId) {

        LocalDate today = LocalDate.now();
        LocalDate maxDay = today.plusDays(MAX_BOOKING_DAYS);

        System.out.println("??????");


        System.out.println(roomsRepository.findByHotelIdAndHotelManagerId(hotelId, userId));
        return roomsRepository.findByHotelIdAndHotelManagerId(hotelId, userId)
                .stream().map(rooms -> RoomsNameItem.builder()
                        .id(rooms.getId())
                        .displayName(rooms.getDisplayName())
                        .isActive(rooms.getIsActive())
                        .quantity(rooms.getQuantity())
                        .availableFrom(rooms.getAvailableFrom().isAfter(today) ? rooms.getAvailableFrom() : today)
                        .availableUntil(rooms.getAvailableUntil() == null || rooms.getAvailableUntil().isAfter(maxDay) ? maxDay: rooms.getAvailableUntil())
                        .build()
                ).toList();
    }

    public Rooms postRoom(Integer userId, Integer hotelId, RoomsRegisterRequest request) {
        // todo: shortName cannot have any digits
        if (request.getShortName().matches(".*\\d.*")){
            throw new IllegalArgumentException("shortName cannot have any digits");
        }

        if (!hotelRepository.existsByIdAndHotelManagerId(hotelId, userId)){
            log.error("Hotel not found for user.");
            throw new ResourceNotFoundException("Hotel not found.");
        }

        Rooms rooms = Rooms.builder()
                .hotel(entityManager.getReference(Hotel.class, hotelId))
                .displayName(request.getDisplayName())
                .shortName(request.getShortName())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .maxAdult(request.getMaxAdult())
                .maxChild(request.getMaxChild())
                .quantity(request.getQuantity())
                .priceMin(request.getPriceMin())
                .priceMax(request.getPriceMax())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .availableFrom(request.getAvailableFrom())
                .availableUntil(request.getAvailableUntil())
                .freeCancellationDays(request.getFreeCancellationDays())
                .noPrepaymentDays(request.getNoPrepaymentDays())
                .updatedTime(LocalDateTime.now())
                .build();
        rooms = roomsRepository.save(rooms);

        List<RoomsFacility> savedRoomsFacilities = facilityService.saveRoomsFacilities(rooms, request.getFacilityDisplayNameList());
        Set<RoomsBed> savedRoomsBed = facilityService.saveRoomsBed(rooms, request.getBedInfoDtoList());

//        rooms = roomsRepository.findById(rooms.getId())
//                .orElseThrow(() -> new ResourceNotFoundException(""));

        rooms.setRoomFacilities(savedRoomsFacilities);
        rooms.setRoomsBeds(savedRoomsBed);
        if (rooms.getIsActive()){
            postRoomsKafka(rooms, userId);
        }
        return rooms;
    }

    /*
    Profile - General Info
     */
    public RoomsGeneralInfoItem getGeneralInfo(Integer hotelId, Integer roomsId, Integer userId) {
        Rooms rooms = roomsRepository.findByHotelIdAndIdAndHotelManagerId(hotelId, roomsId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found."));
        RoomsGeneralInfoItem roomsGeneralInfoItem = modelMapper.map(rooms, RoomsGeneralInfoItem.class);
        roomsGeneralInfoItem.setCheckInTime(timeIntegerToString(rooms.getCheckInTime()));
        roomsGeneralInfoItem.setCheckOutTime(timeIntegerToString(rooms.getCheckOutTime()));
        return roomsGeneralInfoItem;
    }

    public void putGeneralInfo(Integer hotelId, Integer roomsId, Integer userId, RoomsGeneralInfoItem infoItem) {
        if (infoItem.getShortName().matches(".*\\d.*")){
            throw new IllegalArgumentException("shortName cannot have any digits");
        }
        Rooms rooms = roomsRepository.findByHotelIdAndIdAndHotelManagerId(hotelId, roomsId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found."));
        System.out.println(rooms);
        System.out.println(infoItem);

        rooms.setDisplayName(infoItem.getDisplayName());
        rooms.setDescription(infoItem.getDescription());
        rooms.setMaxAdult(infoItem.getMaxAdult());
        rooms.setMaxChild(infoItem.getMaxChild());
        rooms.setQuantity(infoItem.getQuantity());
        rooms.setPriceMax(infoItem.getPriceMax());
        rooms.setPriceMin(infoItem.getPriceMin());
        rooms.setAvailableFrom(infoItem.getAvailableFrom());
        rooms.setAvailableUntil(infoItem.getAvailableUntil());
        rooms.setFreeCancellationDays(infoItem.getFreeCancellationDays());
        rooms.setNoPrepaymentDays(infoItem.getNoPrepaymentDays());
        rooms.setIsActive(infoItem.getIsActive());

        rooms.setCheckInTime(timeStringToInteger(infoItem.getCheckInTime()));
        rooms.setCheckOutTime(timeStringToInteger(infoItem.getCheckOutTime()));

        Map<Bed, Integer> bedMap = new HashMap<>();
        for (RoomsGeneralInfoItem.BedInfoItem bedInfoItem: infoItem.getRoomsBeds()){
            bedMap.put(Bed.valueOf(bedInfoItem.getBed()),bedInfoItem.getQuantity());
        }
        System.out.println(rooms.getRoomsBeds());
        System.out.println(bedMap);
        System.out.println("???");

        List<RoomsBed> roomsBedsToRemove = new ArrayList<>();
        for (RoomsBed roomsBed: rooms.getRoomsBeds()){
            if (!bedMap.containsKey(roomsBed.getBed())){
                roomsBedsToRemove.add(roomsBed);
//                rooms.getRoomsBeds().remove(roomsBed);
            } else {
                roomsBed.setQuantity(bedMap.get(roomsBed.getBed()));
                bedMap.remove(roomsBed.getBed());
            }
        }

        roomsBedsToRemove.forEach(rooms.getRoomsBeds()::remove);

        System.out.println(bedMap);
        for (Map.Entry<Bed, Integer> entry: bedMap.entrySet()){
            rooms.getRoomsBeds().add(RoomsBed.builder()
                            .rooms(rooms)
                            .bed(entry.getKey())
                            .quantity(entry.getValue())
                    .build());
        }
        System.out.println(rooms);
        roomsRepository.save(rooms);

        postRoomsKafka(rooms, userId);
    }

    public String timeIntegerToString(Integer time){
        return String.format("%02d:%02d", time / 60, time % 60);
    }

    public Integer timeStringToInteger(String time){
        return Integer.parseInt(time.substring(0, 2)) * 60 + Integer.parseInt(time.substring(3, 5));
    }

    public RoomsFacilityItem getFacilities(Integer hotelId, Integer roomsId, Integer hotelManagerId) {
        return RoomsFacilityItem.builder()
                .facility(facilityService.getRoomsFacilities(hotelId, roomsId, hotelManagerId)
                        .stream()
                        .map(roomsFacility -> roomsFacility.getFacility().getDisplayName())
                        .toList()
                )
                .build();
    }

    public void putFacility(Integer hotelId, Integer roomsId, Integer hotelManagerId, RoomsFacilityItem infoItem) {
        Set<RoomsFacility> roomsFacilitySet = facilityService.getRoomsFacilities(hotelId, roomsId, hotelManagerId);
        List<RoomsFacility> preserveList = new ArrayList<>();
        Set<String> newFacilitySet = new HashSet<>(infoItem.getFacility());
        for (RoomsFacility roomsFacility: roomsFacilitySet){
            if (newFacilitySet.contains(roomsFacility.getFacility().getDisplayName())){
                preserveList.add(roomsFacility);
                newFacilitySet.remove(roomsFacility.getFacility().getDisplayName());
            }
        }
        preserveList.forEach(roomsFacilitySet::remove);
        roomFacilityRepository.deleteAll(roomsFacilitySet);
        facilityService.saveRoomsFacilities(
                entityManager.getReference(Rooms.class, roomsId),
                newFacilitySet.stream().toList());

    }

    public void postRoomsKafka(Rooms rooms, Integer hotelManagerId){

        Boolean breakfast = false;
        for (RoomsFacility roomsFacility: rooms.getRoomFacilities()){
            if (Objects.equals(roomsFacility.getFacility().getDisplayName(), "Breakfast")){
                breakfast = true;
            }
        }

        RoomsBookingDetails roomsBookingDetails = RoomsBookingDetails.builder()
                .roomsId(rooms.getId())
                .hotelId(rooms.getHotel().getId())
                .hotelManagerId(hotelManagerId)
                .activate(rooms.getIsActive())
                .displayName(rooms.getDisplayName())
                .shortName(rooms.getShortName())
                .maxAdult(rooms.getMaxAdult())
                .maxChild(rooms.getMaxChild())
                .quantity(rooms.getQuantity())
                .numBed(rooms.getRoomsBeds().stream()
                        .map(RoomsBed::getQuantity).reduce(0, Integer::sum))
                .breakfast(breakfast)
                .priceMax(rooms.getPriceMax())
                .priceMin(rooms.getPriceMin())
                .checkInTime(rooms.getCheckInTime())
                .checkOutTime(rooms.getCheckOutTime())
                .availableFrom(rooms.getAvailableFrom())
                .availableUntil(rooms.getAvailableUntil())
                .freeCancellationDays(rooms.getFreeCancellationDays())
                .noPrepaymentDays(rooms.getNoPrepaymentDays())
                .facilityDto(rooms.getRoomFacilities().stream()
                        .map(entity -> RoomsBookingDetails.FacilityDto.builder()
                                .id(entity.getFacility().getId())
                                .displayName(entity.getFacility().getDisplayName())
                                .build()).toList())
                .bedDto(rooms.getRoomsBeds().stream()
                        .map(entity -> RoomsBookingDetails.BedInfoDto.builder()
                                .id(entity.getId())
                                .size(entity.getBed().name())
                                .quantity(entity.getQuantity())
                                .build()).toList())
                .build();

        roomsBookingKafkaTemplate.send("rooms-booking", roomsBookingDetails);
            // to delete: roomsBookingDeleteKafkaTemplate.send("rooms-booking-delete", {RoomsBookingDeleteRequest})

    }

    public RoomsIsActiveItem getRoomsIsActive(Integer hotelId, Integer roomsId, Integer userId) {
        Rooms rooms = roomsRepository.findByHotelIdAndIdAndHotelManagerId(hotelId, roomsId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found"));
        return RoomsIsActiveItem.builder().isActive(rooms.getIsActive()).build();
    }

    public void activateRooms(Integer hotelId, Integer roomsId, Integer userId) {
        Rooms rooms = roomsRepository.findByHotelIdAndIdAndHotelManagerIdAndIsActive(hotelId, roomsId, userId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found"));
        rooms.setIsActive(true);
        roomsRepository.save(rooms);
        postRoomsKafka(rooms, userId);
//        roomsBookingActivateKafkaTemplate.send("rooms-booking-activate",
//                RoomsBookingActivateRequest.builder().hotelId(hotelId).roomsId(roomsId).build());
    }

    public void inActivateRooms(Integer hotelId, Integer roomsId, Integer userId) {
        Rooms rooms = roomsRepository.findByHotelIdAndIdAndHotelManagerIdAndIsActive(hotelId, roomsId, userId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found"));
        rooms.setIsActive(false);
        roomsRepository.save(rooms);
        roomsBookingInActivateKafkaTemplate.send("rooms-booking-inactivate",
                RoomsBookingInActivateRequest.builder().hotelId(hotelId).roomsId(roomsId).build());
        roomsSearchDeleteKafkaTemplate.send("rooms-search-delete",
                RoomsSearchDeleteRequest.builder().hotelId(hotelId).roomsId(roomsId).build());
    }


    public void deleteRooms(Integer hotelId, Rooms rooms){
        roomsRepository.delete(rooms);
        roomsBookingDeleteKafkaTemplate.send("rooms-booking-delete", RoomsBookingDeleteRequest.builder().hotelId(hotelId).roomsId(rooms.getId()).build());
        roomsSearchDeleteKafkaTemplate.send("rooms-search-delete", RoomsSearchDeleteRequest.builder().hotelId(hotelId).roomsId(rooms.getId()).build());
    }

    public void deleteRooms(Integer hotelId, Integer roomsId, Integer userId) {
        Rooms rooms = roomsRepository.findByHotelIdAndIdAndHotelManagerId(hotelId, roomsId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found"));
        deleteRooms(hotelId, rooms);
    }

}
