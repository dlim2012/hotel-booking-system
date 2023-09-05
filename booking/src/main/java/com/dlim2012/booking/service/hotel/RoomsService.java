package com.dlim2012.booking.service.hotel;

import com.dlim2012.booking.service.common.DatesService;
import com.dlim2012.booking.service.common.PriceService;
import com.dlim2012.booking.service.common.RoomService;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDeleteRequest;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingDetails;
import com.dlim2012.clients.kafka.dto.booking.rooms.RoomsBookingInActivateRequest;
import com.dlim2012.clients.mysql_booking.entity.Hotel;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import com.dlim2012.clients.mysql_booking.repository.DatesRepository;
import com.dlim2012.clients.mysql_booking.repository.PriceRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomsRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RoomsService {

    private final PriceService priceService;
    private final RoomService roomService;
    private final DatesService datesService;

    private final RoomsRepository roomsRepository;
    private final RoomRepository roomRepository;
    private final DatesRepository datesRepository;
    private final PriceRepository priceRepository;

    private final Integer MAX_BOOKING_DAYS = 90;
    private final EntityManager entityManager;

    /*
    Kafka messages
     */

    public void setRoomsTableFromDetails(RoomsBookingDetails details, Rooms rooms, LocalDate endDate) {


        rooms.setId(details.getRoomsId());
        if (rooms.getHotel() == null) {
            rooms.setHotel(entityManager.getReference(Hotel.class, details.getHotelId()));
        }
        rooms.setIsActive(details.getActivate());
        rooms.setQuantity(details.getQuantity());
        rooms.setDisplayName(details.getDisplayName());
        rooms.setShortName(details.getShortName());
        rooms.setPriceMin(details.getPriceMin());
        rooms.setPriceMax(details.getPriceMax());
        rooms.setCheckInTime(details.getCheckInTime());
        rooms.setCheckOutTime(details.getCheckOutTime());
        rooms.setAvailableFrom(details.getAvailableFrom());
        rooms.setAvailableUntil(details.getAvailableUntil());
        rooms.setFreeCancellationDays(details.getFreeCancellationDays());
        rooms.setNoPrepaymentDays(details.getNoPrepaymentDays());

        if (rooms.getDatesReserved() == null) {
            rooms.setDatesReserved(0);
        }
        if (rooms.getDatesBooked() == null) {
            rooms.setDatesBooked(0);
        }

        rooms.setDatesAddedUntil(endDate);

    }

    public void updateRooms(RoomsBookingDetails details) {


        // get start and end dates
        LocalDate minBookingDate = LocalDate.now();
        LocalDate startDate = details.getAvailableFrom().isAfter(minBookingDate) ? details.getAvailableFrom() : minBookingDate;
        LocalDate maxBookingDate = minBookingDate.plusDays(MAX_BOOKING_DAYS);
        LocalDate endDate =
                details.getAvailableUntil() == null || details.getAvailableUntil().isAfter(maxBookingDate) ?
                        maxBookingDate : details.getAvailableUntil();

        Rooms rooms = roomsRepository.findByIdWithLock(details.getRoomsId())
                .orElse(new Rooms());

        // Adjust 'room' and 'dates' tables rows
        roomService.adjust(
                rooms, rooms.getQuantity(), rooms.getIsActive(),
                startDate, endDate);

        // Adjust 'rooms' table rows
        setRoomsTableFromDetails(details, rooms, endDate);

        // Save 'rooms', 'room', 'dates' table rows
        roomsRepository.save(rooms);

        // Adjust and save 'price' table rows
        priceService.adjust(rooms, startDate, endDate, details.getPriceMin(), details.getPriceMax());

    }

    public void inactivateRooms(RoomsBookingInActivateRequest request) {
        Rooms rooms = roomsRepository.findByIdWithLock(request.getRoomsId())
                .orElseThrow(() -> new ResourceNotFoundException("Rooms not found."));
        // Adjust all 'dates' table rows
        datesService.removeAllDates(rooms);

        // Adjust 'rooms'
        rooms.setIsActive(false);

        // Save 'rooms', 'room', and 'dates' table rows
        roomsRepository.save(rooms);

        // Adjust and save 'price' table rows
        priceService.removeAllPrice(rooms.getId());
    }

    public void deleteRooms(RoomsBookingDeleteRequest request) {
        roomsRepository.findById(request.getRoomsId());
    }
}
