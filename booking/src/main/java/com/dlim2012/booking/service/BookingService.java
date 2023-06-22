package com.dlim2012.booking.service;

import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.Room;
import com.dlim2012.clients.mysql_booking.repository.AvailableRoomRepository;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomRepository;
import com.dlim2012.clients.dto.booking.BookingItem;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final AvailableRoomRepository availableRoomRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    private final CacheService cacheService;
    private final PriceService priceService;

    private final ModelMapper modelMapper = new ModelMapper();



    /* Booking Operations */
    public Booking reserveBook(Integer userId, Integer hotelId, Integer roomId, BookingItem bookingItem){

        // Check if room exists
        Room room = roomRepository.findByHotelIdAndRoomId(hotelId, roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        // Update availability (return if not available)
        Integer updates = availableRoomRepository.conditionalDecreaseQuantityByRoomIdAndDateBetween(
                roomId, bookingItem.getStartDateTime().toLocalDate(), bookingItem.getEndDateTime().toLocalDate(),
                bookingItem.getQuantity()
        );
        if (updates != ChronoUnit.DAYS.between(bookingItem.getStartDateTime().toLocalDate(),
                bookingItem.getEndDateTime().toLocalDate()) + 1){
            throw new IllegalArgumentException("Room not available.");
        }

        // Get price
        Long price = priceService.getPriceInCents(bookingItem, room);

        // save information in MySQL and get updated Booking
        Booking booking = modelMapper.map(bookingItem, Booking.class);
        booking.setRoomId(room.getRoomId());
        booking.setUserId(userId);
        booking.setStatus(BookingStatus.RESERVED);
        booking = bookingRepository.save(booking);

        // save to cache with TTL
        cacheService.cacheBookingForTTL(booking);
        return booking;
    }

    public void revertBook(
            Booking booking,
            BookingStatus newBookingStatus
    ){
        // update booking status
        booking.setStatus(newBookingStatus);
        bookingRepository.save(booking);

        // delete from cache
        cacheService.cacheBookingEvict(booking);

        // increase room availability
        Integer updates = availableRoomRepository.increaseQuantityByRoomIdAndDateBetween(
                booking.getRoomId(),
                booking.getStartDateTime().toLocalDate(),
                booking.getEndDateTime().toLocalDate(),
                booking.getQuantity()
        );
    }

    public LocalDateTime confirmBooking(Booking booking){
        booking.setStatus(BookingStatus.BOOKED);
        booking.setInvoiceConfirmTime(LocalDateTime.now());
        bookingRepository.save(booking);
        return booking.getInvoiceConfirmTime();
    }

    public void saveInvoiceId(Booking booking, String id){
        booking.setInvoiceId(id);
        bookingRepository.save(booking);
    }



    /* Booking Getters and Setters */
    public Booking getBookingByIdAndStatus(
            Long bookingId,
            BookingStatus bookingStatus
    ){

        Booking booking = bookingRepository.findByIdAndStatus(
                        bookingId, bookingStatus)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        return booking;
    }


    public Booking getBookingByInvoiceId(String invoiceId) {
        List<Booking> bookingList = bookingRepository.findByInvoiceId(invoiceId);
        if (bookingList.size() == 0) {
            throw new RuntimeException("Booking information of a successful payment is not found.");
        } else if (bookingList.size() > 1){
            throw new RuntimeException("An invoice has two booking items which is not allowed.");
        }
        return bookingList.get(0);
    }

    public void saveBookingStatus(Booking booking, BookingStatus bookingStatus){
        booking.setStatus(bookingStatus);
        bookingRepository.save(booking);
    }
}
