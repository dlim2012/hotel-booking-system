package com.dlim2012.booking.service;

import com.dlim2012.clients.dto.booking.BookingItem;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingKafkaProducerService {

    private final KafkaTemplate<String, BookingItem> bookingItemKafkaTemplate;
    private final ModelMapper modelMapper = new ModelMapper();

    public void sendBookingItem(Booking booking, String userEmail){
        BookingItem bookingItem = modelMapper.map(booking, BookingItem.class);
        bookingItemKafkaTemplate.send("booking", bookingItem);
    }
}
