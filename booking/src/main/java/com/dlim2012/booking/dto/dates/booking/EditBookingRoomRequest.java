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
public class EditBookingRoomRequest {
    Long bookingId;
    Long bookingRoomsId;
    Long bookingRoomId;
    Long roomId;
    LocalDate startDate;
    LocalDate endDate;
    String checkInTime;
    String checkOutTime;
}
