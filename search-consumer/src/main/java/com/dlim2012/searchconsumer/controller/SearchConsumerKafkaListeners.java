package com.dlim2012.searchconsumer.controller;


import com.dlim2012.clients.dto.booking.BookingItem;
import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.dto.hotel.facility.FacilityItem;
import com.dlim2012.clients.dto.hotel.facility.HotelFacilitiesItem;
import com.dlim2012.clients.dto.hotel.facility.RoomFacilitiesItem;
import com.dlim2012.clients.elasticsearch.service.ElasticSearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class SearchConsumerKafkaListeners {

    private final ElasticSearchQuery elasticSearchQuery;

    @KafkaListener(topics="hotel", containerFactory = "hotelItemKafkaListenerContainerFactory", groupId = "hotel-searchconsumer")
    void hotelListener(@Validated({HotelItem.SearchConsumer.class})
                       HotelItem hotelItem) throws IOException {
        log.info("Listener received: {} with id {}", "hotel", hotelItem.getId());
        if (hotelItem.getIsActive() != null) {
            elasticSearchQuery.createOrUpdateDocument(
                    "hotel",
                    hotelItem.getId().toString(),
                    hotelItem
            );
        } else {
            elasticSearchQuery.deleteDocumentById("hotel", hotelItem.getId().toString());
        }
    }

    @KafkaListener(topics="room", containerFactory = "roomItemKafkaListenerContainerFactory", groupId = "room-searchconsumer")
    void roomListener(@Validated({RoomItem.SearchConsumer.class}) RoomItem roomItem) throws IOException {
        log.info("Listener received: {} with id {} (is_active: {})", "room", roomItem.getId(), roomItem.getIsActive());
        if (roomItem.getIsActive() != null && roomItem.getIsActive()){
            elasticSearchQuery.createOrUpdateDocument(
                    "room",
                    roomItem.getId().toString(),
                    roomItem
            );
        } else {
            elasticSearchQuery.deleteDocumentById("room", roomItem.getId().toString());
        }
    }

    @KafkaListener(topics="facility", containerFactory = "facilityItemKafkaListenerContainerFactory", groupId = "room-searchconsumer")
    void facilityListener(FacilityItem facilityItem) throws IOException {
        log.info("Listener received: {} with id {} (display_name: {})", "facility", facilityItem.getId(), facilityItem.getDisplayName());
    }

    @KafkaListener(topics="hotel-facilities", containerFactory = "hotelFacilitiesItemListenerContainerFactory", groupId = "room-searchconsumer")
    void hotelFacilitiesListener(HotelFacilitiesItem hotelFacilitiesItem) throws IOException {
        log.info("Listener received: {} for hotel id {} (count: {})",
                "hotel-facilities", hotelFacilitiesItem.getHotelId(),
                hotelFacilitiesItem.getFacilityIds().size());
    }

    @KafkaListener(topics="room-facilities", containerFactory = "roomFacilitiesItemListenerContainerFactory", groupId = "room-searchconsumer")
    void roomFacilitiesListener(RoomFacilitiesItem roomFacilitiesItem) throws IOException {
        log.info("Listener received: {} for room id {} (count: {})",
                "room-facilities", roomFacilitiesItem.getRoomId(),
                roomFacilitiesItem.getFacilityIds().size());
    }

    @KafkaListener(topics="booking", containerFactory = "bookingItemKafkaListenerContainerFactory", groupId = "room-searchconsumer")
    void facilityListener(BookingItem bookingItem) throws IOException {
        log.info("Listener received: {} with id {} (room_id: {})", "facility", bookingItem.getId(), bookingItem.getRoomId());
    }

}
