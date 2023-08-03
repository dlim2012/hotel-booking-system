//package com.dlim2012.hotel.controller.test;
//
//import com.dlim2012.clients.dto.IdItem;
//import com.dlim2012.hotel.dto.hotel.registration.HotelRegisterRequest;
//import com.dlim2012.hotel.dto.rooms.registration.RoomsRegisterRequest;
//import com.dlim2012.hotel.repository.HotelRepository;
//import com.dlim2012.hotel.service.HotelService;
//import com.dlim2012.hotel.service.RoomsService;
//import com.dlim2012.security.service.JwtService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@Slf4j
//@RequestMapping("/api/v1/hotel/test")
//@RequiredArgsConstructor
//@CrossOrigin
//public class HotelTestController {
//    private final JwtService jwtService;
//    private final HotelRepository hotelRepository;
//
//    private final HotelService hotelService;
//    private final RoomsService roomsService;
//    private final JwtDecoder jwtDecoder;
//
//    @GetMapping(path="")
//    public String test(){
//        log.info("Test requested.");
//        return "Test";
//    }
//
//    @DeleteMapping(path="/hotel")
//    void deleteAllByUser(){
//        Integer userId = jwtService.getId();
//        try {
//            hotelRepository.deleteByHotelManagerId(userId);
//        } catch (Exception e){
//            System.out.println(e);
//            System.out.println(e.getMessage());
//        }
//        System.out.println("deleteAllByUser");
//        System.out.println(hotelRepository.findByHotelManagerId(userId));
//    }
//
//    @PostMapping(path="/hotel/register")
//    IdItem postHotel(@RequestBody HotelRegisterRequest request, @RequestHeader (name="Authorization") String token) {
//        Jwt jwt = jwtDecoder.decode(token);
//        Integer userId = jwtService.getId(jwt);
//        log.info("Hotel register requested from user {}.", userId);
//        return IdItem.builder()
//                .id(hotelService.postHotel(userId, request, false).getId())
//                .build();
//    }
//
//
//    @PostMapping(path = "/hotel/{hotelId}/room")
//    public IdItem postRoom(
//            @PathVariable("hotelId") Integer hotelId,
//            @RequestBody RoomsRegisterRequest registerRequest,
//            @RequestHeader (name="Authorization") String token){
//        Jwt jwt = jwtDecoder.decode(token);
//        Integer userId = jwtService.getId(jwt);
//        log.info("Room register requested from user {}.", userId);
//        return new IdItem(roomsService.postRoom(userId, hotelId, registerRequest).getId());
//    }
//
//}
