package com.dlim2012.booking.service.booking_entity.utils;


import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.BookingRoom;
import com.dlim2012.clients.mysql_booking.entity.BookingRooms;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingUtilsService {
    private final BookingRepository bookingRepository;

    public boolean bookingIsActive(Booking booking){
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                if (bookingRoom.getStatus().equals(BookingStatus.RESERVED)
                        || bookingRoom.getStatus().equals(BookingStatus.BOOKED)
                ){
                    return true;
                }
            }
        }
        return false;
    }


    public void recalculateBookingPriceTimeAndSave(Booking booking){

        LocalDateTime startDateTime = LocalDateTime.MAX;
        LocalDateTime endDateTime = LocalDateTime.MIN;
        Long priceInCents = 0L;


        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                if (bookingRoom.getStartDateTime().isBefore(startDateTime)){
                    startDateTime = bookingRoom.getStartDateTime();
                }
                if (bookingRoom.getEndDateTime().isAfter(endDateTime)){
                    endDateTime = bookingRoom.getEndDateTime();
                }
                if (bookingRoom.getStatus().equals(BookingStatus.BOOKED)
                        || bookingRoom.getStatus().equals(BookingStatus.RESERVED)
                        || bookingRoom.getStatus().equals(BookingStatus.RESERVED_FOR_TIMEOUT)){
                    priceInCents += bookingRooms.getPricePerRoomInCents();
                }
            }
        }

        booking.setStartDateTime(startDateTime);
        booking.setEndDateTime(endDateTime);
        booking.setPriceInCents(priceInCents);

        bookingRepository.save(booking);
    }
}
