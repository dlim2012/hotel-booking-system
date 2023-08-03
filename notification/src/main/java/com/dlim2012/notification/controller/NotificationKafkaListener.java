package com.dlim2012.notification.controller;

import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDetails;
import com.dlim2012.clients.kafka.dto.notification.BookingNotification;
import com.dlim2012.clients.kafka.dto.notification.PaymentNotification;
import com.dlim2012.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationKafkaListener {

    private final NotificationService notificationService;


    @KafkaListener(topics="booking-notification", containerFactory = "bookingNotificationKafkaListenerContainerFactory", groupId = "booking-notification")
    void bookingListener(BookingNotification bookingNotification){
        log.info("Kafka Listener received: \"booking-notification\" for booking {} of hotel {}",
                bookingNotification.getBookingId(), bookingNotification.getHotelId());

    }


    @KafkaListener(topics="payment-notification", containerFactory = "paymentNotificationKafkaListenerContainerFactory", groupId = "payment-notification")
    void paymentListener(PaymentNotification paymentNotification){
        log.info("Kafka Listener received: \"payment-notification\" for booking {} of hotel {}",
                paymentNotification.getBookingId(), paymentNotification.getHotelId());
    }
}
