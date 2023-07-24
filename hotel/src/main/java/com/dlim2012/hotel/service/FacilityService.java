package com.dlim2012.hotel.service;

import com.dlim2012.clients.entity.Bed;
import com.dlim2012.clients.entity.SharedIds;
import com.dlim2012.hotel.dto.rooms.registration.BedInfo;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.Rooms;
import com.dlim2012.hotel.entity.facility.Facility;
import com.dlim2012.hotel.entity.facility.HotelFacility;
import com.dlim2012.hotel.entity.facility.RoomsBed;
import com.dlim2012.hotel.entity.facility.RoomsFacility;
import com.dlim2012.hotel.repository.facility.FacilityRepository;
import com.dlim2012.hotel.repository.facility.HotelFacilityRepository;
import com.dlim2012.hotel.repository.facility.RoomFacilityRepository;
import com.dlim2012.hotel.repository.facility.RoomsBedRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilityService {

//    private final HotelRepository hotelRepository;
//    private final RoomRepository roomRepository;

    private final FacilityRepository facilityRepository;
    private final HotelFacilityRepository hotelFacilityRepository;
    private final RoomFacilityRepository roomFacilityRepository;
    private final RoomsBedRepository roomsBedRepository;

    private final SharedIds sharedIds = new SharedIds();
    private final EntityManager entityManager;
//
//    private final KafkaTemplate<String, FacilityItem> facilityItemKafkaTemplate;
//    private final KafkaTemplate<String, HotelFacilitiesItem> hotelFacilityItemKafkaTemplate;
//    private final KafkaTemplate<String, RoomFacilitiesItem> roomFacilityItemKafkaTemplate;
//
//    private final ModelMapper modelMapper = new ModelMapper();
//    private final EntityManager entityManager;

//    public void postFacility(FacilityItem facilityItem) {
//        if (facilityRepository.existsByDisplayName(facilityItem.getDisplayName())) {
//            throw new EntityAlreadyExistsException("Facility already exists");
//        }
//        Facility facility = modelMapper.map(facilityItem, Facility.class);
//        facility.setId(null);
//        facility = facilityRepository.save(facility);
//
//        facilityItem.setId(facility.getId());
//        facilityItemKafkaTemplate.send("facility", facilityItem);
//    }
//
//    public void putFacility(FacilityItem facilityItem){
//        if (!facilityRepository.existsByDisplayName(facilityItem.getDisplayName())) {
//            throw new ResourceNotFoundException("Facility not found.");
//        }
//        Facility facility = modelMapper.map(facilityItem, Facility.class);
//        facility = facilityRepository.save(facility);
//
//        facilityItem.setId(facility.getId());
//        facilityItemKafkaTemplate.send("facility", facilityItem);
//
//    }
//
//    public List<FacilityItem> getFacilities() {
//        return facilityRepository.findAll()
//                .stream().map(entity -> modelMapper.map(entity, FacilityItem.class)).toList();
//    }
//
//    public void deleteFacilities(List<IdItem> idItemList) {
//        List<Integer> toDelete = new ArrayList<>();
//        for (IdItem idItem : idItemList) {
//            if (hotelFacilityRepository.existsByFacilityId(idItem.getId())){
//                throw new DeleteRuleException("The facility is being used by one or more hotels.");
//            }
//            if (roomFacilityRepository.existsByFacilityId(idItem.getId())){
//                throw new DeleteRuleException("The facility is being used by one or more rooms.");
//            }
//            toDelete.add(idItem.getId());
//        }
//        facilityRepository.deleteAllById(toDelete);
//        for (Integer id: toDelete){
//            facilityItemKafkaTemplate.send("facility", FacilityItem.onlyId(id));
//        }
//    }
//
//    public void setHotelFacilities(
//            Integer hotelId,
//            List<HotelFacilityItem> hotelFacilityItemList) {
//        if (!hotelRepository.existsById(hotelId)){
//            throw new ResourceNotFoundException("Hotel not found.");
//        }
//        Hotel hotel = entityManager.getReference(Hotel.class, hotelId);
//        List<HotelFacility> hotelFacilityList = hotelFacilityItemList
//                .stream().map(
//                        item -> {
//                            HotelFacility hotelFacility = modelMapper.map(item, HotelFacility.class);
//                            hotelFacility.setHotel(hotel);
//                            return hotelFacility;
//                        }
//                ).toList();
//        hotelFacilityRepository.deleteByHotelId(hotelId);
//        hotelFacilityRepository.saveAll(hotelFacilityList);
//        HotelFacilitiesItem hotelFacilitiesItem = HotelFacilitiesItem.builder()
//                .hotelId(hotelId)
//                .facilityIds(hotelFacilityItemList.stream().map(HotelFacilityItem::getFacilityId).toList())
//                .isActive(hotelFacilityItemList.stream().map(HotelFacilityItem::getIsActive).toList())
//                .build();
//        hotelFacilityItemKafkaTemplate.send("hotel-facilities", hotelFacilitiesItem);
//
//    }
//
//    public List<FacilityItem> getHotelFacilities(Integer hotelId) {
//        return hotelFacilityRepository.findByHotelId(hotelId)
//                .stream().map(entity -> modelMapper.map(entity.getFacility(), FacilityItem.class)).toList();
//    }
//
//    public void setRoomFacilities(Integer hotelId, Integer roomId, List<RoomFacilityItem> facilityItemList) {
//        if (!roomRepository.existsByHotelIdAndId(hotelId, roomId)){
//            throw new ResourceNotFoundException("Room not found.");
//        }
//        Room room = entityManager.getReference(Room.class, roomId);
//        List<RoomFacility> roomFacilityList = facilityItemList
//                .stream().map(
//                        item -> {
//                            RoomFacility roomFacility = modelMapper.map(item, RoomFacility.class);
//                            roomFacility.setRoom(room);
//                            return roomFacility;
//                        }
//                ).toList();
//        roomFacilityRepository.deleteByRoomId(roomId);
//        roomFacilityRepository.saveAll(roomFacilityList);
//
//        RoomFacilitiesItem roomFacilitiesItem = RoomFacilitiesItem.builder()
//                .roomId(roomId)
//                .facilityIds(facilityItemList.stream().map(RoomFacilityItem::getFacilityId).toList())
//                .isActive(facilityItemList.stream().map(RoomFacilityItem::getIsActive).toList())
//                .build();
//        roomFacilityItemKafkaTemplate.send("room-facilities", roomFacilitiesItem);
//
//    }
//
//
//    public List<FacilityItem> getRoomFacilities(Integer hotelId, Integer roomId) {
//        if (!roomRepository.existsByHotelIdAndId(hotelId, roomId))
//            throw new ResourceNotFoundException("Room not found.");
//        return roomFacilityRepository.findByRoomId(roomId)
//                .stream().map(entity -> modelMapper.map(entity.getFacility(), FacilityItem.class)).toList();
//    }

    public Facility getFacility(String displayName){
//        System.out.println(displayName);
        return Facility.builder()
                .id(sharedIds.getFacilityId(displayName))
                .displayName(displayName)
                .build();
//        return entityManager.getReference(Facility.class, sharedIds.getFacilityId(displayName));

//        Optional<Facility> optionalFacility = facilityRepository.findByDisplayName(displayName);
//        if (optionalFacility.isPresent()){
//            return optionalFacility.get();
//        }
//        return facilityRepository.save(Facility.builder().displayName(displayName).build());
    }

    public List<HotelFacility> saveHotelFacilities(Hotel hotel, List<String> displayNames){
        List<HotelFacility> hotelFacilitiesToSave = new ArrayList<>();
        for (String displayName: displayNames){
            Facility facility = getFacility(displayName);
            HotelFacility hotelFacility = HotelFacility.builder()
                    .facility(facility)
                    .hotel(hotel)
                    .isActive(true)
                    .build();
            hotelFacilitiesToSave.add(hotelFacility);
        }
        return hotelFacilityRepository.saveAll(hotelFacilitiesToSave);
//        return hotelFacilitiesToSave;
    }

    public List<RoomsFacility> saveRoomsFacilities(Rooms rooms, List<String> displayNames) {
        List<RoomsFacility> roomsFacilityToSave = new ArrayList<>();
        for (String displayName: displayNames){
            Facility facility = getFacility(displayName);
            RoomsFacility roomsFacility = RoomsFacility.builder()
                    .facility(facility)
                    .rooms(rooms)
                    .isActive(true)
                    .build();
            roomsFacilityToSave.add(roomsFacility);
        }
        return roomFacilityRepository.saveAll(roomsFacilityToSave);
    }


    public List<RoomsBed> saveRoomsBed(Rooms rooms, List<BedInfo> bedInfoDtoList){
        List<RoomsBed> roomsBedToSave = new ArrayList<>();
        for (BedInfo bedInfo : bedInfoDtoList){
            RoomsBed roomsBed = RoomsBed.builder()
                    .rooms(rooms)
                    .bed(Bed.valueOf(bedInfo.getSize()))
                    .quantity(bedInfo.getQuantity())
                    .build();
            roomsBedToSave.add(roomsBed);
        }
        return roomsBedRepository.saveAll(roomsBedToSave);
    }

    public void saveFacilities(
            Map<String, Integer> facilitiesMap
    ){
        List<Facility> facilityList = new ArrayList<>();
        for (Map.Entry<String, Integer> facilityEntry: facilitiesMap.entrySet()){
            facilityList.add(Facility.builder()
                            .id(facilityEntry.getValue())
                            .displayName(facilityEntry.getKey())
                    .build());

//            System.out.println(facilityList.get(facilityList.size()-1).getId() + " " + facilityList.get(facilityList.size()-1).getDisplayName() +"~");
        }
        facilityList = facilityRepository.saveAll(facilityList);
//        for (Facility facility: facilityList){
//            System.out.println(facility.getId() + " " + facility.getDisplayName() +"?");
//        }
    }


    public Set<RoomsFacility> getRoomsFacilities(Integer roomsId) {
        return roomFacilityRepository.findByRoomsId(roomsId);
    }


    public Set<RoomsFacility> getRoomsFacilities(Integer hotelId, Integer roomsId, Integer hotelManagerId) {
        return roomFacilityRepository.findByHotelIdAndRoomsIdAndHotelManagerId(hotelId, roomsId, hotelManagerId);
    }
}
