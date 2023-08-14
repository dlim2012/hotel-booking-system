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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

//    @Cacheable(cacheNames = "booking")
    public List<Booking> asyncBookingByUserIdAndKeys(Integer userId, BookingMainStatus status, LocalDate startDate, LocalDate endDate){
        if (endDate == null){
            return bookingRepository.findByUserIdAndMainStatusAndEndDate(userId, status, startDate);
        } else {
            return bookingRepository.findByUserIdAndMainStatusAndEndDateRange(userId, status, startDate, endDate);
        }
    }

//    @Cacheable(cacheNames = "booking")
    public List<Booking> asyncBookingByHotelIdAndKeys(Integer hotelId, BookingMainStatus status, LocalDate startDate, LocalDate endDate){
        if (endDate == null){
            return bookingRepository.findByHotelIdAndMainStatusAndDate(hotelId, status, startDate);
        } else {
            return bookingRepository.findByHotelIdAndMainStatusAndDateRange(hotelId, status, startDate, endDate);
        }
    }

//    @Cacheable(cacheNames = "booking")
    public List<Booking> asyncBookingByHotelIdAndReserved(Integer hotelId){
        return bookingRepository.findByHotelIdAndTwoMainStatus(hotelId, BookingMainStatus.RESERVED, BookingMainStatus.BOOKED);
    }

    @Cacheable(cacheNames = "booking", key="#bookingId")
    public Booking findBookingById(Long bookingId){
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }


//    @Cacheable(cacheNames = "hotel")
    public Hotel asyncFindHotel(Integer hotelId){
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
    }

//    @Cacheable(cacheNames = "hotel", key="{#hotelId, #hotelManagerId}")
    public Hotel asyncFindHotel(Integer hotelId, Integer hotelManagerId){
        return hotelRepository.findByIdAndHotelManagerId(hotelId, hotelManagerId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
    }

    public List<Dates> asyncDatesByHotel(Integer hotelId) {
        return datesRepository.findByHotelId(hotelId);
    }

    @Cacheable(cacheNames = "hotel-booking", key="{#bookingId, #hotelManagerId}")
    public Booking getBookingByHotel(Long bookingId, Integer hotelManagerId){
        return bookingRepository.findByIdAndHotelManagerId(bookingId, hotelManagerId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }

    @Cacheable(cacheNames = "user-booking", key="{#bookingId, #userId}")
    public Booking getBookingByAppUser(Long bookingId, Integer userId) {
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }


    @CachePut(cacheNames = "user-booking", key="{#booking.id, #booking.userId}")
    @CacheEvict(cacheNames = "hotel-booking", key="{#booking.id, #booking.hotelManagerId}")
    public Booking saveBookingByAppUser(Booking booking) {
        bookingRepository.save(booking);
        return booking;
    }

    @CachePut(cacheNames = "hotel-booking", key="{#booking.id, #booking.hotelManagerId}")
    @CacheEvict(cacheNames = "user-booking", key="{#booking.id, #booking.userId}")
    public Booking saveBookingByHotelManager(Booking booking) {
        bookingRepository.save(booking);
        return booking;
    }


}
