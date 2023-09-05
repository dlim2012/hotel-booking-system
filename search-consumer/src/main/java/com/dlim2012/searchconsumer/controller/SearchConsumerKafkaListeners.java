package com.dlim2012.searchconsumer.controller;


//import com.dlim2012.clients.elasticsearch.service.ElasticSearchQuery;

import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelsNewDayDetails;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchVersion;
import com.dlim2012.searchconsumer.repository.HotelRepository;
import com.dlim2012.searchconsumer.repository.RoomsRepository;
import com.dlim2012.searchconsumer.service.DateService;
import com.dlim2012.searchconsumer.service.IndexingService;
import com.dlim2012.searchconsumer.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class  SearchConsumerKafkaListeners {

    private final IndexingService indexingService;
    private final DateService dateService;
    private final PriceService priceService;



    @KafkaListener(topics="hotel-search", containerFactory = "hotelSearchKafkaListenerContainerFactory", groupId = "hotel-searchconsumer")
    void hotelListener(HotelSearchDetails hotelDetails) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"hotel-search\" with id {}", hotelDetails.getId());
        indexingService.saveHotel(hotelDetails);
    }

    @KafkaListener(topics="hotel-search-delete", containerFactory = "hotelSearchDeleteKafkaListenerContainerFactory", groupId = "hotel-delete-searchconsumer")
    void hotelDeleteListener(HotelSearchDeleteRequest request) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"hotel-search-delete\" with id {}", request.getHotelId());
        indexingService.delete(request);
    }


    @KafkaListener(topics="hotel-new-day", containerFactory = "hotelNewDayKafkaListenerContainerFactory", groupId = "hotel-new-day-searchconsumer")
    void hotelNewDayListener(HotelsNewDayDetails request) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"hotel-new-day\"");
        indexingService.bulkUpdate(request);
    }

    @KafkaListener(topics="rooms-search", containerFactory = "roomsSearchKafkaListenerContainerFactory", groupId = "rooms-searchconsumer")
    void roomsListener(RoomsSearchDetails roomDetails) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"rooms-search\" with id {}", roomDetails.getRoomsId());
        dateService.updateRooms(roomDetails);
    }

    @KafkaListener(topics="rooms-search-version", containerFactory = "roomsSearchVersionKafkaListenerContainerFactory", groupId="rooms-version-searchconsumer")
    void roomsVersionListener(RoomsSearchVersion roomsSearchVersion) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"rooms-search-version\" with id {}", roomsSearchVersion.getRoomsId());
        dateService.updateRoomsVersion(roomsSearchVersion);

    }

    @KafkaListener(topics="rooms-search-delete", containerFactory = "roomsSearchDeleteKafkaListenerContainerFactory", groupId = "rooms-delete-searchconsumer")
    void roomsDeleteListener(RoomsSearchDeleteRequest request) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"rooms-search-delete\" with id {}", request.getRoomsId());
        dateService.deleteRooms(request);
    }

    @KafkaListener(topics="rooms-search-dates-update", containerFactory = "roomsSearchDatesKafkaListenerContainerFactory", groupId = "dates-searchconsumer")
    void datesListener(DatesUpdateDetails datesUpdateDetails) throws IOException, InterruptedException {
        log.info("Listener received: \"dates\" for rooms of hotel {}.", datesUpdateDetails.getHotelId());
        dateService.updateDates(datesUpdateDetails);
    }

    @KafkaListener(topics="rooms-search-price-update", containerFactory = "roomsSearchPriceKafkaListenerContainerFactory", groupId = "price-searchconsumer")
    void priceListener(PriceUpdateDetails priceUpdateDetails) throws IOException, InterruptedException {
        log.info("Listener received: \"price\" for rooms {}.", priceUpdateDetails.getRoomsId());
        priceService.updatePrices(priceUpdateDetails);
    }

}
