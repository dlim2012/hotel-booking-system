package com.dlim2012.bookingmanagement.service;

import com.dlim2012.bookingmanagement.dto.booking.ActiveBookingItem;
import com.dlim2012.bookingmanagement.dto.booking.BookingArchiveItem;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByHotelId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.BookingRoom;
import com.dlim2012.clients.mysql_booking.entity.BookingRooms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class RecordMapper {

    public BookingArchiveItem bookingToArchiveItem(Booking booking){
        BookingArchiveItem bookingArchiveItem = BookingArchiveItem.builder()
                .id(booking.getId())
                .hotelId(booking.getHotelId())
                .userId(booking.getUserId())
                .hotelName(booking.getHotelName())
                .neighborhood(booking.getNeighborhood())
                .city(booking.getCity())
                .state(booking.getState())
                .country(booking.getCountry())
                .startDateTime(booking.getStartDateTime())
                .endDateTime(booking.getEndDateTime())
                .priceInCents(booking.getPriceInCents())
                .invoiceId(booking.getInvoiceId())
                .invoiceConfirmTime(booking.getInvoiceConfirmTime())
                .mainStatus(booking.getMainStatus().name())
                .status(booking.getStatus().name())
                .rooms(booking.getBookingRooms().stream()
                        .map(bookingRooms -> BookingArchiveItem.Rooms.builder()
                                .roomsId(bookingRooms.getRoomsId())
                                .roomsName(bookingRooms.getRoomsDisplayName())
//                        .quantity(bookingRooms.getBookingRoomList().size())
                                .build())
                        .toList())
                .build();

        bookingArchiveItem.addAggs();
        return bookingArchiveItem;
    }

    public BookingArchiveItem bookingArchiveByUserIdToArchiveItem(BookingArchiveByUserId booking){
        BookingArchiveItem bookingArchiveItem = BookingArchiveItem.builder()
                .id(booking.getBookingId())
                .hotelId(booking.getHotelId())
                .userId(booking.getUserId())
                .hotelName(booking.getHotelName())
                .neighborhood(booking.getNeighborhood())
                .city(booking.getCity())
                .state(booking.getState())
                .country(booking.getCountry())
                .startDateTime(booking.getStartDateTime())
                .endDateTime(booking.getEndDateTime())
                .priceInCents(booking.getPriceInCents())
                .invoiceId(booking.getInvoiceId())
                .invoiceConfirmTime(booking.getInvoiceConfirmTime())
                .mainStatus(booking.getMainStatus().name())
                .status(booking.getStatus().name())
                .rooms(
                        booking.getRooms().stream().map(
                                bookingArchiveRoom -> BookingArchiveItem.Rooms.builder()
                                        .roomsId(bookingArchiveRoom.getRoomsId())
                                        .roomsName(bookingArchiveRoom.getRoomsName())
                                        .build()
                        ).toList()
                )
                .build();
        bookingArchiveItem.addAggs();
        return bookingArchiveItem;
    }

    public BookingArchiveItem bookingArchiveByHotelIdToArchiveItem(BookingArchiveByHotelId booking){
        BookingArchiveItem bookingArchiveItem = BookingArchiveItem.builder()
                .id(booking.getBookingId())
                .hotelId(booking.getHotelId())
                .userId(booking.getUserId())
                .hotelName(booking.getHotelName())
                .neighborhood(booking.getNeighborhood())
                .city(booking.getCity())
                .state(booking.getState())
                .country(booking.getCountry())
                .startDateTime(booking.getStartDateTime())
                .endDateTime(booking.getEndDateTime())
                .priceInCents(booking.getPriceInCents())
                .invoiceId(booking.getInvoiceId())
                .invoiceConfirmTime(booking.getInvoiceConfirmTime())
                .mainStatus(booking.getMainStatus().name())
                .status(booking.getStatus().name())
                .rooms(
                        booking.getRooms().stream().map(
                                bookingArchiveRoom -> BookingArchiveItem.Rooms.builder()
                                        .roomsId(bookingArchiveRoom.getRoomsId())
                                        .roomsName(bookingArchiveRoom.getRoomsName())
                                        .build()
                        ).toList()
                )
                .build();
        bookingArchiveItem.addAggs();
        return bookingArchiveItem;
    }

    public ActiveBookingItem bookingToActiveBookingItemMapper(Booking booking){

        List<ActiveBookingItem.Room> roomList = new ArrayList<>();
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                ActiveBookingItem.Room room = ActiveBookingItem.Room.builder()
                        .bookingRoomsId(bookingRooms.getId())
                        .bookingRoomId(bookingRoom.getId())
                        .roomsId(bookingRooms.getRoomsId())
                        .roomId(bookingRoom.getRoomId())
                        .roomsDisplayName(bookingRooms.getRoomsDisplayName())
                        .prepayUntil(bookingRooms.getPrepayUntil())
                        .freeCancellationUntil(bookingRooms.getFreeCancellationUntil())
                        .priceInCents(bookingRooms.getPricePerRoomInCents())
                        .startDateTime(bookingRoom.getStartDateTime())
                        .endDateTime(bookingRoom.getEndDateTime())
                        .status(bookingRoom.getStatus().name())
                        .guestName(bookingRoom.getGuestName())
                        .guestEmail(bookingRoom.getGuestEmail())
                        .build();
                roomList.add(room);
            }
        }

        roomList.sort(
                new Comparator<ActiveBookingItem.Room>() {
                    @Override
                    public int compare(ActiveBookingItem.Room o1, ActiveBookingItem.Room o2) {

                        if (!o1.getRoomId().equals(o2.getRoomId())){
                            return o1.getRoomId().compareTo(o2.getRoomId());
                        }
                        if (!o1.getStartDateTime().equals(o2.getStartDateTime())){
                            return o1.getStartDateTime().compareTo(o2.getStartDateTime());
                        }
                        if (!o1.getEndDateTime().equals(o2.getEndDateTime())){
                            return o1.getEndDateTime().compareTo(o2.getEndDateTime());
                        }
                        return o1.getRoomsDisplayName().compareTo(o2.getRoomsDisplayName());
                    }
                }
        );

        ActiveBookingItem activeBookingItem =  ActiveBookingItem.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .hotelId(booking.getHotelId())
                .reservationTime(booking.getReservationTime())
                .hotelName(booking.getHotelName())
                .firstName(booking.getFirstName())
                .lastName(booking.getLastName())
                .email(booking.getEmail())
                .specialRequests(booking.getSpecialRequests())
                .estimatedArrivalHour(booking.getEstimatedArrivalHour())
                .startDateTime(booking.getStartDateTime())
                .endDateTime(booking.getEndDateTime())
                .status(booking.getStatus().name())
                .priceInCents(booking.getPriceInCents())
                .invoiceId(booking.getInvoiceId())
                .invoiceConfirmTime(booking.getInvoiceConfirmTime())
                .room(roomList)
                .build();
        activeBookingItem.setAddress(
                booking.getNeighborhood(),
                booking.getCity(),
                booking.getState(),
                booking.getCountry());
        return activeBookingItem;
    }


}
