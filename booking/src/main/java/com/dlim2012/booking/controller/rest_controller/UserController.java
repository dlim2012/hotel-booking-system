package com.dlim2012.booking.controller.rest_controller;

import com.dlim2012.booking.dto.cancel.CancelBookingRoomResponse;
import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.booking.dto.reserve.BookingResponse;
import com.dlim2012.booking.dto.reserve.ReserveResponse;
import com.dlim2012.booking.service.UserService;
import com.dlim2012.booking.service.booking_entity.BookingService;
import com.dlim2012.booking.service.booking_entity.utils.PaypalService;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;
    private final BookingService bookingService;
    private final PaypalService paypalService;


    @PostMapping(path = "/hotel/{hotelId}/reserve")
    public ReserveResponse reserveHotel(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody BookingRequest bookingRequest
    ){
        Integer userId = jwtService.getId();
        log.info("Reserve requested for hotel {} by user {}", hotelId, userId);
        Booking booking = bookingService.reserveHotel(hotelId, userId, bookingRequest);
        if (booking == null){
            return new ReserveResponse(-1L, false);
        }
        return new ReserveResponse(
                booking.getId(),
                booking.getStatus().equals(BookingStatus.RESERVED)
        );
    }

    @PostMapping(path = "/hotel/{hotelId}/book")
    public BookingResponse bookHotel(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody BookingRequest bookingRequest
            ){
        Integer userId = jwtService.getId();
        log.info("Booking requested for hotel {} by user {}", hotelId, userId);
        return bookingService.bookHotel(hotelId, userId, bookingRequest);
    }

    @PutMapping(path = "/booking/{bookingId}/pay/user")
    public BookingResponse payReservationByUser(
            @PathVariable("bookingId") Long bookingId
    ){
        Integer userId = jwtService.getId();
        log.info("Booking {} payment requested from user {}", bookingId, userId);
        return userService.payReservation(bookingId, userId);
    }

    @PutMapping(path = "/booking/{bookingId}/cancel/user")
    public void cancelBookingByUser(
            @PathVariable("bookingId") Long bookingId
    ){
        Integer userId = jwtService.getId();
        log.info("Booking cancellation requested by app user {}: booking {}", userId, bookingId);
        userService.cancelBookingByUser(bookingId, userId);
    }

    @PutMapping(path = "/booking/{bookingId}/booking-room/{bookingRoomId}/cancel/user")
    public CancelBookingRoomResponse cancelBookingRoomByUser(
            @PathVariable("bookingId") Long bookingId,
            @PathVariable("bookingRoomId") Long bookingRoomId
            ){
        Integer userId = jwtService.getId();
        log.info("Booking Room cancellation requested by app user {}: booking {}, booking-room {}",
                userId, bookingId, bookingRoomId);
        return userService.cancelBookingRoomByUser(bookingId, bookingRoomId, userId);
    }


}
