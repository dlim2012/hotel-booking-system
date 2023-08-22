package com.dlim2012.booking.controller;


import com.dlim2012.booking.service.UserService;
import com.dlim2012.booking.service.booking_entity.hotel_entity.HotelEntityService;
import com.dlim2012.booking.service.booking_entity.hotel_entity.RoomsEntityService;
import com.dlim2012.booking.service.booking_entity.utils.PaypalService;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDetails;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingInActivateRequest;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDetails;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingInActivateRequest;
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

    @KafkaListener(topics="hotel-booking-inactivate", containerFactory = "hotelBookingDeleteKafkaListenerContainerFactory", groupId="hotel-booking-delete")
    void hotelDeleteListener(HotelBookingDeleteRequest request){
        log.info("Kafka Listener received: \"hotel-booking-delete\" with id {}", request.getHotelId());
        hotelEntityService.deleteHotel(request);
    }

    @KafkaListener(topics="hotel-booking-delete", containerFactory = "hotelBookingInactivateKafkaListenerContainerFactory", groupId="hotel-booking-delete")
    void hotelInactivateListener(HotelBookingInActivateRequest request){
        log.info("Kafka Listener received: \"hotel-booking-inactivate\" with id {}", request.getHotelId());
        // todo
        hotelEntityService.inactivateHotel(request);
    }

    // This does not update whether room is active. If rooms doesn't exist, rooms will be registered with isActive = true
    @KafkaListener(topics="rooms-booking", containerFactory = "roomsRegisterListenerContainerFactory", groupId="rooms-booking")
    void roomsListener(RoomsBookingDetails roomsBookingDetails){
        log.info("Kafka Listener received: \"rooms-booking\" for hotel {} with id {}", roomsBookingDetails.getHotelId(), roomsBookingDetails.getRoomsId());
        roomsEntityService.updateRooms(roomsBookingDetails);
    }

//    @KafkaListener(topics="rooms-booking-activate", containerFactory = "roomsBookingActivateListenerContainerFactory", groupId="rooms-booking")
//    void roomsActivateListener(RoomsBookingActivateRequest request){
//        log.info("Kafka Listener received: \"rooms-booking-activate\" for hotel {} with id {}", request.getHotelId(), request.getRoomsId());
//        roomsEntityService.activateRooms(request);
//    }

    @KafkaListener(topics="rooms-booking-inactivate", containerFactory = "roomsBookingInactivateListenerContainerFactory", groupId="rooms-booking")
    void roomsInactivateListener(RoomsBookingInActivateRequest request){
        log.info("Kafka Listener received: \"rooms-booking-inactivate\" for hotel {} with id {}", request.getHotelId(), request.getRoomsId());
        roomsEntityService.inactivateRooms(request);
    }

    @KafkaListener(topics="rooms-booking-delete", containerFactory = "roomsBookingDeleteListenerContainerFactory", groupId="rooms-booking-delete")
    void roomsDeleteListener(RoomsBookingDeleteRequest request){
        log.info("Kafka Listener received: \"rooms-booking-delete\" for hotel {} with id {}", request.getHotelId(), request.getRoomsId());
        System.out.println(request);
        roomsEntityService.deleteRooms(request);
    }


}
