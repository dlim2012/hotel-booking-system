package com.dlim2012.booking.controller.rest_controller;

import com.dlim2012.booking.dto.dates.availability.AddAvailabilityRequest;
import com.dlim2012.booking.dto.dates.availability.DeleteAvailabilityRequest;
import com.dlim2012.booking.dto.dates.availability.EditAvailabilityRequest;
import com.dlim2012.booking.dto.dates.booking.AddBookingRoomRequest;
import com.dlim2012.booking.dto.dates.booking.CancelBookingRoomRequest;
import com.dlim2012.booking.dto.dates.booking.EditBookingRoomRequest;
import com.dlim2012.booking.dto.profile.RoomsPriceItem;
import com.dlim2012.booking.service.booking.BookingService;
import com.dlim2012.booking.service.common.DatesService;
import com.dlim2012.booking.service.common.PriceService;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.mysql_booking.entity.Price;
import com.dlim2012.clients.mysql_booking.repository.PriceRepository;
import com.dlim2012.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class HotelController {

    private final JwtService jwtService;
    private final BookingService bookingService;
    private final DatesService datesService;
    private final PriceService priceService;

    private final PriceRepository priceRepository;

    /*
    Hotel dates page && booking records
     */
    @PostMapping("/hotel/{hotelId}/dates/available")
    public void addAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody AddAvailabilityRequest request
    ) {
        Integer userId = jwtService.getId();
        log.info("Add availability requested from hotel manager {}", userId);
        datesService.addAvailability(
                UserRole.HOTEL_MANAGER, userId, hotelId,
                request.getRoomId(), request.getStartDate(), request.getEndDate()
        );
    }

    @PutMapping("/hotel/{hotelId}/dates/available")
    public void editAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody EditAvailabilityRequest request
    ) {
        Integer userId = jwtService.getId();
        log.info("Edit availability requested from hotel manager {}", userId);
        datesService.editAvailability(
                UserRole.HOTEL_MANAGER, userId, hotelId,
                request.getRoomId(), request.getDatesId(), request.getNewStartDate(), request.getNewEndDate()
        );
    }

    @DeleteMapping("/hotel/{hotelId}/dates/available")
    public void deleteAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody DeleteAvailabilityRequest request
    ) {
        Integer userId = jwtService.getId();
        log.info("Delete availability requested from hotel manager {}", userId);
        datesService.deleteAvailability(
                UserRole.HOTEL_MANAGER, userId, hotelId,
                request.getDatesId()
        );
    }

    @PostMapping("/hotel/{hotelId}/booking/{bookingId}/dates")
    public void addReserved(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("bookingId") Long bookingId,
            @RequestBody AddBookingRoomRequest request
    ) {
        Integer userId = jwtService.getId();
        log.info("Add booking room requested from hotel manager {}", userId);
        // TODO: SET TIME FOR startDateTime AND endDateTime
        LocalDateTime startDateTime = request.getStartDate().atTime(0, 0);
        LocalDateTime endDateTime = request.getEndDate().atTime(0, 0);
        bookingService.addRoom(
                UserRole.HOTEL_MANAGER, userId,
                hotelId, request.getRoomsId(), request.getRoomId(),
                bookingId, startDateTime, endDateTime, request.getPayed(),
                request.getGuestName(), request.getGuestEmail()
        );
    }

    @PutMapping("/hotel/{hotelId}/booking/{bookingId}/dates")
    public void editReserved(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("bookingId") Long bookingId,
            @RequestBody EditBookingRoomRequest request
    ) {
        Integer userId = jwtService.getId();
        log.info("Edit booking room requested from hotel manager {}", userId);
        // TODO: SET TIME FOR startDateTime AND endDateTime
        LocalDateTime newStartDateTime = request.getStartDate().atTime(0, 0);
        LocalDateTime newEndDateTime = request.getEndDate().atTime(0, 0);
        bookingService.editRoom(
                UserRole.HOTEL_MANAGER, userId,
                request.getBookingId(), request.getBookingRoomsId(), request.getRoomId(),
                request.getRoomId(), newStartDateTime, newEndDateTime);
    }

    @PutMapping("/hotel/{hotelId}/booking/{bookingId}/dates/cancel")
    public void cancelBookingRoomByHotelManager(
            @PathVariable("bookingId") Long bookingId,
            @RequestBody CancelBookingRoomRequest request
    ) {
        Integer userId = jwtService.getId();
        log.info("Delete booking room requested from hotel manager {}", userId);
        bookingService.cancelRoom(
                UserRole.HOTEL_MANAGER, userId,
                request.getBookingId(), request.getBookingRoomsId(), request.getBookingRoomId()
        );
    }


    @PutMapping(path = "/booking/{bookingId}/cancel/hotel")
    public void cancelBookingByHotelManager(
            @PathVariable("bookingId") Long bookingId
    ) {
        Integer userId = jwtService.getId();
        log.info("Booking cancellation requested by hotel manager {}: booking {}", userId, bookingId);
        bookingService.cancel(
                UserRole.HOTEL_MANAGER, userId,
                bookingId
        );
    }


    /*
    Profile - price
     */

    @GetMapping(path = "/hotel/{hotelId}/rooms/{roomsId}/price")
    public RoomsPriceItem getRoomsPrice(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId
    ) {
        Integer userId = jwtService.getId();
        log.info("Get rooms {} price requested for hotel {} by hotel manager {}", roomsId, hotelId, userId);

        // No authentication needed to fetch price
        List<Price> priceList = priceRepository.findByRoomsIdWithLock(roomsId);

        return RoomsPriceItem.builder()
                .roomsId(roomsId)
                .priceDtoList(priceList.stream()
                        .map(price -> RoomsPriceItem.PriceDto.builder()
                                .date(price.getDate())
                                .priceInCents(price.getPriceInCents())
                                .build())
                        .toList())
                .build();
    }

    @PutMapping(path = "/hotel/{hotelId}/rooms/{roomsId}/price")
    public void putRoomsPrice(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomsId") Integer roomsId,
            @RequestBody RoomsPriceItem item
    ) {
        Integer userId = jwtService.getId();
        log.info("Put rooms {} price requested for hotel {} by hotel manager {}", roomsId, hotelId, userId);
        // todo
        priceService.putRoomsPrice(UserRole.HOTEL_MANAGER, userId, hotelId, item);
    }

}
