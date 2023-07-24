package com.dlim2012.bookingmanagement.service;

import com.dlim2012.clients.cassandra.entity.BookingArchiveByHotelId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByHotelIdRepository;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByUserIdRepository;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CassandraService {


    private final BookingArchiveByUserIdRepository bookingArchiveByUserIdRepository;
    private final BookingArchiveByHotelIdRepository bookingArchiveByHotelIdRepository;


    public List<BookingArchiveByUserId> asyncQueryBookingArchiveByUserId(Integer userId, BookingMainStatus status, LocalDate startDate, LocalDate endDate){

        LocalDateTime startDateTime = startDate.atTime(0, 0);
        if (endDate == null){
            return bookingArchiveByUserIdRepository
                    .findByUserIdAndMainStatusAndEndDateTimeGreaterThanEqual(
                            userId, status, startDateTime);
        } else {
            LocalDateTime endDateTime = endDate.atTime(0, 0);
            return bookingArchiveByUserIdRepository
                    .findByUserIdAndMainStatusAndEndDateTimeGreaterThanEqualAndEndDateTimeLessThan(
                            userId, status, startDateTime, endDateTime
                    );
        }
    }

    public List<BookingArchiveByHotelId> asyncQueryBookingArchiveByHotelId(Integer hotelId, BookingMainStatus status, LocalDate startDate, LocalDate endDate){
        List<BookingArchiveByHotelId> bookingArchiveByUserIdList;
        LocalDateTime startDateTime = startDate.atTime(0, 0);
        if (endDate == null){
            return bookingArchiveByHotelIdRepository
                    .findByHotelIdAndMainStatusAndEndDateTimeGreaterThanEqual(
                            hotelId, status, startDateTime);
        } else {
            LocalDateTime endDateTime = endDate.atTime(0, 0);
            return bookingArchiveByHotelIdRepository
                    .findByHotelIdAndMainStatusAndEndDateTimeGreaterThanEqualAndEndDateTimeLessThan(
                            hotelId, status, startDateTime, endDateTime
                    );
        }
    }


    public BookingArchiveByUserId getBookingArchiveByUserId(Integer userId, BookingMainStatus status, LocalDate endDate, Long bookingId) {
        List<BookingArchiveByUserId> bookingArchiveByUserIdList = asyncQueryBookingArchiveByUserId(
                userId, status, endDate, endDate.plusDays(1)
        );
        for (BookingArchiveByUserId bookingArchiveByUserId: bookingArchiveByUserIdList){
            if (bookingArchiveByUserId.getBookingId().equals(bookingId)){
                return bookingArchiveByUserId;
            }
        }
        throw new ResourceNotFoundException("BookingArchiveByUserId not found.");
    }
}
