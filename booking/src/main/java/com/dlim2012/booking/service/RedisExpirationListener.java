package com.dlim2012.booking.service;

import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener {

    private final BookingService bookingService;
    private final PaypalService paypalService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        String channel = new String(message.getChannel());
        log.info("Received task timeout event: {} for key: {}", body, channel);
        Booking booking;
        try {
            booking = objectMapper.readValue(message.toString(), Booking.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return;
        }
        bookingService.revertBook(booking, BookingStatus.CANCELLED_PAYMENT_FAIL);
    }
}
