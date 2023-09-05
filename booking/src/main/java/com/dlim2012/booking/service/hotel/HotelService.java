package com.dlim2012.booking.service.hotel;

import com.dlim2012.booking.dto.reserve.BookingRequest;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingDetails;
import com.dlim2012.clients.kafka.dto.booking.hotel.HotelBookingInActivateRequest;
import com.dlim2012.clients.mysql_booking.entity.Dates;
import com.dlim2012.clients.mysql_booking.entity.Hotel;
import com.dlim2012.clients.mysql_booking.entity.Room;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import com.dlim2012.clients.mysql_booking.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {

    private final HotelRepository hotelRepository;

    /*
    Kafka messages
     */
    public void addHotel(HotelBookingDetails hotelBookingDetails) {
        Hotel hotel = Hotel.builder()
                .id(hotelBookingDetails.getHotelId())
                .hotelManagerId(hotelBookingDetails.getHotelManagerId())
                .build();
        hotelRepository.save(hotel);
    }

    public void deleteHotel(HotelBookingDeleteRequest request) {
        hotelRepository.deleteById(request.getHotelId());
    }

    public void inactivateHotel(HotelBookingInActivateRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        for (Rooms rooms : hotel.getRoomsSet()) {
            rooms.setIsActive(false);
        }
        hotelRepository.save(hotel);
    }

    /*
    Internal usage
     */
    public Boolean validateBookingRequest(
            Hotel hotel, Map<Integer, Long> roomsPriceMap,
            BookingRequest request, Map<Integer, List<BookingRequest.BookingRequestRooms>> requestRoomsMap
    ) {
        LocalDate today = LocalDate.now();

        Set<Integer> invalidRooms = new HashSet<>();
        Long priceInCents = 0L;
        for (Rooms rooms : hotel.getRoomsSet()) {
            List<BookingRequest.BookingRequestRooms> requestRoomsList = requestRoomsMap.get(rooms.getId());
            if (requestRoomsList == null) {
                continue;
            }
            // check-in time and check-out time
            if (rooms.getCheckInTime() > request.getCheckInTime()
                    || rooms.getCheckOutTime() < request.getCheckOutTime()
            ) {
                log.error("Invalid booking request -- check-in time/check-out time mismatch.");
                invalidRooms.add(rooms.getId());
                continue;
            }

            // no pre-payment days and free-cancellation days
            for (BookingRequest.BookingRequestRooms requestRooms : requestRoomsList) {
                if (
                        (rooms.getNoPrepaymentDays() != null
                                && requestRooms.getNoPrepaymentUntil()
                                .isAfter(today.minusDays(rooms.getNoPrepaymentDays())))
                                ||
                                (rooms.getFreeCancellationDays() != null
                                        && requestRooms.getFreeCancellationUntil()
                                        .isAfter(today.minusDays(rooms.getFreeCancellationDays())))
                ) {
                    log.error("Invalid booking request -- no-prepayment days/no cancellation days mismatch.");
                    invalidRooms.add(rooms.getId());
                    break;
                }
            }

            // set up prices
            if (invalidRooms.isEmpty()) {
                Long roomsPrice = roomsPriceMap.get(rooms.getId());
                if (roomsPrice == null) {
                    invalidRooms.add(rooms.getId());
                    continue;
                }
                priceInCents += requestRoomsList.size() * roomsPrice;
            }

            // check dates availability
            Integer count = requestRoomsList.size();
            for (Room room : rooms.getRoomSet()) {
                for (Dates dates : room.getDatesSet()) {
                    if (dates.getStartDate().isBefore(request.getStartDate())
                            && dates.getEndDate().isAfter(request.getEndDate())) {
                        count--;
                        break;
                    }
                }
                if (count <= 0) {
                    break;
                }
            }

            if (count > 0) {
                invalidRooms.add(rooms.getId());
            }
        }

        if (!invalidRooms.isEmpty()) {
            return false;
        }

        if (!priceInCents.equals(request.getPriceInCents())) {
            log.error("Invalid booking request -- price mismatch.");
            return false;
        }
        return true;
    }

}
