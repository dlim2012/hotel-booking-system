package com.dlim2012.booking.controller;


import com.dlim2012.booking.service.UserService;
import com.dlim2012.booking.service.booking_entity.hotel_entity.HotelEntityService;
import com.dlim2012.booking.service.booking_entity.hotel_entity.RoomsEntityService;
import com.dlim2012.booking.service.booking_entity.utils.PaypalService;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDetails;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingKafkaListeners {

    private final HotelEntityService hotelEntityService;
    private final RoomsEntityService roomsEntityService;
    private final UserService userService;
    private final PaypalService paypalService;

    @KafkaListener(topics="hotel-booking", containerFactory = "hotelBookingKafkaListenerContainerFactory", groupId = "hotel-booking")
    void hotelListener(HotelBookingDetails hotelBookingDetails){
        log.info("Kafka Listener received: \"hotel-booking\" with id {}", hotelBookingDetails.getHotelId());
        hotelEntityService.addHotel(hotelBookingDetails);
    }

    @KafkaListener(topics="hotel-booking-delete", containerFactory = "hotelSearchDeleteKafkaListenerContainerFactory", groupId="hotel-booking-delete")
    void hotelDeleteListener(HotelBookingDeleteRequest request){
        log.info("Kafka Listener received: \"hotel-booking-delete\" with id {}", request.getHotelId());
        hotelEntityService.deleteHotel(request);
    }

    @KafkaListener(topics="rooms-booking", containerFactory = "roomsRegisterListenerContainerFactory", groupId="rooms-booking")
    void roomsListener(RoomsBookingDetails roomsBookingDetails){
        log.info("Kafka Listener received: \"rooms-booking\" with id {}", roomsBookingDetails.getRoomsId());
        roomsEntityService.updateRooms(roomsBookingDetails);
    }

    @KafkaListener(topics="rooms-booking-delete", containerFactory = "roomsBookingDeleteListenerContainerFactory", groupId="rooms-booking-delete")
    void roomsDeleteListener(RoomsBookingDeleteRequest request){
        log.info("Kafka Listener received: \"rooms-booking-delete\" with id {}", request.getRoomsId());
        System.out.println(request);
        roomsEntityService.deleteRooms(request);
    }


}
