package com.dlim2012.booking.service;

import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.booking.service.booking.BookingService;
import com.dlim2012.booking.service.common.GetEntityService;
import com.dlim2012.booking.service.hotel.HotelService;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.Hotel;
import com.dlim2012.clients.mysql_booking.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestService {
    /*
    A middle layer between REST API Controllers and other services for duplicate codes
     */

    private final GetEntityService getEntityService;
    private final BookingService bookingService;
    private final HotelService hotelService;

    private final HotelRepository hotelRepository;

    public Booking reserve(Integer userId, Integer hotelId, BookingRequest request) {

        Hotel hotel = hotelRepository.findByIdWithLock(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        Map<Integer, Long> roomsPriceMap = getEntityService.getRoomsPriceSumMap(
                hotelId, request.getStartDate(), request.getEndDate());

        Map<Integer, List<BookingRequest.BookingRequestRooms>> requestRoomsMap = new HashMap<>();
        for (BookingRequest.BookingRequestRooms bookingRequestRooms : request.getRooms()) {
            Integer roomsId = bookingRequestRooms.getRoomsId();
            List<BookingRequest.BookingRequestRooms> requestRoomsList = requestRoomsMap.getOrDefault(roomsId, new ArrayList<>());
            requestRoomsList.add(bookingRequestRooms);
            requestRoomsMap.putIfAbsent(roomsId, requestRoomsList);
        }

        // validate request
        if (!hotelService.validateBookingRequest(hotel, roomsPriceMap, request, requestRoomsMap)) {
            return null;
        }

        // make reservation
        return bookingService.reserve(userId, hotel, roomsPriceMap, request, requestRoomsMap);
    }
}
