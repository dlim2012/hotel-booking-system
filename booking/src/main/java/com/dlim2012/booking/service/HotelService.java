package com.dlim2012.booking.service;

import com.dlim2012.booking.dto.dates.availability.AddAvailabilityRequest;
import com.dlim2012.booking.dto.dates.availability.DeleteAvailabilityRequest;
import com.dlim2012.booking.dto.dates.availability.EditAvailabilityRequest;
import com.dlim2012.booking.dto.dates.booking.AddBookingRoomRequest;
import com.dlim2012.booking.dto.dates.booking.DeleteBookingRoomRequest;
import com.dlim2012.booking.dto.dates.booking.EditBookingRoomRequest;
import com.dlim2012.booking.service.booking_entity.BookingService;
import com.dlim2012.booking.service.booking_entity.DatesService;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import com.dlim2012.clients.mysql_booking.repository.DatesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {

    private final DatesService datesService;
    private final BookingService bookingService;

    private final DatesRepository datesRepository;
    private final BookingRepository bookingRepository;


    public void addDatesByHotelManager(Integer hotelId, Integer hotelManagerId, AddAvailabilityRequest request) {
        datesService.addDates(
                UserRole.HOTEL_MANAGER, hotelManagerId,
                hotelId, request.getRoomId(),
                request.getStartDate(), request.getEndDate()
        );
    }

    public void editDatesByHotelManager(Integer hotelId, Integer hotelManagerId, EditAvailabilityRequest request) {
        datesService.editDates(
                UserRole.HOTEL_MANAGER, hotelManagerId,
                hotelId, request.getRoomId(), request.getDatesId(),
                request.getStartDate(), request.getEndDate()
        );
    }

    public void deleteDatesByHotelManager(Integer hotelId, Integer hotelManagerId, DeleteAvailabilityRequest request) {
        datesService.deleteDate(
                UserRole.HOTEL_MANAGER, hotelManagerId,
                hotelId, request.getDatesId()
        );
    }


    public void addBookingRoomByHotelManager(Integer hotelId, Long bookingId, Integer hotelManagerId, AddBookingRoomRequest request) {
        datesService.addBookingRoom(
                UserRole.HOTEL_MANAGER, hotelManagerId,
                hotelId, bookingId,
                request
        );
    }

    public void editBookingRoomByHotelManager(Integer hotelId, Integer hotelManagerId, EditBookingRoomRequest request) {
        datesService.editBookingRoom(
                UserRole.HOTEL_MANAGER, hotelManagerId,
                hotelId,
                request
        );
    }

    public void cancelBookingRoomByHotelManager(Long bookingId, Integer hotelManagerId, DeleteBookingRoomRequest request) {
        datesService.cancelBookingRoom(
                UserRole.HOTEL_MANAGER, hotelManagerId,
                bookingId, request.getBookingRoomId()
        );
    }

    public void cancelBookingByHotel(Long bookingId, Integer userId) {
        Booking booking = bookingRepository.findByIdAndHotelManagerId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        bookingService.cancelBooking(booking, BookingStatus.CANCELLED_BY_HOTEL_MANAGER);
    }

}

