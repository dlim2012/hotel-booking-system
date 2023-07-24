package com.dlim2012.bookingmanagement;

import com.dlim2012.clients.cassandra.repository.BookingArchiveByHotelIdRepository;
import com.dlim2012.clients.cassandra.repository.BookingArchiveByUserIdRepository;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component

public class BookingManagementRunner implements CommandLineRunner {

    private final BookingRepository bookingRepository;
    private final BookingArchiveByUserIdRepository bookingArchiveByUserIdRepository;
    private final BookingArchiveByHotelIdRepository bookingArchiveByHotelIdRepository;

    @Override
    public void run(String... args) throws Exception {
//        System.out.println(bookingRepository.findAll());
//        System.out.println(bookingArchiveByUserIdRepository.findAll());
//        System.out.println(bookingArchiveByHotelIdRepository.findAll());
    }

}
