package com.dlim2012.booking;

import com.dlim2012.booking.service.UserService;
import com.dlim2012.booking.service.booking_entity.hotel_entity.RoomsEntityService;
import com.dlim2012.clients.kafka.dto.archive.BookingIdArchiveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookingRunner implements CommandLineRunner {

//    private final RedisTemplate<String, Object> redisTemplate;
    // docker exec -it hotel-booking-redis-server redis-cli
    private final RoomsEntityService roomsEntityService;
    private final UserService userService;
//    private final Receiver receiver;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, BookingIdArchiveRequest> bookingIdArchiveKafkaTemplate;

    @Override
    public void run(String... args) throws Exception {

//        System.out.println("run finished");
    }
}
