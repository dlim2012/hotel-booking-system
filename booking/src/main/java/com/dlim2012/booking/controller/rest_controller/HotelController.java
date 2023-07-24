package com.dlim2012.booking.controller.rest_controller;

import com.dlim2012.booking.dto.dates.availability.AddAvailabilityRequest;
import com.dlim2012.booking.dto.dates.availability.DeleteAvailabilityRequest;
import com.dlim2012.booking.dto.dates.availability.EditAvailabilityRequest;
import com.dlim2012.booking.dto.dates.booking.AddBookingRoomRequest;
import com.dlim2012.booking.dto.dates.booking.DeleteBookingRoomRequest;
import com.dlim2012.booking.dto.dates.booking.EditBookingRoomRequest;
import com.dlim2012.booking.dto.profile.RoomsPriceItem;
import com.dlim2012.booking.service.HotelService;
import com.dlim2012.booking.service.booking_entity.PriceService;
import com.dlim2012.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class HotelController {

    private final JwtService jwtService;
    private final HotelService hotelService;
    private final PriceService priceService;


    @PostMapping("/hotel/{hotelId}/dates/available")
    public void addAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody AddAvailabilityRequest request
            ){
        Integer userId = jwtService.getId();
        log.info("Add availability requested from hotel manager {}", userId);
        hotelService.addDatesByHotelManager(hotelId, userId, request);
    }

    @PutMapping("/hotel/{hotelId}/dates/available")
    public void editAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody EditAvailabilityRequest request
            ){
        Integer userId = jwtService.getId();
        log.info("Edit availability requested from hotel manager {}", userId);
        hotelService.editDatesByHotelManager(hotelId, userId, request);

    }

    @DeleteMapping("/hotel/{hotelId}/dates/available")
    public void deleteAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody DeleteAvailabilityRequest request
            ){
        Integer userId = jwtService.getId();
        log.info("Delete availability requested from hotel manager {}", userId);
        hotelService.deleteDatesByHotelManager(hotelId, userId, request);

    }

    @PostMapping("/hotel/{hotelId}/booking/{bookingId}/dates")
    public void addReserved(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("bookingId") Long bookingId,
            @RequestBody AddBookingRoomRequest request
            ){
        Integer userId = jwtService.getId();
        log.info("Add booking room requested from hotel manager {}", userId);
        hotelService.addBookingRoomByHotelManager(hotelId, bookingId, userId, request);
    }

    @PutMapping("/hotel/{hotelId}/booking/{bookingId}/dates")
    public void editReserved(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("bookingId") Long bookingId,
            @RequestBody EditBookingRoomRequest request
            ){
        Integer userId = jwtService.getId();
        log.info("Edit booking room requested from hotel manager {}", userId);
        hotelService.editBookingRoomByHotelManager(hotelId, userId, request);
    }

    @PutMapping("/hotel/{hotelId}/booking/{bookingId}/dates/cancel")
    public void cancelReserved(
            @PathVariable("bookingId") Long bookingId,
            @RequestBody DeleteBookingRoomRequest request
            ){
        Integer userId = jwtService.getId();
        log.info("Delete booking room requested from hotel manager {}", userId);
        hotelService.cancelBookingRoomByHotelManager(bookingId, userId, request);
    }


    @PutMapping(path = "/booking/{bookingId}/cancel/hotel")
    public void cancelBookingByHotelManager(
            @PathVariable("bookingId") Long bookingId
    ){
        Integer userId = jwtService.getId();
        log.info("Booking cancellation requested by hotel manager {}: booking {}", userId, bookingId);
        hotelService.cancelBookingByHotel(bookingId, userId);
    }


    /*
    Profile - price
     */

    @GetMapping(path = "/hotel/{hotelId}/rooms/{roomsId}/price")
    public RoomsPriceItem getRoomsPrice(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId
    ){
        Integer userId = jwtService.getId();
        log.info("Get rooms {} price requested for hotel {} by hotel manager {}", roomsId, hotelId, userId);
        // todo
        return priceService.getRoomsPrice(hotelId, roomsId, userId);
    }

    @PutMapping(path = "/hotel/{hotelId}/rooms/{roomsId}/price")
    public void putRoomsPrice(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId,
            @RequestBody RoomsPriceItem item
    ){
        Integer userId = jwtService.getId();
        log.info("Put rooms {} price requested for hotel {} by hotel manager {}", roomsId, hotelId, userId);
        // todo
        priceService.putRoomsPrice(hotelId, roomsId, userId, item);
    }

}
