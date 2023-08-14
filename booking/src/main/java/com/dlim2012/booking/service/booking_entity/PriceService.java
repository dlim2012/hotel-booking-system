package com.dlim2012.booking.service.booking_entity;

import com.dlim2012.booking.dto.profile.RoomsPriceItem;
import com.dlim2012.booking.service.booking_entity.hotel_entity.HotelEntityService;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.price.PriceDto;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.dlim2012.clients.mysql_booking.entity.Price;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import com.dlim2012.clients.mysql_booking.repository.PriceRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomsRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {

    private HotelEntityService hotelEntityService;

    private final PriceRepository priceRepository;
    private final RoomsRepository roomsRepository;

    private final KafkaTemplate<String, PriceUpdateDetails> roomsSearchPriceUpdateKafkaTemplate;

    private final EntityManager entityManager;


    private final Random rand = new Random();

    public Long getPrice(
            Long priceMin,
            Long priceMax
            ){
        return priceMin + (long) ((priceMax - priceMin) * rand.nextDouble());
    }

    public List<Price> getDefaultPrice(
            Integer roomsId,
            LocalDate startDate,
            LocalDate endDate,
            Long priceMin,
            Long priceMax,
            Integer maxBookingDays){

        List<Price> priceList = new ArrayList<>();
        for (LocalDate date = startDate; date.isBefore(endDate); date=date.plusDays(1)){
            Price price = Price.builder()
                    .rooms(entityManager.getReference(Rooms.class, roomsId))
                    .date(date)
                    .priceInCents(getPrice(priceMin, priceMax))
                    .build();
            priceList.add(price);
        }
        return priceList;
    }

    public List<Price> updatePriceNewDateRange(
            Integer roomsId,
            Long priceMin,
            Long priceMax,
            LocalDate prevStartDate,
            LocalDate prevEndDate,
            LocalDate startDate,
            LocalDate endDate
    ){
        // delete price out of range
        if (prevStartDate.isBefore(startDate) || prevEndDate.isAfter(endDate)){
            priceRepository.deleteByOutOfDateRange(startDate, endDate);
        }

        // add price before previous date range
        List<Price> priceList = new ArrayList<>();
        if (startDate.isBefore(prevStartDate)){
            LocalDate addUntil = prevStartDate.isAfter(endDate) ? endDate : prevStartDate;
            for (LocalDate date = startDate; date.isBefore(addUntil); date = date.plusDays(1)) {
                Price price = Price.builder()
                        .rooms(entityManager.getReference(Rooms.class, roomsId))
                        .date(date)
                        .priceInCents(getPrice(priceMin, priceMax))
                        .build();
                priceList.add(price);
            }
        }

        // add price after previous date range
        if (endDate.isAfter(prevEndDate)){
            LocalDate addSince = prevEndDate.isBefore(startDate) ? startDate : prevEndDate;
            for (LocalDate date = addSince; date.isBefore(endDate); date = date.plusDays(1)) {
                Price price = Price.builder()
                        .rooms(entityManager.getReference(Rooms.class, roomsId))
                        .date(date)
                        .priceInCents(getPrice(priceMin, priceMax))
                        .build();
                priceList.add(price);
            }
        }
        return priceRepository.saveAll(priceList);
    }

    public RoomsPriceItem getRoomsPrice(Integer hotelId, Integer roomsId, Integer hotelManagerId) {
        return RoomsPriceItem.builder()
                .roomsId(roomsId)
                .priceDtoList(priceRepository.findByRoomsId(roomsId).stream()
                        .map(price-> RoomsPriceItem.PriceDto.builder()
                                .date(price.getDate())
                                .priceInCents(price.getPriceInCents())
                                .build())
                        .toList())
                .build();
    }

    public void putRoomsPrice(Integer hotelId, Integer roomsId, Integer hotelManagerId, RoomsPriceItem item) {

        priceRepository.deleteAllByRoomsId(roomsId);
        List<Price> priceList = priceRepository.saveAll(
                item.getPriceDtoList().stream()
                        .map(priceDto -> Price.builder()
                                .rooms(entityManager.getReference(Rooms.class, roomsId))
                                .date(priceDto.getDate())
                                .priceInCents(priceDto.getPriceInCents())
                                .build())
                        .toList()
        );

        PriceUpdateDetails priceUpdateDetails = PriceUpdateDetails.builder()
                .hotelId(hotelId)
                .roomsId(roomsId)
                .version(hotelEntityService.getNewHotelVersion(hotelId))
                .priceDtoList(priceList.stream()
                        .map(price -> PriceDto.builder()
                                .priceId(price.getId())
                                .date(price.getDate())
                                .priceInCents(price.getPriceInCents())
                                .build()).toList())
                .build();
        roomsSearchPriceUpdateKafkaTemplate.send("rooms-search-price-update", priceUpdateDetails);
    }


}
