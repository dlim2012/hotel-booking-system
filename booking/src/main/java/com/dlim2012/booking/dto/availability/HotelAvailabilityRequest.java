package com.dlim2012.booking.dto.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityRequest {
    LocalDate startDate;
    LocalDate endDate;
}
