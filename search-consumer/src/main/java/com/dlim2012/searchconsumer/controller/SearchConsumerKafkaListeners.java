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

//    private final ElasticSearchQuery elasticSearchQuery;
//    private final ModelMapper modelMapper = new ModelMapper();

    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;

    //https://medium.com/trendyol-tech/elasticsearch-optimistic-concurrency-implementation-997377a16a21
    //https://medium.com/@sourav.pati09/how-to-use-java-high-level-rest-client-with-spring-boot-to-talk-to-aws-elasticsearch-2b6106f2e2c


    @KafkaListener(topics="hotel-search", containerFactory = "hotelSearchKafkaListenerContainerFactory", groupId = "hotel-searchconsumer")
    void hotelListener(HotelSearchDetails hotelDetails) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"hotel-search\" with id {}", hotelDetails.getId());
        indexingService.saveHotel(hotelDetails);
//        System.out.println(hotelRepository.findById(hotelDetails.getId().toString()));
    }

    @KafkaListener(topics="hotel-search-delete", containerFactory = "hotelSearchDeleteKafkaListenerContainerFactory", groupId = "hotel-delete-searchconsumer")
    void hotelDeleteListener(HotelSearchDeleteRequest request) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"hotel-search-delete\" with id {}", request.getHotelId());
//        System.out.println(request);
        indexingService.delete(request);
//        System.out.println(hotelRepository.findById(request.getHotelId().toString()));
    }


    @KafkaListener(topics="hotel-new-day", containerFactory = "hotelNewDayKafkaListenerContainerFactory", groupId = "hotel-new-day-searchconsumer")
    void hotelNewDayListener(HotelsNewDayDetails request) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"hotel-new-day\"");
//        System.out.println(request);
        indexingService.bulkUpdate(request);
//        System.out.println(hotelRepository.findById(request.getHotelId().toString()));
    }

    @KafkaListener(topics="rooms-search", containerFactory = "roomsSearchKafkaListenerContainerFactory", groupId = "rooms-searchconsumer")
    void roomsListener(RoomsSearchDetails roomDetails) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"rooms-search\" with id {}", roomDetails.getRoomsId());
//        System.out.println(roomDetails);
        dateService.updateRooms(roomDetails);
//        System.out.println(hotelRepository.findById(roomDetails.getHotelId().toString()));
    }

    @KafkaListener(topics="rooms-search-version", containerFactory = "roomsSearchVersionKafkaListenerContainerFactory", groupId="rooms-version-searchconsumer")
    void roomsVersionListener(RoomsSearchVersion roomsSearchVersion) throws IOException, InterruptedException {
        log.info("Kafka listener received: \"rooms-search-version\" with id {}", roomsSearchVersion.getRoomsId());
//        System.out.println(roomsSearchVersion);
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
//        System.out.println(datesUpdateDetails);
        dateService.updateDates(datesUpdateDetails);
//        System.out.println(hotelRepository.findById(datesUpdateDetails.getHotelId().toString()));
    }

    @KafkaListener(topics="rooms-search-price-update", containerFactory = "roomsSearchPriceKafkaListenerContainerFactory", groupId = "price-searchconsumer")
    void priceListener(PriceUpdateDetails priceUpdateDetails) throws IOException, InterruptedException {
        log.info("Listener received: \"price\" for rooms {}.", priceUpdateDetails.getRoomsId());
//        System.out.println(priceUpdateDetails);
        priceService.updatePrices(priceUpdateDetails);
//        System.out.println(hotelRepository.findById(priceUpdateDetails.getHotelId().toString()));
    }


//    @KafkaListener(topics="price-search", containerFactory = "priceSearchKafkaListenerContainerFactory", groupId = "price-searchconsumer")
//    void priceListener(PriceSearchDetails priceDetails) throws IOException {
//        log.info("Listener received: {} for rooms {}.", "price", priceDetails);
//        hotelRepository.findAll().forEach(System.out::println);
//    }
//
//    @KafkaListener(topics="facility", containerFactory = "facilityItemKafkaListenerContainerFactory", groupId = "room-searchconsumer")
//    void facilityListener(FacilityItem facilityItem) throws IOException {
//        log.info("Listener received: {} with id {} (display_name: {})", "facility", facilityItem.getId(), facilityItem.getDisplayName());
//        Facility facility = Facility.builder()
//                .id(facilityItem.getId().toString())
//                .displayName(facilityItem.getDisplayName())
//                .build();
//        if (facilityItem.getDisplayName() != null){
//            elasticSearchQuery.createOrUpdateDocument(
//                    "facility",
//                    facility.getId(),
//                    facility
//            );
//        } else {
//            elasticSearchQuery.deleteDocumentById("facility", facilityItem.getId().toString());
//        }
//    }
//
//    @KafkaListener(topics="hotel-facilities", containerFactory = "hotelFacilitiesItemListenerContainerFactory", groupId = "room-searchconsumer")
//    void hotelFacilitiesListener(HotelFacilitiesItem hotelFacilitiesItem) throws IOException {
//        log.info("Listener received: {} for hotel id {} (count: {})",
//                "hotel-facilities", hotelFacilitiesItem.getHotelId(),
//                hotelFacilitiesItem.getFacilityIds().size());
//        if (hotelFacilitiesItem.getFacilityIds().size() != hotelFacilitiesItem.getIsActive().size()){
//            throw new IllegalArgumentException("Invalid hotel-facilities: number of facilities and number of is-active are not same.");
//        }
//
//        Hotel hotel = elasticSearchQuery.getDocumentById("hotel", hotelFacilitiesItem.getHotelId().toString(), Hotel.class);
//        System.out.println(hotel);
//        List<Facility> facilityList = new ArrayList<>();
//        for (int i=0; i<hotelFacilitiesItem.getFacilityIds().size(); i++){
//            if (hotelFacilitiesItem.getIsActive().get(i)){
//                facilityList.add(elasticSearchQuery.getDocumentById("facility", hotelFacilitiesItem.getFacilityIds().toString(), Facility.class));
////                facilityList.add(entityManager.getReference(Facility.class, hotelFacilitiesItem.getFacilityIds().get(i).toString()));
//            }
//        }
//        hotel.setFacility(facilityList);
//        elasticSearchQuery.createOrUpdateDocument("hotel", hotel.getId(), hotel);
//    }
//
//    @KafkaListener(topics="room-facilities", containerFactory = "roomFacilitiesItemListenerContainerFactory", groupId = "room-searchconsumer")
//    void roomFacilitiesListener(RoomFacilitiesItem roomFacilitiesItem) throws IOException {
//        log.info("Listener received: {} for hotel id {} (count: {})",
//                "hotel-facilities", roomFacilitiesItem.getRoomId(),
//                roomFacilitiesItem.getFacilityIds().size());
//        if (roomFacilitiesItem.getFacilityIds().size() != roomFacilitiesItem.getIsActive().size()){
//            throw new IllegalArgumentException("Invalid hotel-facilities: number of facilities and number of is-active are not same.");
//        }
//
//        Room room = elasticSearchQuery.getDocumentById("room", roomFacilitiesItem.getRoomId().toString(), Room.class);
//        System.out.println(room);
//        List<Facility> facilityList = new ArrayList<>();
//        for (int i=0; i<roomFacilitiesItem.getFacilityIds().size(); i++){
//            if (roomFacilitiesItem.getIsActive().get(i)){
//                facilityList.add(elasticSearchQuery.getDocumentById("facility", roomFacilitiesItem.getFacilityIds().toString(), Facility.class));
////                facilityList.add(entityManager.getReference(Facility.class, roomFacilitiesItem.getFacilityIds().get(i).toString()));
//            }
//        }
//        room.setFacility(facilityList);
//        elasticSearchQuery.createOrUpdateDocument("room", room.getId(), room);
//    }
//
//    @KafkaListener(topics="booking", containerFactory = "bookingItemKafkaListenerContainerFactory", groupId = "room-searchconsumer")
//    void facilityListener(BookingItem bookingItem) throws IOException {
//        log.info("Listener received: {} with id {} (room_id: {})", "facility", bookingItem.getId(), bookingItem.getRoomId());
//    }

}
