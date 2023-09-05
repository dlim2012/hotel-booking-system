package com.dlim2012.booking.dto.dates.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelBookingRoomRequest {
    Long bookingId;
    Long bookingRoomsId;
    Long bookingRoomId;
}
