package com.dlim2012.booking.service.booking_entity.hotel_entity;

import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDetails;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingInActivateRequest;
import com.dlim2012.clients.mysql_booking.entity.Hotel;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import com.dlim2012.clients.mysql_booking.repository.DatesRepository;
import com.dlim2012.clients.mysql_booking.repository.HotelRepository;
import com.dlim2012.clients.mysql_booking.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelEntityService {
    private final HotelRepository hotelRepository;
    private final DatesRepository datesRepository;
    private final PriceRepository priceRepository;

    public void addHotel(HotelBookingDetails hotelBookingDetails) {
        Hotel hotel = Hotel.builder()
                .id(hotelBookingDetails.getHotelId())
                .hotelManagerId(hotelBookingDetails.getHotelManagerId())
                .version(0L)
                .build();
        hotelRepository.save(hotel);
    }

    public void deleteHotel(HotelBookingDeleteRequest request) {
        hotelRepository.deleteById(request.getHotelId());

    }

    public Hotel getHotel(Integer hotelId){
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
    }

    public void inactivateHotel(HotelBookingInActivateRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        for (Rooms rooms: hotel.getRoomsSet()){
            rooms.setIsActive(false);
        }
        hotelRepository.save(hotel);
        datesRepository.deleteByHotelId(request.getHotelId());
        priceRepository.deleteByHotelId(request.getHotelId());
    }

    public Long getNewHotelVersion(Integer hotelId) {
        Hotel hotel = hotelRepository.findByIdWithLock(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        hotel.setVersion(hotel.getVersion()+1L);
        hotelRepository.save(hotel);
        return hotel.getVersion();
    }
//    private LocalDate testDay = LocalDate.now();
//    private LocalDate criteria = LocalDate.of(1970, 1, 1);
//
//    public Integer localDateToInteger(LocalDate localDate){
//        return Math.toIntExact(ChronoUnit.DAYS.between(criteria, localDate));
//    }

}
