package com.dlim2012.test.dto.booking_management.dto;

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
public class ListByUserRequest {
    List<String> status;
    LocalDate startDate;
    LocalDate endDate;
}
