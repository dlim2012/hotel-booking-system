package com.dlim2012.bookingmanagement.service;

import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.BookingRoom;
import com.dlim2012.clients.mysql_booking.entity.Dates;
import com.dlim2012.clients.mysql_booking.entity.Hotel;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import com.dlim2012.clients.mysql_booking.repository.BookingRoomRepository;
import com.dlim2012.clients.mysql_booking.repository.DatesRepository;
import com.dlim2012.clients.mysql_booking.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MySqlService {
    private final BookingRepository bookingRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final HotelRepository hotelRepository;
    private final DatesRepository datesRepository;
    private final RecordMapper recordMapper;


    public List<Booking> asyncBookingByUserIdAndKeys(Integer userId, BookingMainStatus status, LocalDate startDate, LocalDate endDate){
        if (endDate == null){
            return bookingRepository.findByUserIdAndMainStatusAndEndDate(userId, status, startDate);
        } else {
            return bookingRepository.findByUserIdAndMainStatusAndEndDateRange(userId, status, startDate, endDate);
        }
    }

    public List<Booking> asyncBookingByHotelIdAndKeys(Integer hotelId, BookingMainStatus status, LocalDate startDate, LocalDate endDate){
        if (endDate == null){
            return bookingRepository.findByHotelIdAndMainStatusAndDate(hotelId, status, startDate);
        } else {
            return bookingRepository.findByHotelIdAndMainStatusAndDateRange(hotelId, status, startDate, endDate);
        }
    }

    public List<Booking> asyncBookingByHotelIdAndReserved(Integer hotelId){
        return bookingRepository.findByHotelIdAndTwoMainStatus(hotelId, BookingMainStatus.RESERVED, BookingMainStatus.BOOKED);
    }

    public Booking findBookingById(Long bookingId){
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }


    public Hotel asyncFindHotel(Integer hotelId){
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
    }

    public Hotel asyncFindHotel(Integer hotelId, Integer hotelManagerId){
        return hotelRepository.findByIdAndHotelManagerId(hotelId, hotelManagerId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
    }

    public List<Dates> asyncDatesByHotel(Integer hotelId) {
        return datesRepository.findByHotelId(hotelId);
    }

    public Booking getBookingByHotel(Long bookingId, Integer hotelManagerId){
        return bookingRepository.findByIdAndHotelManagerId(bookingId, hotelManagerId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }

    public Booking getBookingByAppUser(Long bookingId, Integer userId) {
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }

    public BookingRoom getBookingRoomByAppUser(Long bookingId, Long bookingRoomId, Integer userId) {
        return bookingRoomRepository.findByIdAndBookingIdAndUserId(bookingRoomId, bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking room not found."));
    }

    public void saveBooking(Booking booking) {
        bookingRepository.save(booking);
    }

    public void saveBookingRoom(BookingRoom bookingRoom){
        bookingRoomRepository.save(bookingRoom);
    }

}
