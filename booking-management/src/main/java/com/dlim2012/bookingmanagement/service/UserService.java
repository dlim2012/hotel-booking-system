package com.dlim2012.bookingmanagement.service;

import com.dlim2012.bookingmanagement.dto.ListByUserRequest;
import com.dlim2012.bookingmanagement.dto.booking.ActiveBookingItem;
import com.dlim2012.bookingmanagement.dto.booking.ArchivedBookingByUserSearchInfo;
import com.dlim2012.bookingmanagement.dto.booking.BookingArchiveItem;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingBookerInfo;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingDetailsInfo;
import com.dlim2012.bookingmanagement.dto.booking.put.BookingRoomGuestInfo;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.BookingRoom;
import com.dlim2012.clients.mysql_booking.entity.BookingRooms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final CassandraService cassandraService;
    private final MySqlService mySqlService;
    private final RecordMapper recordMapper;
    private final CacheManager cacheManager;
    private final Cache bookingCache;

    public UserService(CassandraService cassandraService, MySqlService mySqlService, RecordMapper recordMapper, CacheManager cacheManager) {
        this.cassandraService = cassandraService;
        this.mySqlService = mySqlService;
        this.recordMapper = recordMapper;
        this.cacheManager = cacheManager;
        this.bookingCache = cacheManager.getCache("booking");
    }

    public List<BookingArchiveItem> getBookingsByUserId(Integer userId, ListByUserRequest request){
        // status has only two options: CANCELLED, COMPLETED
        List<BookingArchiveItem> responseItemList = new ArrayList<>();

        for (String status: request.getStatus()) {
            // todo: make queries asynchronous
            BookingMainStatus bookingMainStatus = BookingMainStatus.valueOf(status);

            if (bookingMainStatus != BookingMainStatus.COMPLETED && bookingMainStatus != BookingMainStatus.CANCELLED) {
//                List<Booking> bookingArchiveByUserIdList = mySqlService.asyncBookingByUserIdAndKeys(userId, bookingMainStatus, request.getStartDate(), request.getEndDate())
//                for (Booking booking: bookingArchiveByUserIdList){
//                    if (booking.getStatus().equals(BookingStatus.RESERVED_FOR_TIMEOUT)){
//                        continue;
//                    }
//                    responseItemList.add(recordMapper.bookingToArchiveItem(booking));
//                }
                responseItemList.addAll(mySqlService.asyncBookingByUserIdAndKeys(userId, bookingMainStatus, request.getStartDate(), request.getEndDate()).stream()
                        .filter(booking -> !booking.getStatus().equals(BookingStatus.RESERVED_FOR_TIMEOUT))
                        .filter(booking -> booking.getUserId().equals(userId))
                        .map(recordMapper::bookingToArchiveItem)
                        .toList()
                );
            } else {
                responseItemList.addAll(
                        cassandraService.asyncQueryBookingArchiveByUserId(userId, bookingMainStatus, request.getStartDate(), request.getEndDate())
                                .stream()
                                .filter(bookingArchiveByUserId -> bookingArchiveByUserId.getUserId().equals(userId))
                                .map(recordMapper::bookingArchiveByUserIdToArchiveItem).toList()
                );
            }
        }
        responseItemList.sort(
                new Comparator<BookingArchiveItem>() {
                    @Override
                    public int compare(BookingArchiveItem o1, BookingArchiveItem o2) {
                        if (o1.getEndDateTime().isBefore(o2.getEndDateTime())){
                            return -1;
                        }
                        if (o1.getStartDateTime().isBefore(o2.getStartDateTime())){
                            return -1;
                        }
                        if (o1.getInvoiceConfirmTime() == null){
                            if (o2.getInvoiceConfirmTime() == null){
                                if (!o1.getHotelName().equals(o2.getHotelName())){
                                    return o1.getHotelName().compareTo(o2.getHotelName());
                                } else {
                                    return o1.getAddress().compareTo(o2.getAddress());
                                }
                            } else {
                                return 1;
                            }
                        } else{
                            if (o2.getInvoiceConfirmTime() == null){
                                return -1;
                            } else {
                                return o1.getInvoiceConfirmTime().compareTo(o2.getInvoiceConfirmTime());
                            }
                        }
                    }
                }
        );
        return responseItemList;
    }

    public ActiveBookingItem getActiveBookingItemByAppUser(Long bookingId, Integer userId) {
        Booking booking = mySqlService.getBookingByAppUser(bookingId, userId);
        System.out.println(booking);
        return recordMapper.bookingToActiveBookingItemMapper(booking);
    }

    public void putBookerInfo(Long bookingId, Integer userId, BookingBookerInfo request) {
        Booking booking = mySqlService.getBookingByAppUser(bookingId, userId);
        booking.setFirstName(request.getFirstName());
        booking.setLastName(request.getLastName());
        booking.setEmail(request.getEmail());
        mySqlService.saveBookingByAppUser(booking);
    }

    public void putDetailsInfo(Long bookingId, Integer userId, BookingDetailsInfo request) {
        Booking booking = mySqlService.getBookingByAppUser(bookingId, userId);
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setEstimatedArrivalHour(request.getEstimatedArrivalHour());
        mySqlService.saveBookingByAppUser(booking);
    }

    public void putGuestInfo(Long bookingId, Long bookingRoomId, Integer userId, BookingRoomGuestInfo request) {
        Booking booking = mySqlService.getBookingByAppUser(bookingId, userId);
        for (BookingRooms bookingRooms: booking.getBookingRooms()){
            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
                if (bookingRoom.getId().equals(bookingRoomId)){
                    bookingRoom.setGuestName(request.getGuestName());
                    bookingRoom.setGuestEmail(request.getGuestEmail());
                    break;
                }
            }
        }
        mySqlService.saveBookingByAppUser(booking);
    }

    public BookingArchiveByUserId getArchivedBookingItemByAppUser(Long bookingId, Integer userId, ArchivedBookingByUserSearchInfo request) {
        return cassandraService.getBookingArchiveByUserId(
                userId, BookingMainStatus.valueOf(request.getBookingMainStatus()), request.getEndDate(), bookingId);
    }
}
