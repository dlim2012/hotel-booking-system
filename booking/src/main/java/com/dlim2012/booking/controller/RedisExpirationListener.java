package com.dlim2012.booking.controller;

import com.dlim2012.booking.service.booking.BookingService;
import com.dlim2012.clients.entity.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import static com.dlim2012.clients.cache.CacheConfig.bookingIdKeyName;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener {

    private final BookingService bookingService;

    private final Integer beginIndex = bookingIdKeyName.length() + 2;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        String channel = new String(message.getChannel());
        log.info("Received task timeout event: {} for key: {}", body, channel);

        if (!body.startsWith(bookingIdKeyName)) {
            return;
        }
        Long bookingId = Long.valueOf(body.substring(beginIndex));
        bookingService.cancelIfStatusEquals(bookingId, BookingStatus.RESERVED, BookingStatus.CANCELLED_PAYMENT_TIME_EXPIRED);
    }
}
