package com.dlim2012.searchconsumer.service;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.clients.elasticsearch.document.Facility;
import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.clients.elasticsearch.document.Price;
import com.dlim2012.clients.elasticsearch.document.Rooms;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelSearchDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelsNewDayDetails;
import com.dlim2012.clients.kafka.dto.search.price.PriceDto;
import com.dlim2012.searchconsumer.repository.HotelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndexingService {

    private final DateService dateService;

    private final HotelRepository hotelRepository;

    private final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("10.0.0.110", 9103, "http")
            )
    );

    private final ElasticSearchUtils elasticSearchUtils;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper = new ModelMapper();
    private final Random random = new Random();

    private final Integer NUM_RETRY_UPDATE = 2;

    public Hotel saveHotel(HotelSearchDetails hotelDetails) throws IOException, InterruptedException {

        if (hotelDetails.getCreateNew()) {
            Hotel hotel = modelMapper.map(hotelDetails, Hotel.class);
            hotel.setId(hotelDetails.getId().toString());

            hotel.setFacility(hotelDetails.getFacility().stream()
                    .map(dto -> Facility.builder().id(dto.getId()).build()).toList()
            );

            hotel.setRooms(new ArrayList<>());
            hotel.setGeoPoint(new GeoPoint(hotelDetails.getLatitude(), hotelDetails.getLongitude()));
            hotel = hotelRepository.save(hotel);

            log.info("Hotel {} saved.", hotel.getId());

            SearchRequest searchRequest = new SearchRequest("hotel");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse1 = client.search(searchRequest, RequestOptions.DEFAULT);
//            System.out.println(searchResponse1);
//            System.out.println(searchResponse1.getHits().getTotalHits());
//            System.out.println(searchResponse1.getHits().getHits());
            return hotel;
        } else {
            for (int i=0; i<NUM_RETRY_UPDATE; i++){
                try {
                    Hotel hotel = hotelRepository.findById(hotelDetails.getId().toString())
                            .orElseThrow(() -> new ResourceNotFoundException("Hotel {} not found while updateing."));
                    hotel.setId(hotelDetails.getId().toString());
                    hotel.setName(hotelDetails.getName());
                    hotel.setPropertyTypeOrdinal(hotelDetails.getPropertyTypeOrdinal().toString());
                    hotel.setNeighborhood(hotelDetails.getNeighborhood());
                    hotel.setZipcode(hotelDetails.getZipcode());
                    hotel.setCity(hotelDetails.getCity());
                    hotel.setState(hotelDetails.getState());
                    hotel.setCountry(hotelDetails.getCountry());
                    hotel.setGeoPoint(new GeoPoint(hotelDetails.getLatitude(), hotelDetails.getLongitude()));
                    hotel.setPropertyRating(hotelDetails.getPropertyRating());

                    hotel.setFacility(hotelDetails.getFacility().stream()
                            .map(dto -> Facility.builder().id(dto.getId()).build()).toList()
                    );
                    hotel = hotelRepository.save(hotel);
                    return hotel;

                } catch (ResourceNotFoundException | OptimisticLockingFailureException e){
                    log.error(e.getMessage());
                } catch (Exception e){
                    log.error(e.getMessage());
                    return null;
                }
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 1000));
            }
            log.error("Update of hotel {} failed after {} retries.", hotelDetails.getId(), NUM_RETRY_UPDATE);
            // todo: update hotel
            return null;
        }
    }

    public void delete(HotelSearchDeleteRequest request) {
        hotelRepository.deleteById(request.getHotelId().toString());
    }

    public void bulkUpdate(HotelsNewDayDetails request) throws InterruptedException {
        Set<Hotel> hotelList = hotelRepository.findByIdBetween(request.getStartId(), request.getEndId()-1);

        Integer today = elasticSearchUtils.toInteger(LocalDate.now());
        for (int i=0; i<NUM_RETRY_UPDATE; i++){
            try{
                for (Hotel hotel: hotelList){
                    if (hotel.getSeqNoPrimaryTerm() == null){
                        log.error("Hotel {} found without SeqNoPrimaryTerm", hotel.getId());
                    }

                    DatesUpdateDetails details = request
                            .getDatesUpdateDetailsMap()
                            .getOrDefault(Integer.valueOf(hotel.getId()), null);
                    if (details != null){
                        dateService.updateRoomsDates(hotel, details);
                    }


                    Map<Integer, List<PriceDto>> hotelPriceUpdateDetailsMap = request
                            .getPriceUpdateDetailsMap()
                            .getOrDefault(Integer.valueOf(hotel.getId()), null);

                    for (Rooms rooms: hotel.getRooms()) {
                        List<Price> newPrice = new ArrayList<>();
                        Set<Integer> dates = new HashSet<>();
                        if (hotelPriceUpdateDetailsMap != null){
                            List<PriceDto> priceDtoList = hotelPriceUpdateDetailsMap.getOrDefault(rooms.getRoomsId(), null);
                            if (priceDtoList != null) {
                                newPrice.addAll(priceDtoList.stream()
                                        .map(priceDto -> Price.builder()
                                                .id(priceDto.getPriceId().toString())
                                                .date(elasticSearchUtils.toInteger(priceDto.getDate()))
                                                .priceInCents(priceDto.getPriceInCents())
                                                .build())
                                        .toList()
                                );
                                dates.addAll(priceDtoList.stream().map(
                                        price->elasticSearchUtils.toInteger(price.getDate())).toList());
                            }
                        }
                        for (Price price: rooms.getPrice()){
                            if (price.getDate() >= today && !dates.contains(price.getDate())){
                                newPrice.add(price);
                            }
                        }
                        rooms.setPrice(newPrice);
                    }

                    System.out.println(hotel.getSeqNoPrimaryTerm());

                }
                hotelRepository.saveAll(hotelList);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e){
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 1000));
            } catch (Exception e){
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Updating dates for hotel {} ~ {} failed after {} retries.", request.getStartId(), request.getEndId(), NUM_RETRY_UPDATE);
    }
}
