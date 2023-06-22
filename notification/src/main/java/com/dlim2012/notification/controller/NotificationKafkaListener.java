package com.dlim2012.notification.controller;

import com.dlim2012.clients.dto.booking.BookingItem;
import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.security.service.JwtService;
import com.dlim2012.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationKafkaListener {

    private final NotificationService notificationService;

//    @KafkaListener(topics="room", containerFactory = "roomItemKafkaListenerContainerFactory", groupId = "room-notification")
//    void roomListener(@Validated({RoomItem.SearchConsumer.class}) RoomItem roomItem) throws IOException {
//        log.info("Listener received: {} with id {} (is_active: {})", "room", roomItem.getId(), roomItem.getIsActive());
//        notificationService.sendMail(roomItem.getManagerEmail(), "room", roomItem.toString());
//    }
//
//    @KafkaListener(topics="booking", containerFactory = "bookingItemKafkaListenerContainerFactory", groupId = "booking-notification")
//    void bookingListener(BookingItem bookingItem) throws IOException {
//        log.info("Listener received: {} with id {} (status: {})", "booking", bookingItem.getId(), bookingItem.getStatus().name());
//        notificationService.sendMail(bookingItem.getUserEmail(), "booking", bookingItem.toString());
//    }
}
