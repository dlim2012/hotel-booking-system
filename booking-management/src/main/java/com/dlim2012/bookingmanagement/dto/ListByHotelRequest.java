package com.dlim2012.bookingmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListByHotelRequest {
    List<String> status;
    LocalDate startDate;
    LocalDate endDate;
}
