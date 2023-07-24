package com.dlim2012.archival.controller;

import com.dlim2012.archival.service.ArchivalService;
import com.dlim2012.clients.kafka.dto.archive.BookingIdArchiveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

    @KafkaListener(topics="booking-archive", containerFactory = "bookingIdArchiveListenerContainerFactory", groupId = "archival")
    void bookingListener(BookingIdArchiveRequest request) throws IOException {
        log.info("Listener received booking-archive request for bookings {}", request.getBookingIds());
        archivalService.archiveBooking(request);
    }
}
