package com.dlim2012.hotel.controller;


import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.hotel.dto.hotel.profile.RoomNameItem;
import com.dlim2012.hotel.dto.rooms.profile.RoomsFacilityItem;
import com.dlim2012.hotel.dto.rooms.profile.RoomsGeneralInfoItem;
import com.dlim2012.hotel.dto.rooms.registration.RoomsRegisterRequest;
import com.dlim2012.hotel.service.RoomsService;
import com.dlim2012.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
@CrossOrigin
public class RoomsController {
    private final RoomsService roomsService;
    private final JwtService jwtService;

    @PostMapping(path = "/hotel/{hotelId}/room")
    public IdItem postRoom(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody RoomsRegisterRequest registerRequest){
//        System.out.println(registerRequest);
        Integer userId = jwtService.getId();
        log.info("Room register requested from user {}.", userId);
        return new IdItem(roomsService.postRoom(userId, hotelId, registerRequest).getId());
    }


    @GetMapping(path = "/hotel/{hotelId}/rooms/list")
    public List<RoomNameItem> getRooms(
            @PathVariable("hotelId") Integer hotelId
    ) {
        // todo user authenticate?
        Integer userId = jwtService.getId();
        log.info("Get rooms requested for hotel {}.", hotelId);
        return roomsService.getRoomNames(hotelId);
    }

    /*
    Rooms Profile - general information
     */
    @GetMapping(path = "/hotel/{hotelId}/rooms/{roomsId}/info")
    public RoomsGeneralInfoItem getRoomsGeneralInfo(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId
    ){
        Integer userId = jwtService.getId();
        log.info("Fetch rooms general info requested for rooms {} of hotel {}.", roomsId, hotelId);
        return roomsService.getGeneralInfo(hotelId, roomsId, userId);
    }

    @PutMapping(path = "/hotel/{hotelId}/rooms/{roomsId}/info")
    public void putRoomsGeneralInfo(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId,
            @RequestBody RoomsGeneralInfoItem infoItem
    ){
        Integer userId = jwtService.getId();
        log.info("Put rooms general info requested for rooms {} of hotel {}.", roomsId, hotelId);
        roomsService.putGeneralInfo(hotelId, roomsId, userId, infoItem);
    }

    /*
    Rooms Profile - facilities
     */
    @GetMapping(path = "/hotel/{hotelId}/rooms/{roomsId}/facility")
    public RoomsFacilityItem getRoomsFacilityInfo(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId
    ){
        Integer userId = jwtService.getId();
        log.info("Fetch rooms general info requested for rooms {} of hotel {} from hotel manager {}.", roomsId, hotelId, userId);
        return roomsService.getFacilities(hotelId, roomsId, userId);
    }

    @PutMapping(path = "/hotel/{hotelId}/rooms/{roomsId}/facility")
    public void putRoomsFacility(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId,
            @RequestBody RoomsFacilityItem infoItem
    ){
        Integer userId = jwtService.getId();
        log.info("Put rooms general info requested for rooms {} of hotel {} from hotel manager {}.", roomsId, hotelId, userId);
        roomsService.putFacility(hotelId, roomsId, userId, infoItem);
    }

    @DeleteMapping(path="/hotel/{hotelId}/rooms/{roomsId}")
    public void deleteRooms(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId){
        Integer userId = jwtService.getId();
        log.info("Delete rooms requested (hotel ID: {}, rooms ID: {}, hotelManager ID: {})", hotelId, roomsId, userId);
        roomsService.deleteRooms(hotelId, roomsId, userId);
    }
}
