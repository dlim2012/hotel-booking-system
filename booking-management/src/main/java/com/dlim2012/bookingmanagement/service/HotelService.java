package com.dlim2012.bookingmanagement.service;

import com.dlim2012.bookingmanagement.dto.ListByHotelRequest;
import com.dlim2012.bookingmanagement.dto.booking.*;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingBookerInfo;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingDetailsInfo;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingRoomGuestInfo;
import com.dlim2012.bookingmanagement.dto.hotelInfo.HotelDatesInfoResponse;
import com.dlim2012.bookingmanagement.dto.hotelInfo.HotelMainInfoResponse;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByHotelId;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.mysql_booking.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {

    private final CassandraService cassandraService;
    private final MySqlService mySqlService;
    private final RecordMapper recordMapper;


    public List<BookingArchiveItem> getBookingsByHotelId(Integer hotelId, Integer hotelManagerId, ListByHotelRequest request){
        List<BookingArchiveItem> responseItemList = new ArrayList<>();

        for (String status: request.getStatus()) {
            // todo: make queries asynchronous
            BookingMainStatus bookingMainStatus = BookingMainStatus.valueOf(status);

            if (bookingMainStatus != BookingMainStatus.COMPLETED) {
                responseItemList.addAll(
                        mySqlService.asyncBookingByHotelIdAndKeys(hotelId, bookingMainStatus, request.getStartDate(), request.getEndDate()).stream()
                                .filter(booking -> booking.getHotelManagerId().equals(hotelManagerId))
                                .filter(booking -> !booking.getStatus().equals(BookingStatus.RESERVED_FOR_TIMEOUT))
                                .map(recordMapper::bookingToArchiveItem).toList()
                );
            }

            if (bookingMainStatus == BookingMainStatus.COMPLETED || bookingMainStatus == BookingMainStatus.CANCELLED) {
                responseItemList.addAll(
                        cassandraService.asyncQueryBookingArchiveByHotelId(hotelId, bookingMainStatus, request.getStartDate(), request.getEndDate())
                                .stream()
                                .filter(booking -> booking.getHotelManagerId().equals(hotelManagerId))
                                .map(recordMapper::bookingArchiveByHotelIdToArchiveItem).toList());
            }
        }
        return responseItemList;
    }



//    public BookingGuestInfoItem getBookingGuestInfo(Long bookingId){
//        Booking booking = mySqlService.findBookingById(bookingId);
//
//        List<BookingGuestInfoItem.Room> roomList = new ArrayList<>();
//        for (BookingRooms bookingRooms: booking.getBookingRooms()){
//            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
//                roomList.add(
//                        BookingGuestInfoItem.Room.builder()
//                                .roomsId(bookingRooms.getRoomsId())
//                                .roomsName(bookingRooms.getRoomsDisplayName())
//                                .roomId(bookingRoom.getRoomId())
//                                .guestName(bookingRoom.getGuestName())
//                                .guestEmail(bookingRoom.getGuestEmail())
//                                .build()
//                );
//            }
//        }
//
//        return BookingGuestInfoItem.builder()
//                .firstName(booking.getFirstName())
//                .lastName(booking.getLastName())
//                .email(booking.getEmail())
//                .specialRequests(booking.getSpecialRequests())
//                .estimatedArrivalHour(booking.getEstimatedArrivalHour())
//                .roomList(roomList)
//                .build();
//
//    }

    public HotelMainInfoResponse getHotelMain(Integer hotelId, Integer userId) {

        LocalDate today = LocalDate.now();

        Hotel hotel = mySqlService.asyncFindHotel(hotelId, userId);

        List<BookingArchiveByHotelId> bookingArchiveByHotelIdList = cassandraService.asyncQueryBookingArchiveByHotelId(
                hotelId, BookingMainStatus.COMPLETED, today.minusMonths(1), today.plusDays(1)
        );
        List<Booking> bookingList = mySqlService.asyncBookingByHotelIdAndReserved(hotelId);


        Map<Integer, Rooms> roomsMap = new HashMap<>();
        for (Rooms rooms: hotel.getRoomsSet()){
            roomsMap.put(rooms.getId(), rooms);
        }

        HotelMainInfoResponse response = new HotelMainInfoResponse();

        // populate response.numRooms, response.numRoom, response.availableDates
        response.addRooms(hotel.getRoomsSet());


        for (Booking booking: bookingList){
            for (BookingRooms bookingRooms: booking.getBookingRooms()){
                Rooms rooms = roomsMap.getOrDefault(bookingRooms.getRoomsId(), null);
                for (BookingRoom bookingRoom: bookingRooms.getBookingRoomSet()){
                    LocalDate startDate = bookingRoom.getStartDateTime().toLocalDate().isBefore(today)
                            ? today : bookingRoom.getStartDateTime().toLocalDate();
                    int dates = (int) ChronoUnit.DAYS.between(startDate,
                            bookingRoom.getEndDateTime().toLocalDate());
                    boolean outOfRange = rooms == null ||
                            bookingRoom.getStartDateTime().toLocalDate().isBefore(rooms.getAvailableFrom()) ||
                            bookingRoom.getEndDateTime().toLocalDate().isAfter(rooms.getDatesAddedUntil());
                    response.addDateRange(dates, booking.getMainStatus(), outOfRange);
                }
            }
        }

        long totalPrice = 0L;
        for (BookingArchiveByHotelId bookingArchiveByHotelId: bookingArchiveByHotelIdList){
            response.addTotalPrice(bookingArchiveByHotelId.getPriceInCents());
        }
        response.setRecordStartDate(today.minusMonths(1));
        response.setRecordNumBooking(bookingArchiveByHotelIdList.size());
        response.setRecordTotalPrice(totalPrice);

        return response;
    }

    public HotelDatesInfoResponse getHotelDatesInfo(Integer hotelId, Integer userId) {

        Hotel hotel = mySqlService.asyncFindHotel(hotelId, userId);
        List<Booking> bookingList = mySqlService.asyncBookingByHotelIdAndReserved(hotelId);
        List<Dates> hotelDatesList = mySqlService.asyncDatesByHotel(hotelId);

        Map<Long, List<Dates>> datesMap = new HashMap<>();
        for (Dates dates: hotelDatesList){
            Long roomId = dates.getRoom().getId();
            List<Dates> roomDatesList = datesMap.getOrDefault(roomId, new ArrayList<>());
            roomDatesList.add(dates);
            datesMap.put(roomId, roomDatesList);
        }


        Map<Long, HotelDatesInfoResponse.Room> roomMap = new HashMap<>();

        // Add availability
        for (Rooms rooms: hotel.getRoomsSet()){
            Integer roomsId = rooms.getId();
            // todo: add option to see also check-in/check-out time
            Integer checkInHour = rooms.getCheckInTime() / 60;
            Integer checkInMinute = rooms.getCheckInTime() % 60;
            Integer checkOutHour = rooms.getCheckOutTime() / 60;
            Integer checkOutMinute = rooms.getCheckOutTime() % 60;

            for (Room room: rooms.getRoomSet()){
                Long roomId = room.getId();
                roomMap.put(roomId, HotelDatesInfoResponse.Room.builder()
                    .roomsId(roomsId)
                    .title(rooms.getShortName() + " " + room.getRoomNumber().toString())
                    .isActive(true)
                    .dates(new ArrayList<>(datesMap.getOrDefault(roomId, new ArrayList<>()).stream()
                        .map(dates -> HotelDatesInfoResponse.Dates.builder()
                            .status("AVAILABLE")
                            .bookingId(null)
                            .datesId(dates.getId())
                            .bookingRoomsId(null)
                            .bookingRoomId(null)
                            .startDateTime(dates.getStartDate().atTime(checkInHour, checkInMinute))
                            .endDateTime(dates.getEndDate().atTime(checkOutHour, checkOutMinute))
                                .build()
                            ).toList()
                        )
                    )
                    .availableFrom(rooms.getAvailableFrom())
                    .availableUntil(rooms.getAvailableUntil())
                    .build());
            }
        }


        for (Booking booking: bookingList){
            for (BookingRooms bookingRooms: booking.getBookingRooms()){
                for (BookingRoom bookingRoom: bookingRooms.getBookingRoomSet()){
                    Long roomId = bookingRoom.getRoomId();
                    if (!bookingRoom.getStatus().equals(BookingStatus.RESERVED)
                            && !bookingRoom.getStatus().equals(BookingStatus.RESERVED_FOR_TIMEOUT)
                         && !bookingRoom.getStatus().equals(BookingStatus.BOOKED)
                    ){
                        continue;
                    }

                    HotelDatesInfoResponse.Room room = roomMap.getOrDefault(roomId, HotelDatesInfoResponse.Room.builder()
                                    .roomsId(bookingRooms.getRoomsId())
                                    .title(bookingRooms.getRoomsShortName())
                                    .isActive(false)
                                    .dates(new ArrayList<>())
                                    .availableFrom(null)
                                    .availableUntil(null)
                                    .build()
                    );
                    room.getDates().add(HotelDatesInfoResponse.Dates.builder()
                            .bookingId(booking.getId())
                            .datesId(null)
                            .bookingRoomsId(bookingRooms.getId())
                            .bookingRoomId(bookingRoom.getId())
                            .status(booking.getStatus().equals(BookingStatus.RESERVED_FOR_TIMEOUT) ? "RESERVED_FOR_TIMEOUT" : booking.getMainStatus().name())
                            .startDateTime(bookingRoom.getStartDateTime())
                            .endDateTime(bookingRoom.getEndDateTime())
                            .build());
                    roomMap.put(roomId, room);
                }
            }
        }

        return HotelDatesInfoResponse.builder()
                .roomMap(roomMap)
                .build();
    }

    public BookingBasicInfo getBookingByHotel(Long bookingId, Integer userId) {
        throw new RuntimeException("Not implemented yet.");
    }

    public ActiveBookingItem getActiveBookingItemByHotel(Long bookingId, Integer userId) {
        Booking booking = mySqlService.getBookingByHotel(bookingId, userId);
        return recordMapper.bookingToActiveBookingItemMapper(booking);
    }

    public BookingMainGuestInfo getActiveMainGuestInfo(Long bookingId, Integer userId) {
        Booking booking = mySqlService.getBookingByHotel(bookingId, userId);
        return BookingMainGuestInfo.builder()
                .firstName(booking.getFirstName())
                .lastName(booking.getLastName())
                .email(booking.getEmail())
                .build();
    }

    public BookingArchiveByHotelId getArchivedBookingItemByHotel(Integer hotelId, Integer userId, Long bookingId, ArchivedBookingByUserSearchInfo request) {
        return cassandraService.getBookingArchiveByHotelId(
                hotelId, BookingMainStatus.valueOf(request.getBookingMainStatus()), request.getEndDate(), bookingId, userId);
    }

    public void putBookerInfo(Long bookingId, Integer userId, BookingBookerInfo request) {
        Booking booking = mySqlService.getBookingByHotel(bookingId, userId);
        booking.setFirstName(request.getFirstName());
        booking.setLastName(request.getLastName());
        booking.setEmail(request.getEmail());
        mySqlService.saveBookingByHotelManager(booking);

    }

    public void putDetailsInfo(Long bookingId, Integer userId, BookingDetailsInfo request) {
        Booking booking = mySqlService.getBookingByHotel(bookingId, userId);
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setEstimatedArrivalHour(request.getEstimatedArrivalHour());
        mySqlService.saveBookingByHotelManager(booking);
    }

    public void putGuestInfo(Long bookingId, Long bookingRoomId, Integer userId, BookingRoomGuestInfo request) {
        Booking booking = mySqlService.getBookingByHotel(bookingId, userId);
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomSet()){
                if (bookingRoom.getId().equals(bookingRoomId)){
                    bookingRoom.setGuestName(request.getGuestName());
                    bookingRoom.setGuestEmail(request.getGuestEmail());
                    break;
                }
            }
        }
        mySqlService.saveBookingByHotelManager(booking);
    }
}
