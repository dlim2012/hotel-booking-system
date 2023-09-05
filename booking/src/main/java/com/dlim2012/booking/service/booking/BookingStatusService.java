package com.dlim2012.booking.service.booking;

import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingStatusService {

    public BookingStatus getCancelStatus(UserRole userRole) {
        if (userRole.equals(UserRole.HOTEL_MANAGER)) {
            return BookingStatus.CANCELLED_BY_HOTEL_MANAGER;
        } else if (userRole.equals(UserRole.APP_USER)) {
            return BookingStatus.CANCELLED_BY_APP_USER;
        } else if (userRole.equals(UserRole.ADMIN)) {
            return BookingStatus.CANCELLED_BY_ADMIN;
        } else {
            throw new RuntimeException("Invalid UserRole.");
        }
    }

    public Boolean isReservedStatus(BookingStatus bookingStatus){
        return bookingStatus.equals(BookingStatus.RESERVED) || bookingStatus.equals(BookingStatus.RESERVED_FOR_TIMEOUT);
    }

    public Boolean isActiveStatus(BookingStatus bookingStatus) {
        return isReservedStatus(bookingStatus) || bookingStatus.equals(BookingStatus.BOOKED);
    }

}
