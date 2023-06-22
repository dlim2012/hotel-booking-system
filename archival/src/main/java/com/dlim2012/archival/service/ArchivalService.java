package com.dlim2012.archival.service;

import com.dlim2012.clients.cassandra.entity.BookingArchive;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByHotelId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByRoomId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByHotelIdRepository;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByRoomIdRepository;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByUserIdRepository;
import com.dlim2012.clients.cassandra.repository.BookingArchiveRepository;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArchivalService {

    // sql repositories
    private final BookingRepository bookingRepository;

    // nosql repositories
    private final BookingArchiveRepository bookingArchiveRepository;
    private final BookingArchiveByUserIdRepository bookingArchiveByUserIdRepository;
    private final BookingArchiveByHotelIdRepository bookingArchiveByHotelIdRepository;
    private final BookingArchiveByRoomIdRepository bookingArchiveByRoomIdRepository;

    final ModelMapper modelMapper = new ModelMapper();

    private Map<BookingStatus, BookingMainStatus> statusMap = Map.ofEntries(
            Map.entry(BookingStatus.RESERVED, BookingMainStatus.RESERVED),
            Map.entry(BookingStatus.BOOKED, BookingMainStatus.BOOKED),
            Map.entry(BookingStatus.CANCELLED_PAYMENT_FAIL, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_PAYMENT_TIME_EXPIRED, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_BY_APP_USER, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_BY_HOTEL_MANAGER, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.CANCELLED_BY_ADMIN, BookingMainStatus.CANCELLED),
            Map.entry(BookingStatus.COMPLETED, BookingMainStatus.COMPLETED)
    );

    public void saveBookingArchive(List<Booking> bookingList){
        List<BookingArchive> bookingArchiveList = new ArrayList<>();
        List<BookingArchiveByUserId> bookingArchiveByUserIdList = new ArrayList<>();
        List<BookingArchiveByHotelId> bookingArchiveByHotelIdList = new ArrayList<>();
        List<BookingArchiveByRoomId> bookingArchiveByRoomIdList = new ArrayList<>();
        for (Booking booking: bookingList) {

            BookingMainStatus bookingMainStatus = statusMap.get(booking.getStatus());

            BookingArchive bookingArchive = modelMapper.map(booking, BookingArchive.class);

            BookingArchiveByUserId bookingArchiveByUserId = BookingArchiveByUserId.builder()
                    .userId(booking.getUserId())
                    .mainStatus(bookingMainStatus)
                    .endDateTime(booking.getEndDateTime())
                    .bookingId(booking.getId())
                    .build();

            BookingArchiveByHotelId bookingArchiveByHotelId = BookingArchiveByHotelId.builder()
                    .hotelId(booking.getHotelId())
                    .mainStatus(bookingMainStatus)
                    .endDateTime(booking.getEndDateTime())
                    .build();
            BookingArchiveByRoomId bookingArchiveByRoomId = BookingArchiveByRoomId.builder()
                    .roomId(booking.getRoomId())
                    .mainStatus(bookingMainStatus)
                    .endDateTime(booking.getEndDateTime())
                    .bookingId(booking.getId())
                    .build();

            bookingArchiveList.add(bookingArchive);
            bookingArchiveByUserIdList.add(bookingArchiveByUserId);
            bookingArchiveByHotelIdList.add(bookingArchiveByHotelId);
            bookingArchiveByRoomIdList.add(bookingArchiveByRoomId);
        }

        bookingArchiveByUserIdRepository.saveAll(bookingArchiveByUserIdList);
        bookingArchiveByHotelIdRepository.saveAll(bookingArchiveByHotelIdList);
        bookingArchiveByRoomIdRepository.saveAll(bookingArchiveByRoomIdList);
        bookingArchiveRepository.saveAll(bookingArchiveList);
        bookingRepository.deleteAll(bookingList);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void archiveCompleted(LocalDate endDate){
        log.info("Archiving completed.");
        List<Booking> bookingList = bookingRepository.findByStatusAndBeforeEndDate(BookingStatus.BOOKED, endDate);
        for (Booking booking: bookingList){
            booking.setStatus(BookingStatus.COMPLETED);
        }
        saveBookingArchive(bookingList);
    }

    public void archiveCancelled(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        saveBookingArchive(bookingList);
    }


}
