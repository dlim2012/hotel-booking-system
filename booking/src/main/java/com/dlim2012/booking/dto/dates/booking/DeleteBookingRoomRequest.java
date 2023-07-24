package com.dlim2012.booking.dto.dates.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBookingRoomRequest {
    Long bookingId;
    Long bookingRoomId;
}
