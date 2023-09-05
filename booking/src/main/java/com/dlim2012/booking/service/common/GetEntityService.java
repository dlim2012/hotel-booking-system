package com.dlim2012.booking.service.common;

import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.mysql_booking.entity.Hotel;
import com.dlim2012.clients.mysql_booking.entity.Price;
import com.dlim2012.clients.mysql_booking.entity.Room;
import com.dlim2012.clients.mysql_booking.repository.HotelRepository;
import com.dlim2012.clients.mysql_booking.repository.PriceRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetEntityService {
    /*
    A service made to deal with getting entity with more than a few lines
     */
    private final MappingService mappingService;

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final PriceRepository priceRepository;

    public Hotel getHotelByManagerWithLock(UserRole userRole, Integer userId, Integer hotelId) {
        if (userRole.equals(UserRole.ADMIN)) {
            return hotelRepository.findByIdWithLock(hotelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        } else if (userRole.equals(UserRole.HOTEL_MANAGER)) {
            return hotelRepository.findByIdAndHotelManagerIdWithLock(hotelId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        } else if (userRole.equals(UserRole.APP_USER)) {
            throw new RuntimeException("App user not allowed to manage hotel.");
        } else {
            throw new RuntimeException("Invalid user role " + userRole.toString() + " for find hotel.");
        }
    }

    public Room getRoomByManagerWithLock(UserRole userRole, Integer userId, Integer hotelId, Long roomId) {
        if (userRole.equals(UserRole.ADMIN)) {
            return roomRepository.findByIdWithLock(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found."));
        } else if (userRole.equals(UserRole.HOTEL_MANAGER)) {
            return roomRepository.findByIdAndHotelIdAndHotelManagerIdWithLock(roomId, hotelId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found."));
        } else if (userRole.equals(UserRole.APP_USER)) {
            throw new RuntimeException("App user not allowed to manage hotel.");
        } else {
            throw new RuntimeException("Invalid user role " + userRole.toString() + " for find room.");
        }
    }


    /* get Price */

    public List<Price> getPriceByManagerWithLock(UserRole userRole, Integer userId, Integer roomsId) {
        if (userRole.equals(UserRole.ADMIN)) {
            return priceRepository.findByRoomsIdWithLock(roomsId);
        } else if (userRole.equals(UserRole.HOTEL_MANAGER)) {
            return priceRepository.getByRoomsIdAndHotelManagerIdWithLock(roomsId, userId);
        } else {
            throw new RuntimeException("Invalid user. (App users are not allowed to modify rooms prices.)");
        }

    }

    public Map<Integer, Long> getRoomsPriceSumMap(Integer hotelId, LocalDate startDate, LocalDate endDate) {
        return mappingService.mapPriceSum(
                priceRepository.findByHotelIdAndDates(hotelId, startDate, endDate)
        );
    }

    public Map<Integer, Long> getRoomsPriceSumMapByDateRangeByManager(Integer hotelId, Integer hotelManagerId, LocalDate startDate, LocalDate endDate) {
        return mappingService.mapPriceSum(
                priceRepository.findByHotelIdAndHotelManagerAndDates(hotelId, hotelManagerId, startDate, endDate)
        );
    }

}
