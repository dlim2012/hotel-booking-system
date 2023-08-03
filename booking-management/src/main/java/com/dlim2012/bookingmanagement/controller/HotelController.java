package com.dlim2012.bookingmanagement.controller;

import com.dlim2012.bookingmanagement.dto.ListByHotelRequest;
import com.dlim2012.bookingmanagement.dto.booking.ActiveBookingItem;
import com.dlim2012.bookingmanagement.dto.booking.ArchivedBookingByUserSearchInfo;
import com.dlim2012.bookingmanagement.dto.booking.BookingArchiveItem;
import com.dlim2012.bookingmanagement.dto.booking.BookingMainGuestInfo;
import com.dlim2012.bookingmanagement.dto.hotelInfo.HotelDatesInfoResponse;
import com.dlim2012.bookingmanagement.dto.hotelInfo.HotelMainInfoResponse;
import com.dlim2012.bookingmanagement.service.HotelService;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByHotelId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking-management")
@CrossOrigin
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final JwtService jwtService;

    @PostMapping("/hotel/{hotelId}/booking")
    public List<BookingArchiveItem> getBookingsByHotelId(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody ListByHotelRequest request
    ){

        Integer userId = jwtService.getId();
        return hotelService.getBookingsByHotelId(
                hotelId, userId, request
        );
    }

    @GetMapping("/hotel/{hotelId}/main")
    public HotelMainInfoResponse getHotelMainInfo(
            @PathVariable("hotelId") Integer hotelId
    ){
        Integer userId = jwtService.getId();
        return hotelService.getHotelMain(hotelId, userId);
    }

    @GetMapping("/hotel/{hotelId}/dates")
    public HotelDatesInfoResponse getHotelDatesInfo(
            @PathVariable("hotelId") Integer hotelId
    ){
        Integer userId = jwtService.getId();
        return hotelService.getHotelDatesInfo(hotelId, userId);
    }

    @GetMapping("/hotel/{hotelId}/booking/{bookingId}/active/main-guest")
    public BookingMainGuestInfo getActiveMainGuestInfo(
            @PathVariable("bookingId") Long bookingId
    ){
        Integer userId = jwtService.getId();
        return hotelService.getActiveMainGuestInfo(bookingId, userId);
    }

    @GetMapping("/hotel/booking/{bookingId}/active")
    public ActiveBookingItem getActiveBookingItem(
            @PathVariable("bookingId") Long bookingId
    ){
        Integer userId = jwtService.getId();
        log.info("Active booking {} requested by hotel-manager {}", bookingId, userId);
        return hotelService.getActiveBookingItemByHotel(bookingId, userId);
    }

    @PostMapping("/hotel/{hotelId}/booking/{bookingId}/archived")
    public BookingArchiveByHotelId getArchivedBookingItem(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("bookingId") Long bookingId,
            @RequestBody ArchivedBookingByUserSearchInfo request
    ){
        Integer userId = jwtService.getId();
        log.info("Archived booking {} requested by hotel-manager {}", bookingId, userId);
        return hotelService.getArchivedBookingItemByHotel(hotelId, userId, bookingId, request);
    }

}
