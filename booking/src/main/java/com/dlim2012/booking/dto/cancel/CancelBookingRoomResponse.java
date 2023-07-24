package com.dlim2012.booking.dto.cancel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelBookingRoomResponse {
    boolean bookingCancelled;
}
