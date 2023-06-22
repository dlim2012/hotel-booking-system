package com.dlim2012.booking.service;

import com.dlim2012.clients.mysql_booking.entity.AvailableRoom;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.Room;
import com.dlim2012.clients.mysql_booking.repository.AvailableRoomRepository;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomRepository;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.etc.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailableRoomService {
    private final AvailableRoomRepository availableRoomRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    private final Integer MAX_BOOKING_DAYS = 30;
    private final ModelMapper modelMapper = new ModelMapper();


    public List<Pair<String, Long>> updateRoom(RoomItem roomItem){
        /*
        Updates a room given roomItem: add/modify/delete available room
        Return the invoice Ids with price in cents to cancel invoice
        * */
        List<Pair<String, Long>> invoiceToCancel = new ArrayList<>();

        boolean active = roomItem.getIsActive() != null && roomItem.getIsActive() && roomItem.getQuantity() > 0;

        Optional<Room> optionalRoom = roomRepository.findById(roomItem.getId());

        List<AvailableRoom> availableRoomList;
        Room room;
        List<Pair<LocalDate, Integer>> toCancel = new ArrayList<>();
        if (optionalRoom.isEmpty()){
            if (!active){
                return invoiceToCancel;
            }
            // Register new rooms
            LocalDate date = LocalDate.now();
            availableRoomList = new ArrayList<>();
            for (int i = 0; i< MAX_BOOKING_DAYS; i++){

                AvailableRoom availableRoom = AvailableRoom.builder()
                        .roomId(roomItem.getId())
                        .date(date)
                        .initialQuantity(roomItem.getQuantity())
                        .availableQuantity(roomItem.getQuantity())
                        .build();
                date = date.plus(1, ChronoUnit.DAYS);
                availableRoomList.add(availableRoom);
            }
            room = modelMapper.map(roomItem, Room.class);
            roomRepository.save(room);
        } else {
            // update information
            room = optionalRoom.get();
            Integer quantityDiff = roomItem.getQuantity() - room.getQuantity();
            if (quantityDiff == 0){
                return invoiceToCancel;
            }

            availableRoomList = availableRoomRepository.findByRoomId(roomItem.getId());
            for (AvailableRoom availableRoom: availableRoomList){
                if (availableRoom.getAvailableQuantity() + quantityDiff < 0){
                    toCancel.add(new Pair(
                            availableRoom.getDate(),
                            quantityDiff - availableRoom.getAvailableQuantity()
                    ));
                    availableRoom.setAvailableQuantity(0);
                } else {
                    availableRoom.setAvailableQuantity(availableRoom.getInitialQuantity() + quantityDiff);
                }
                availableRoom.setInitialQuantity(roomItem.getQuantity());
                availableRoomList.add(availableRoom);
            }

            if (!active){
                roomRepository.deleteById(room.getRoomId());
            } else {
                room = modelMapper.map(roomItem, Room.class);
                roomRepository.save(room);
            }
        }
        availableRoomRepository.saveAll(availableRoomList);

        // Get invoice ids of rooms for automatic cancellation (select the latest bookings)
        List<Booking> bookingToCancel = new ArrayList<>();
        for (Pair<LocalDate, Integer> p: toCancel){
            Integer quantity = p.getR();

            // Cancel reserved
            List<Booking> bookingList = bookingRepository.findByRoomIdAndDateAndStatusOrderByInvoiceConfirmationTime(
                    roomItem.getId(),  p.getL(), BookingStatus.RESERVED
            );


            bookingList = bookingRepository.findByRoomIdAndDateAndStatusOrderByInvoiceConfirmationTime(
                    roomItem.getId(),  p.getL(), BookingStatus.BOOKED
            );
            for (Booking booking: bookingList){
                if (quantity <= 0){
                    break;
                }
                booking.setStatus(BookingStatus.CANCELLED_BY_HOTEL_MANAGER);
                bookingToCancel.add(booking);
                invoiceToCancel.add(new Pair<>(booking.getInvoiceId(), booking.getPriceInCents()));
                quantity -= booking.getQuantity();
            }
        }

        // cancel or reduce price of the invoices
        return invoiceToCancel;
//                cancelPayment(roomItem.id(), toCancel);

    }
}
