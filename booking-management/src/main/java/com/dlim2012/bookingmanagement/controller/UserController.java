package com.dlim2012.bookingmanagement.controller;

import com.dlim2012.bookingmanagement.dto.ListByUserRequest;
import com.dlim2012.bookingmanagement.dto.booking.ActiveBookingItem;
import com.dlim2012.bookingmanagement.dto.booking.BookingArchiveItem;
import com.dlim2012.bookingmanagement.dto.booking.ArchivedBookingByUserSearchInfo;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingBookerInfo;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingDetailsInfo;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingRoomGuestInfo;
import com.dlim2012.bookingmanagement.service.UserService;
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
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/test")
    public String test(){
        return "Test";
    }

    @PostMapping("/user/booking")
    public List<BookingArchiveItem> getBookingsByUserId(
            @RequestBody ListByUserRequest request){
        Integer userId = jwtService.getId();;
        log.info("Booking list requested by user from user {}", userId);
        return userService.getBookingsByUserId(
                userId, request
        );
    }

    @GetMapping("/user/booking/{bookingId}/active")
    public ActiveBookingItem getActiveBookingItem(
            @PathVariable("bookingId") Long bookingId
    ){
        Integer userId = jwtService.getId();
        log.info("Booking {} (active) requested by user {}", bookingId, userId);
        return userService.getActiveBookingItemByAppUser(bookingId, userId);
    }

    @PutMapping("/booking/{bookingId}/active/booker")
    public void putBookerInfo(
            @PathVariable("bookingId") Long bookingId,
            @RequestBody BookingBookerInfo request

            ){
        Integer userId = jwtService.getId();
        log.info("Booking {} booker info edit requested by user {}", bookingId, userId);
        userService.putBookerInfo(bookingId, userId, request);
    }

    @PutMapping("/booking/{bookingId}/active/details")
    public void putDetails(
            @PathVariable("bookingId") Long bookingId,
            @RequestBody BookingDetailsInfo request
            ){
        Integer userId = jwtService.getId();
        log.info("Booking {} details info edit requested from user {}", userId, bookingId);
        userService.putDetailsInfo(bookingId, userId, request);
    }

    @PutMapping("/booking/{bookingId}/active/booking-room/{bookingRoomId}/guest")
    public void putGuestInfo(
            @PathVariable("bookingId") Long bookingId,
            @PathVariable("bookingRoomId") Long bookingRoomId,
            @RequestBody BookingRoomGuestInfo request
    ){
        Integer userId = jwtService.getId();
        log.info("Booking {} guest info edit requested from user {}: booking room {}",
                userId, bookingId, bookingRoomId);
        userService.putGuestInfo(bookingId, bookingRoomId, userId, request);
    }

    @PostMapping("/user/booking/{bookingId}/archived")
    public BookingArchiveByUserId getArchivedBookingItem(
            @PathVariable("bookingId") Long bookingId,
            @RequestBody ArchivedBookingByUserSearchInfo request
    ){
        Integer userId = jwtService.getId();
        log.info("Booking {} (archived) requested by user {}", bookingId, userId);
        return userService.getArchivedBookingItemByAppUser(bookingId, userId, request);
    }

}
