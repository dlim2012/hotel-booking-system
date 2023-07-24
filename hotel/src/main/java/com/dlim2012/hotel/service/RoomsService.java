package com.dlim2012.hotel.service;

import com.dlim2012.clients.entity.Bed;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDeleteRequest;
import com.dlim2012.hotel.dto.hotel.profile.RoomNameItem;
import com.dlim2012.hotel.dto.rooms.RoomsInfo;
import com.dlim2012.hotel.dto.rooms.profile.RoomsFacilityItem;
import com.dlim2012.hotel.dto.rooms.profile.RoomsGeneralInfoItem;
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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class RoomsService {

    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;
    private final RoomFacilityRepository roomFacilityRepository;

    private final LocalityService localityService;
    private final FacilityService facilityService;

    private final KafkaTemplate<String, RoomsBookingDetails> roomsBookingKafkaTemplate;
    private final KafkaTemplate<String, RoomsBookingDeleteRequest> roomsBookingDeleteKafkaTemplate;
    private final KafkaTemplate<String, RoomsSearchDeleteRequest> roomsSearchDeleteKafkaTemplate;

    private final ModelMapper modelMapper = new ModelMapper();
    private final EntityManager entityManager;

    public RoomsService(HotelRepository hotelRepository, RoomsRepository roomsRepository, RoomFacilityRepository roomFacilityRepository, LocalityService localityService, FacilityService facilityService, KafkaTemplate<String, RoomsBookingDetails> roomsBookingKafkaTemplate, KafkaTemplate<String, RoomsBookingDeleteRequest> roomsBookingDeleteKafkaTemplate, KafkaTemplate<String, RoomsSearchDeleteRequest> roomsSearchDeleteKafkaTemplate, EntityManager entityManager) {
        this.hotelRepository = hotelRepository;
        this.roomsRepository = roomsRepository;
        this.roomFacilityRepository = roomFacilityRepository;
        this.localityService = localityService;
        this.facilityService = facilityService;
        this.roomsBookingKafkaTemplate = roomsBookingKafkaTemplate;
        this.roomsBookingDeleteKafkaTemplate = roomsBookingDeleteKafkaTemplate;
        this.roomsSearchDeleteKafkaTemplate = roomsSearchDeleteKafkaTemplate;
        this.entityManager = entityManager;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

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

    public List<RoomNameItem> getRoomNames(Integer hotelId) {
        return roomsRepository.findByHotelId(hotelId)
                .stream().map(room -> RoomNameItem.builder()
                        .id(room.getId())
                        .displayName(room.getDisplayName())
                        .isActive(room.getIsActive())
                        .build()
                ).toList();
    }

    public Rooms postRoom(Integer userId, Integer hotelId, RoomsRegisterRequest registerRequest) {
        // todo: shortName cannot have any digits
        if (registerRequest.getShortName().matches(".*\\d.*")){
            throw new IllegalArgumentException("shortName cannot have any digits");
        }

        if (!hotelRepository.existsByIdAndHotelManagerId(hotelId, userId)){
            log.error("Hotel not found for user.");
            throw new ResourceNotFoundException("Hotel not found.");
        }

        Rooms rooms = modelMapper.map(registerRequest, Rooms.class);
        rooms.setId(null);
        rooms.setHotel(entityManager.getReference(Hotel.class, hotelId));
        rooms.setUpdatedTime(LocalDateTime.now());
        rooms = roomsRepository.save(rooms);

        List<RoomsFacility> savedRoomsFacilities = facilityService.saveRoomsFacilities(rooms, registerRequest.getFacilityDisplayNameList());
        List<RoomsBed> savedRoomsBed = facilityService.saveRoomsBed(rooms, registerRequest.getBedInfoDtoList());

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

        for (RoomsBed roomsBed: rooms.getRoomsBeds()){
            if (!bedMap.containsKey(roomsBed.getBed())){
                rooms.getRoomsBeds().remove(roomsBed);
            } else {
                roomsBed.setQuantity(bedMap.get(roomsBed.getBed()));
                bedMap.remove(roomsBed.getBed());
            }
        }

        for (Map.Entry<Bed, Integer> entry: bedMap.entrySet()){
            rooms.getRoomsBeds().add(RoomsBed.builder()
                            .rooms(rooms)
                            .bed(entry.getKey())
                            .quantity(entry.getValue())
                    .build());
        }
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
                                .id(entity.getId())
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

    public void deleteRooms(Integer hotelId, Integer roomsId, Integer userId) {
        roomsRepository.deleteByHotelIdAndIdAndHotelManagerId(hotelId, roomsId, userId);
        roomsBookingDeleteKafkaTemplate.send("rooms-booking-delete", RoomsBookingDeleteRequest.builder().hotelId(hotelId).roomsId(roomsId).build());
        roomsSearchDeleteKafkaTemplate.send("rooms-search-delete", RoomsSearchDeleteRequest.builder().hotelId(hotelId).roomsId(roomsId).build());
    }
}
