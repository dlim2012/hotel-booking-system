package com.dlim2012.searchconsumer;


import com.dlim2012.ElasticSearchQuery;
import com.dlim2012.dto.HotelFullAddressItem;
import com.dlim2012.dto.RoomItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListeners {

    private final ElasticSearchQuery elasticSearchQuery;

    @KafkaListener(topics="hotel", containerFactory = "hotelFullAddressItemKafkaListenerContainerFactory", groupId = "hotelGroupId")
    void hotelListener(@Validated({HotelFullAddressItem.SearchConsumerValidation.class})
                       HotelFullAddressItem hotelFullAddressItem) throws IOException {
        log.info("Listener received: {} with id {} (is_active: {})", "hotel", hotelFullAddressItem.id(), hotelFullAddressItem.isActive());
        if (hotelFullAddressItem.isActive() != null && hotelFullAddressItem.isActive()){
            elasticSearchQuery.createOrUpdateDocument(
                    "hotel",
                    hotelFullAddressItem.id().toString(),
                    hotelFullAddressItem
            );
        } else {
            elasticSearchQuery.deleteDocumentById("hotel", hotelFullAddressItem.id().toString());
        }
    }


    @KafkaListener(topics="room", containerFactory = "roomItemKafkaListenerContainerFactory", groupId = "roomGroupId")
    void roomListener(@Validated({RoomItem.SearchConsumerValidation.class}) RoomItem roomItem) throws IOException {
        System.out.println("Listener received: " + roomItem);
        log.info("Listener received: {} with id {} (is_active: {})", "hotel", roomItem.id(), roomItem.isActive());
        if (roomItem.isActive() != null && roomItem.isActive()){
            elasticSearchQuery.createOrUpdateDocument(
                    "room",
                    roomItem.id().toString(),
                    roomItem
            );
        } else {
            elasticSearchQuery.deleteDocumentById("hotel", roomItem.id().toString());
        }
    }
}
