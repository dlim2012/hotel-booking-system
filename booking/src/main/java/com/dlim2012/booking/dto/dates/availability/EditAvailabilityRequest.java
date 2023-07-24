package com.dlim2012.booking.dto.dates.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditAvailabilityRequest {
    Long roomId;
    Long datesId;
    LocalDate startDate;
    LocalDate endDate;
}
