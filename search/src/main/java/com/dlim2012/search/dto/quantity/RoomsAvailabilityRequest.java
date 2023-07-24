package com.dlim2012.search.dto.quantity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsAvailabilityRequest {
    LocalDate startDate;
    LocalDate endDate;
}
