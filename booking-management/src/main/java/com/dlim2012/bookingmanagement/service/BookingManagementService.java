package com.dlim2012.bookingmanagement.service;

import com.dlim2012.clients.cassandra.entity.BookingArchive;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByRoomIdRepository;
import com.dlim2012.clients.entity.BookingEntity;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByHotelIdRepository;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByUserIdRepository;
import com.dlim2012.clients.cassandra.repository.BookingArchiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingManagementService {

    private final BookingArchiveRepository bookingArchiveRepository;
    private final BookingArchiveByUserIdRepository bookingArchiveByUserIdRepository;
    private final BookingArchiveByHotelIdRepository bookingArchiveByHotelIdRepository;
    private final BookingArchiveByRoomIdRepository bookingArchiveByRoomIdRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public List<BookingEntity> getBookingsByUserId(Integer userId, BookingMainStatus bookingMainStatus, Integer months){
        List<BookingEntity> bookingList = new ArrayList<>();
        bookingArchiveByUserIdRepository.findByUserIdAndMainStatusAndEndDateTimeGreaterThan(
                userId, bookingMainStatus, LocalDate.now().atTime(0, 0).minusMonths(months)
        ).forEach(entity ->  {
                Optional<BookingArchive> optionalBookingArchive = bookingArchiveRepository.findById(entity.getBookingId());
                optionalBookingArchive.ifPresent(bookingList::add);
            }
        );
        return bookingList;
    }

    public List<BookingEntity> getBookingsByHotelId(Integer hotelId, BookingMainStatus bookingMainStatus, Integer months){
        List<BookingEntity> bookingList = new ArrayList<>();
        bookingArchiveByHotelIdRepository.findByHotelIdAndMainStatusAndEndDateTimeGreaterThan(
                hotelId, bookingMainStatus, LocalDate.now().atTime(0, 0).minusMonths(months)
        ).forEach(entity ->  {
                    Optional<BookingArchive> optionalBookingArchive = bookingArchiveRepository.findById(entity.getBookingId());
                    optionalBookingArchive.ifPresent(bookingList::add);
                }
        );
        return bookingList;
    }

    public List<BookingEntity> getBookingsByRoomId(Integer hotelId, Integer roomId, BookingMainStatus bookingMainStatus, Integer months){
        List<BookingEntity> bookingList = new ArrayList<>();
        bookingArchiveByRoomIdRepository.findByRoomIdAndMainStatusAndEndDateTimeGreaterThan(
                roomId, bookingMainStatus, LocalDate.now().atTime(0, 0).minusMonths(months)
        ).forEach(entityByRoomId ->  {
                    Optional<BookingArchive> optionalBookingArchive = bookingArchiveRepository.findById(entityByRoomId.getBookingId());
                    optionalBookingArchive.ifPresent(entity -> {
                        if (entity.getHotelId().equals(hotelId)){
                            bookingList.add(entity);
                        }
                    });
                }
        );
        return bookingList;
    }
}
