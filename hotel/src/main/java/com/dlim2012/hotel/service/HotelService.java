package com.dlim2012.hotel.service;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.dto.hotel.facility.FacilityItem;
import com.dlim2012.clients.dto.hotel.facility.HotelFacilityItem;
import com.dlim2012.clients.dto.hotel.facility.RoomFacilityItem;
import com.dlim2012.hotel.dto.locality.CityItem;
import com.dlim2012.hotel.dto.locality.CountryItem;
import com.dlim2012.hotel.dto.locality.LocalityItem;
import com.dlim2012.hotel.dto.locality.StateItem;
import com.dlim2012.clients.exception.DeleteRuleException;
import com.dlim2012.clients.exception.EntityAlreadyExistsException;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.Room;
import com.dlim2012.hotel.entity.facility.Facility;
import com.dlim2012.hotel.entity.facility.HotelFacility;
import com.dlim2012.hotel.entity.facility.RoomFacility;
import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.entity.locality.City;
import com.dlim2012.hotel.entity.locality.Country;
import com.dlim2012.hotel.entity.locality.Locality;
import com.dlim2012.hotel.entity.locality.State;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.RoomRepository;
import com.dlim2012.hotel.repository.facility.FacilityRepository;
import com.dlim2012.hotel.repository.facility.HotelFacilityRepository;
import com.dlim2012.hotel.repository.facility.RoomFacilityRepository;
import com.dlim2012.hotel.repository.file.HotelImageRepository;
import com.dlim2012.hotel.repository.file.RoomImageRepository;
import com.dlim2012.hotel.repository.locality.CityRepository;
import com.dlim2012.hotel.repository.locality.CountryRepository;
import com.dlim2012.hotel.repository.locality.LocalityRepository;
import com.dlim2012.hotel.repository.locality.StateRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;


    private final KafkaTemplate<String, RoomItem> roomKafkaTemplate;
    private final KafkaTemplate<String, HotelItem> hotelKafkaTemplate;

    private final ImageService imageService;
    private final LocalityService localityService;

    private final ModelMapper modelMapper = new ModelMapper();
    private final EntityManager entityManager;



    public void postRoom(Integer hotelId, RoomItem roomItem, String managerEmail) {
        if (!hotelRepository.existsById(hotelId)){
            throw new ResourceNotFoundException("Hotel not found.");
        }
        Room room = modelMapper.map(roomItem, Room.class);
        room.setId(null);
        room.setHotel(entityManager.getReference(Hotel.class, hotelId));
        room.setUpdatedTime(LocalDateTime.now());
        Room savedRoom = roomRepository.save(room);

        roomItem.setId(savedRoom.getId());
        roomItem.setHotelId(hotelId);
        roomItem.setManagerEmail(managerEmail);
        roomKafkaTemplate.send("room", roomItem);
    }

    public void putRoom(Integer hotelId, Integer roomId, RoomItem roomItem, String managerEmail){
        if (!roomRepository.existsByHotelIdAndId(hotelId, roomId)){
            throw new ResourceNotFoundException("Room not found.");
        }
        Room room = modelMapper.map(roomItem, Room.class);
        room.setId(roomId);
        room.setHotel(entityManager.getReference(Hotel.class, hotelId));
        room.setActive(roomItem.getIsActive());
        room.setUpdatedTime(LocalDateTime.now());
        roomRepository.save(room);

//        roomItem.setId(roomId);
//        roomItem.setHotelId(hotelId);
        System.out.println(room);
        roomKafkaTemplate.send("room", modelMapper.map(room, RoomItem.class));
    }

    public RoomItem getRoom(Integer hotelId, Integer roomId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        Room room = roomRepository.findByHotelIdAndId(hotelId, roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found."));
        return modelMapper.map(room, RoomItem.class);
    }

    public void deleteRoom(Integer hotelId, Integer roomId) {
        List<Integer> roomIdList = new ArrayList<>();

        if (!roomRepository.existsByHotelIdAndId(hotelId, roomId)){
            throw new ResourceNotFoundException("Room not found.");
        }
        // delete room images
        imageService.deleteRoomImages(roomId);
        roomIdList.add(roomId);
        roomKafkaTemplate.send("room", RoomItem.zeroQuantity(roomId));
        // delete all room and room facilities (cascaded)
        roomRepository.deleteAllById(roomIdList);
        System.out.println("delete room");
    }


    public Hotel postHotel(
            Integer userId,
            HotelItem hotelItem, String managerEmail) {
        /*
         * While countries are pre-populated beforehand, state, city, and localities can be added by a post hotel request
         * */

        Locality locality = localityService.createOrGetLocality(hotelItem.getZipcode(), hotelItem.getCity(),
                hotelItem.getState(), hotelItem.getCountry());
        Hotel hotel = modelMapper.map(hotelItem, Hotel.class);
        hotel.setId(null);
        hotel.setHotelManagerId(userId);
        hotel.setLocality(locality);
        hotel.setUpdatedTime(LocalDateTime.now());
        hotel = hotelRepository.save(hotel);

        hotelItem.setManagerEmail(managerEmail);
        hotelItem.setId(hotel.getId());
        hotelKafkaTemplate.send("hotel", hotelItem);
        return hotel;
    }

    public void putHotel(Integer hotelId, HotelItem hotelItem, String managerEmail){

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));

        Locality locality = localityService.createOrGetLocality(hotelItem.getZipcode(), hotelItem.getCity(),
                hotelItem.getState(), hotelItem.getCountry());

        hotel.setName(hotelItem.getDisplayName());
        hotel.setDescription(hotelItem.getDescription());
        hotel.setAddressLine1(hotelItem.getAddressLine1());
        hotel.setAddressLine2(hotelItem.getAddressLine2());
        hotel.setLocality(locality);
        hotel.setUpdatedTime(LocalDateTime.now());
        hotelRepository.save(hotel);

        hotelItem.setManagerEmail(managerEmail);
        hotelItem.setId(hotelId);
        hotelKafkaTemplate.send("hotel",hotelItem);
    }



    public void deleteHotel(Integer hotelId) {
        // delete rooms
        List<Integer> roomIdList = roomRepository.findIdsByHotelId(hotelId);
        for (Integer roomId: roomIdList){
            imageService.deleteRoomImages(roomId);
            roomKafkaTemplate.send("room", RoomItem.zeroQuantity(roomId));
        }
        roomRepository.deleteAllById(roomIdList);
        // delete hotel images
        imageService.deleteHotelImages(hotelId);
        // delete hotel and hotel facilities (cascaded)
        hotelRepository.deleteById(hotelId);
        hotelKafkaTemplate.send("hotel", new HotelItem(hotelId));
        System.out.println("delete hotel");
    }



    public HotelItem getHotel(Integer hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        Locality locality = hotel.getLocality();
        City city = locality.getCity();
        State state = city.getState();
        Country country = state.getCountry();

        HotelItem hotelItem = modelMapper.map(hotel, HotelItem.class);
        hotelItem.setZipcode(locality.getZipcode());
        hotelItem.setCity(city.getName());
        hotelItem.setState(state.getName());
        hotelItem.setCountry(country.getName());

        return hotelItem;
    }



}
