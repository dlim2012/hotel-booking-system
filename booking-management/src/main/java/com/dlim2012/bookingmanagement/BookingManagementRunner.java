package com.dlim2012.bookingmanagement;

import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByUserIdRepository;
import com.dlim2012.clients.entity.BookingStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class BookingManagementRunner implements CommandLineRunner {

    private final BookingArchiveByUserIdRepository bookingArchiveByUserIdRepository;
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;

    @Override
    public void run(String... args) throws Exception {
        testBooking();
        testBookingArchiveByUserId();
        ex();
    }
    void ex(){
//        BookingArchive booking1 = BookingArchive.builder()
//                .userId(-1)
//                .hotelId(-1)
//                .roomId(-1)
//                .status(BookingStatus.COMPLETED)
//                .startDate(LocalDate.now())
//                .endDate(LocalDate.now().plusDays(3))
//                .quantity(1)
//                .priceInCents(10000L)
//                .build();
//        System.out.println(booking1);
//        BookingEntity bookingEntity = (BookingEntity) booking1;
//        System.out.println(bookingEntity);
    }

    void testBooking(){
        Booking booking1 = Booking.builder()
                .userId(-1)
                .hotelId(-1)
                .roomId(-1)
                .status(BookingStatus.COMPLETED)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(3))
                .quantity(1)
                .priceInCents(10000L)
                .build();
        bookingRepository.save(booking1);
        System.out.println(bookingRepository.findAll());
    }

    void testBookingArchiveByUserId(){
        BookingArchiveByUserId bookingArchiveByUserId1 =
                saveBookingArchiveByUserId(-1, -1L,
                        LocalDate.now().minusDays(100));
        BookingArchiveByUserId bookingArchiveByUserId2 =
                saveBookingArchiveByUserId(-1, -2L,
                        LocalDate.now().plusDays(100));
        List<BookingArchiveByUserId> list;
        list = bookingArchiveByUserIdRepository.findAll();
        System.out.println(list.size());
        System.out.println(list);
        LocalDateTime endDate = LocalDateTime.now().plusDays(50);
        System.out.println(endDate);
        list = bookingArchiveByUserIdRepository
                .findByUserIdAndMainStatusAndEndDateTimeGreaterThan(
                        -1, BookingMainStatus.COMPLETED,
                        endDate).collect(Collectors.toList());
        System.out.println(list.size());
        System.out.println(list);

        bookingArchiveByUserIdRepository.delete(bookingArchiveByUserId1);
        bookingArchiveByUserIdRepository.delete(bookingArchiveByUserId2);



    }

    public BookingArchiveByUserId saveBookingArchiveByUserId(
            Integer userId, Long bookingId, LocalDate endDate){
        BookingArchiveByUserId bookingArchiveByUserId =
                BookingArchiveByUserId.builder()
                        .userId(userId)
                        .mainStatus(BookingMainStatus.COMPLETED)
                        .endDateTime(endDate.atTime(12, 0))
                        .bookingId(bookingId)
                        .build();
        bookingArchiveByUserIdRepository.save(bookingArchiveByUserId);
        return bookingArchiveByUserId;
    }
}
