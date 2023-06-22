package com.dlim2012.booking.service;

import com.dlim2012.clients.mysql_booking.entity.Room;
import com.dlim2012.clients.dto.booking.BookingItem;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class PriceService {

    public Long getPriceInCents(
            BookingItem bookingItem,
            Room room){
        Long days = ChronoUnit.DAYS.between(bookingItem.getStartDateTime().toLocalDate(),
                bookingItem.getEndDateTime().toLocalDate()) + 1;
        Double averagePrice = (room.getPriceMax() + room.getPriceMin()) / 2 * 100;
        double price = days * averagePrice * bookingItem.getQuantity();
        return (Long) (long) (price * 100);
    }
}
