package com.dlim2012.booking.controller.rest_controller;

import com.dlim2012.booking.dto.dates.booking.CancelBookingRoomRequest;
import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.booking.dto.reserve.BookingResponse;
import com.dlim2012.booking.dto.reserve.ReserveResponse;
import com.dlim2012.booking.service.RestService;
import com.dlim2012.booking.service.booking.BookingService;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.security.service.JwtService;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class UserController {


    private final JwtService jwtService;
    private final RestService restService;
    private final BookingService bookingService;


    @PostMapping(path = "/hotel/{hotelId}/reserve")
    public ReserveResponse reserveHotel(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody BookingRequest request
    ) {
        Integer userId = jwtService.getId();
        log.info("Reserve requested for hotel {} by user {}", hotelId, userId);
        Booking booking = restService.reserve(userId, hotelId, request);
        return ReserveResponse.builder()
                .bookingId(booking == null ? -1L : booking.getId())
                .build();
    }

    @PostMapping(path = "/hotel/{hotelId}/book")
    public BookingResponse bookHotel(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody BookingRequest bookingRequest
    ) throws PayPalRESTException {

        Integer userId = jwtService.getId();
        log.info("Booking requested for hotel {} by user {}", hotelId, userId);
        Booking booking = restService.reserve(userId, hotelId, bookingRequest);
        String redirectUrl = bookingService.createPaypalPayment(booking);
        return BookingResponse.builder()
                .bookingId(booking == null ? -1L : booking.getId())
                .redirectUrl(redirectUrl)
                .build();
    }

    @PutMapping(path = "/booking/{bookingId}/pay/user")
    public BookingResponse payReservationByUser(
            @PathVariable("bookingId") Long bookingId
    ) throws PayPalRESTException {
        Integer userId = jwtService.getId();
        log.info("Booking {} payment requested from user {}", bookingId, userId);
        Booking booking = bookingService.getBookingWithLock(UserRole.APP_USER, userId, bookingId);
        String redirectUrl = bookingService.createPaypalPayment(booking);
        return BookingResponse.builder()
                .bookingId(booking == null ? -1L : booking.getId())
                .redirectUrl(redirectUrl)
                .build();
    }

    @PutMapping(path = "/booking/{bookingId}/cancel/user")
    public void cancelBookingByUser(
            @PathVariable("bookingId") Long bookingId
    ) {
        Integer userId = jwtService.getId();
        log.info("Booking cancellation requested by app user {}: booking {}", userId, bookingId);
        bookingService.cancel(UserRole.APP_USER, userId, bookingId);
    }

    @PutMapping(path = "/booking/{bookingId}/booking-room/{bookingRoomId}/cancel/user")
    public void cancelBookingRoomByUser(
            @PathVariable("bookingId") Long bookingId,
            @RequestBody CancelBookingRoomRequest request
    ) {
        Integer userId = jwtService.getId();
        log.info("Booking Room cancellation requested by app user {}: booking {}, booking-room {}",
                userId, bookingId, request.getBookingRoomId());
        bookingService.cancelRoom(UserRole.APP_USER, userId,
                request.getBookingId(), request.getBookingRoomsId(), request.getBookingRoomId());
    }


}
