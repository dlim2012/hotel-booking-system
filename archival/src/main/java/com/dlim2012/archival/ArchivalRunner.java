package com.dlim2012.archival;

import com.dlim2012.clients.cassandra.entity.BookingArchive;
import com.dlim2012.clients.cassandra.repository.BookingArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ArchivalRunner implements CommandLineRunner {

//    private final CassandraOperations cassandraOperations;
    private final BookingArchiveRepository bookingArchiveRepository;

    @Override
    public void run(String... args) throws Exception {
        BookingArchive bookingArchive = BookingArchive.builder()
                .bookingId(-1L)
                .hotelId(-1)
                .roomId(-1)
                .startDateTime(LocalDate.now().atTime(12, 0))
                .endDateTime(LocalDate.now().atTime(12, 0))
                .quantity(1)
                .priceInCents(10000L)
                .invoiceId("invoiceId")
                .invoiceConfirmTime(LocalDateTime.now())
                .build();
        bookingArchiveRepository.save(bookingArchive);

        System.out.println("Archival Runner");

//        cassandraOperations.insert(bookingArchive);
//        System.out.println(cassandraOperations.selectOne(
//                Query.query(Criteria.where("id").is(bookingArchive.getId())),
//                BookingArchive.class
//        ));
    }
}
