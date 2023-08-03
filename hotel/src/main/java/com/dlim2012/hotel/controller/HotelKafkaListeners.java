package com.dlim2012.hotel.controller;

import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDetails;
import com.dlim2012.clients.kafka.dto.user.DeleteUserRequest;
import com.dlim2012.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotelKafkaListeners {

    private final HotelService hotelService;

    @KafkaListener(topics="delete-user", containerFactory = "deleteUserKafkaListenerContainerFactory", groupId = "hotel-delete-user")
    void deleteUserListener(DeleteUserRequest request){
        log.info("Kafka Listener received: \"delete-user\" for user {}", request.getUserId());
        hotelService.deleteAllHotelByHotelManager(request.getUserId());
    }
}
