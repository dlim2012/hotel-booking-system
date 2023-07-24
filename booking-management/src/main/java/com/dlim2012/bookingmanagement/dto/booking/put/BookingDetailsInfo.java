package com.dlim2012.bookingmanagement.dto.booking.put;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetailsInfo {
    String specialRequests;
    Integer estimatedArrivalHour;
}
