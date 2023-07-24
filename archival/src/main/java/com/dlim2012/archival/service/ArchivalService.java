package com.dlim2012.archival.service;

import com.dlim2012.clients.cassandra.entity.BookingArchiveByHotelId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveRoom;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByHotelIdRepository;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByUserIdRepository;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.kafka.dto.archive.BookingIdArchiveRequest;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.BookingRoom;
import com.dlim2012.clients.mysql_booking.entity.BookingRooms;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ArchivalService {

    // sql repositories
    private final BookingRepository bookingRepository;

    // nosql repositories
    private final BookingArchiveByUserIdRepository bookingArchiveByUserIdRepository;
    private final BookingArchiveByHotelIdRepository bookingArchiveByHotelIdRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    private final LocalDateTime MINUTES_FROM = LocalDateTime.of(1970, 1, 1, 0, 0);

    public ArchivalService(BookingRepository bookingRepository, BookingArchiveByUserIdRepository bookingArchiveByUserIdRepository, BookingArchiveByHotelIdRepository bookingArchiveByHotelIdRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingArchiveByUserIdRepository = bookingArchiveByUserIdRepository;
        this.bookingArchiveByHotelIdRepository = bookingArchiveByHotelIdRepository;

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Long localDateTimeToLong(LocalDateTime localDateTime){
        return ChronoUnit.MINUTES.between(MINUTES_FROM, localDateTime);
    }

    public LocalDateTime longToLocalDateTime(Long minutes){
        return MINUTES_FROM.plusMinutes(minutes);
    }


    public BookingArchiveByUserId getBookingArchiveByUserIdFromBooking(Booking booking){
        BookingArchiveByUserId bookingArchiveByUserId = modelMapper.map(booking, BookingArchiveByUserId.class);
        List<BookingArchiveRoom> bookingArchiveRoomList = new ArrayList<>();
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                BookingArchiveRoom bookingArchiveRoom = BookingArchiveRoom.builder()
                        .roomsId(bookingRooms.getRoomsId())
                        .roomsName(bookingRooms.getRoomsDisplayName())
                        .roomId(bookingRoom.getRoomId())
                        .startDateTime(bookingRoom.getStartDateTime())
                        .endDateTime(bookingRoom.getEndDateTime())
                        .build();
                bookingArchiveRoomList.add(bookingArchiveRoom);
            }
        }
        bookingArchiveByUserId.setRooms(bookingArchiveRoomList);
        bookingArchiveByUserId.setBookingId(booking.getId());
        return bookingArchiveByUserId;
    }

    public BookingArchiveByHotelId getBookingArchiveByHotelIdFromBooking(Booking booking){
        BookingArchiveByHotelId bookingArchiveByHotelId = modelMapper.map(booking, BookingArchiveByHotelId.class);
        List<BookingArchiveRoom> bookingArchiveRoomList = new ArrayList<>();
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                BookingArchiveRoom bookingArchiveRoom = BookingArchiveRoom.builder()
                        .roomsId(bookingRooms.getRoomsId())
                        .roomsName(bookingRooms.getRoomsDisplayName())
                        .roomId(bookingRoom.getRoomId())
                        .startDateTime(bookingRoom.getStartDateTime())
                        .endDateTime(bookingRoom.getEndDateTime())
                        .build();
                bookingArchiveRoomList.add(bookingArchiveRoom);
            }
        }
        bookingArchiveByHotelId.setRooms(bookingArchiveRoomList);
        bookingArchiveByHotelId.setBookingId(booking.getId());
        return bookingArchiveByHotelId;
    }

    /*
    Save list of Booking to Cassandra
     */
    public void archiveBookingBatch(List<Booking> bookingList){
        List<BookingArchiveByUserId> bookingArchiveByUserIdList = new ArrayList<>();
        List<BookingArchiveByHotelId> bookingArchiveByHotelIdList = new ArrayList<>();

        for (Booking booking: bookingList){

            if (booking.getStatus() == BookingStatus.RESERVED){
//                log.error("Booking status is RESERVED");
                throw new IllegalArgumentException("Booking status is RESERVED");
            }

            if (booking.getStatus() == BookingStatus.BOOKED){
                if (booking.getEndDateTime().isBefore(LocalDateTime.now())){
                    booking.setStatus(BookingStatus.COMPLETED);
                }
            }

            bookingArchiveByUserIdList.add(getBookingArchiveByUserIdFromBooking(booking));
            bookingArchiveByHotelIdList.add(getBookingArchiveByHotelIdFromBooking(booking));
        }

        bookingArchiveByUserIdRepository.saveAll(bookingArchiveByUserIdList);
        bookingArchiveByHotelIdRepository.saveAll(bookingArchiveByHotelIdList);
        bookingRepository.deleteAll(bookingList);
    }

    public void archiveBooking(BookingIdArchiveRequest request){
        List<Booking> bookingToArchive = bookingRepository.findByIds(request.getBookingIds());
        archiveBookingBatch(bookingToArchive);
    }


    public void archiveCompleted(LocalDate endDate){
        log.info("Archiving completed.");
        List<Booking> bookingList = bookingRepository.findByStatusAndBeforeEndDate(BookingStatus.BOOKED, endDate);
        for (Booking booking: bookingList){
            booking.setStatus(BookingStatus.COMPLETED);
        }
        archiveBookingBatch(bookingList);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void archiveCompletedCron(){
        archiveCompleted(LocalDate.now());
    }

//    public void archiveMonthlyRecords(Integer hotelId, Integer year, Integer month){
//        // get bookings with end date in the month
//        LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
//        LocalDateTime endDateTime = startDateTime.plusMonths(1);
//        bookingArchiveByHotelIdRepository.findByHotelIdAndMainStatusAndEndDateTimeGreaterThanEqualAndEndDateTimeLessThan(
//                hotelId, BookingMainStatus.COMPLETED, startDateTime, endDateTime
//        );
//
//        // get bookings with any booked nights in the month
//    }
//
//    @Scheduled(cron = "0 0 12 1 * ?")
//    public void archiveMonthylRecordsCron(){
//    }

}
