package com.dlim2012.booking.service.common;

import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.dlim2012.clients.mysql_booking.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MappingService {

    public Map<Integer, Long> mapPriceSum(List<Price> priceList) {
        Map<Integer, Long> priceMap = new HashMap<>();
        for (Price price : priceList) {
            Integer roomsId = price.getRooms().getId();
            priceMap.put(roomsId, priceMap.getOrDefault(roomsId, 0L) + price.getPriceInCents());
        }
        return priceMap;
    }

    public Map<LocalDate, Price> getRoomsPriceMapByLocalDate(List<Price> priceList) {
        Map<LocalDate, Price> priceMap = new HashMap<>();
        for (Price price : priceList) {
            priceMap.put(price.getDate(), price);
        }
        return priceMap;
    }


    public Map<Integer, List<Price>> mapPriceByRoomsId(List<Price> priceList) {
        Map<Integer, List<Price>> priceMap = new HashMap<>();
        for (Price price : priceList) {
            Integer roomsId = price.getRooms().getId();
            List<Price> roomsPriceList = priceMap.get(roomsId);
            if (roomsPriceList == null) {
                roomsPriceList = new ArrayList<>(List.of(price));
                priceMap.put(roomsId, roomsPriceList);
            } else {
                roomsPriceList.add(price);
            }
        }
        return priceMap;
    }

    public PriceUpdateDetails getPriceUpdateDetails(Integer hotelId, Integer roomsId, List<Price> priceList) {
        return PriceUpdateDetails.builder()
                .hotelId(hotelId)
                .roomsId(roomsId)
                .priceDtoList(
                        priceList == null ? null : priceList.stream()
                                .map(price -> PriceUpdateDetails.PriceDto.builder()
                                        .priceId(price.getId())
                                        .date(price.getDate())
                                        .priceInCents(price.getPriceInCents())
                                        .version(price.getVersion())
                                        .build())
                                .toList())
                .build();
    }

    public PriceUpdateDetails getPriceUpdateDetails(Rooms rooms, List<Price> priceList) {
        return getPriceUpdateDetails(rooms.getHotel().getId(), rooms.getId(), priceList);
    }


    public List<PriceUpdateDetails> getPriceUpdateDetails(Hotel hotel, Map<Integer, List<Price>> roomsPriceMap) {
        if (roomsPriceMap == null) {
            return new ArrayList<>();
        }
        return hotel.getRoomsSet().stream()
                .map(rooms -> getPriceUpdateDetails(rooms, roomsPriceMap.get(rooms.getId())))
                .toList();
    }


    /*
    Dates
     */
    public DatesUpdateDetails getDatesUpdateDetails(Integer hotelId, List<Room> roomList) {

        Map<Long, Long> datesVersions = new HashMap<>();
        Map<Long, List<DatesUpdateDetails.DatesDto>> datesMap = new HashMap<>();
        for (Room room : roomList) {
            datesVersions.put(room.getId(), room.getDatesVersion());
            datesMap.put(room.getId(), room.getDatesSet().stream()
                    .map(dates -> DatesUpdateDetails.DatesDto.builder()
                            .build())
                    .toList());
        }
        return DatesUpdateDetails.builder()
                .hotelId(hotelId)
                .datesVersions(datesVersions)
                .datesMap(datesMap)
                .build();
    }

    public DatesUpdateDetails getDatesUpdateDetails(Hotel hotel) {
        if (hotel == null) {
            return null;
        }
        Map<Long, Long> datesVersions = new HashMap<>();
        Map<Long, List<DatesUpdateDetails.DatesDto>> datesMap = new HashMap<>();
        for (Rooms rooms : hotel.getRoomsSet()) {
            for (Room room : rooms.getRoomSet()) {
                datesVersions.put(room.getId(), room.getDatesVersion());
                datesMap.put(room.getId(), room.getDatesSet().stream()
                        .map(dates -> DatesUpdateDetails.DatesDto.builder()
                                .build())
                        .toList());

            }
        }
        return DatesUpdateDetails.builder()
                .hotelId(hotel.getId())
                .datesVersions(datesVersions)
                .datesMap(datesMap)
                .build();
    }

    /*
    Rooms
     */
    public Map<Integer, Rooms> mapRooms(List<Rooms> roomsList){
        Map<Integer, Rooms> roomsMap = new HashMap<>();
        for (Rooms rooms: roomsList){
            roomsMap.put(rooms.getId(), rooms);
        }
        return roomsMap;
    }

    public Map<Integer, List<Booking>> mapBookingByHotelId(List<Booking> bookingList){
        Map<Integer, List<Booking>> bookingMap = new HashMap<>();
        for (Booking booking: bookingList){
            Integer hotelId = booking.getHotelId();
            List<Booking> hotelBookingList = bookingMap.get(hotelId);
            if (hotelBookingList == null){
                hotelBookingList = new ArrayList<>(List.of(booking));
                bookingMap.put(hotelId, hotelBookingList);
            } else {
                hotelBookingList.add(booking);
            }
        }
        return bookingMap;
    }
}

