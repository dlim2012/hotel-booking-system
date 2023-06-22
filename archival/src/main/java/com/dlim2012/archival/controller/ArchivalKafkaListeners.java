package com.dlim2012.archival.controller;

import com.dlim2012.archival.service.ArchivalService;
import com.dlim2012.clients.dto.booking.BookingItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ArchivalKafkaListeners {

    private final ArchivalService archivalService;

//    @KafkaListener(topics="room", containerFactory = "roomItemKafkaListenerContainerFactory", groupId = "room-archival")
//    void roomListener(@Validated({RoomItem.SearchConsumer.class}) RoomItem roomItem) throws IOException {
//        log.info("Listener received: {} with id {} (is_active: {})", "room", roomItem.getId(), roomItem.getIsActive());
//
//    }

    @KafkaListener(topics="booking", containerFactory = "bookingItemKafkaListenerContainerFactory", groupId = "booking-archival")
    void bookingListener(BookingItem bookingItem) throws IOException {
        log.info("Listener received: {} with id {} (status: {})", "booking", bookingItem.getId(), bookingItem.getStatus().name());
        if (bookingItem.getStatus().name().contains("CANCELLED")){
            archivalService.archiveCancelled(bookingItem.getId());
        }
    }
}
