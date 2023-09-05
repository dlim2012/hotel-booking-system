package com.dlim2012.booking.dto.dates.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingRoomRequest {
    Long bookingId;
    //    Long bookingRoomsId;
    Integer roomsId;
    Long roomId;
    LocalDate startDate;
    LocalDate endDate;
    String checkInTime;
    String checkOutTime;
    Boolean payed;
    String guestName;
    String guestEmail;
}
