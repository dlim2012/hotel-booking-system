package com.dlim2012.bookingmanagement.controller;

import com.dlim2012.bookingmanagement.dto.ArchiveSearchCriteria;
import com.dlim2012.clients.entity.BookingEntity;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.bookingmanagement.service.BookingManagementService;
import com.dlim2012.clients.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking-management")
@CrossOrigin
@RequiredArgsConstructor
public class BookingManagementController {

    private final BookingManagementService bookingManagementService;
    private final JwtService jwtService;

    @GetMapping("/appuser")
    public List<BookingEntity> getBookingsByUserId(
            @RequestBody ArchiveSearchCriteria archiveSearchCriteria){
        Integer userId = jwtService.getId();;
        return bookingManagementService.getBookingsByUserId(
                userId, archiveSearchCriteria.getBookingMainStatus(), archiveSearchCriteria.getMonths()
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public List<BookingEntity> getBookingsByHotelId(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody ArchiveSearchCriteria archiveSearchCriteria
    ){
        return bookingManagementService.getBookingsByHotelId(
                hotelId, archiveSearchCriteria.getBookingMainStatus(), archiveSearchCriteria.getMonths()
        );
    }

    @GetMapping("/hotel/{hotelId}/room/{roomId}")
    public List<BookingEntity> getBookingsByRoomId(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @RequestBody ArchiveSearchCriteria archiveSearchCriteria
    ){
        return bookingManagementService.getBookingsByRoomId(
                hotelId, roomId, archiveSearchCriteria.getBookingMainStatus(), archiveSearchCriteria.getMonths()
        );
    }
}
