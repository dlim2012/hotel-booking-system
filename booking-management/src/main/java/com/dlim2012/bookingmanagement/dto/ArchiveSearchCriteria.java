package com.dlim2012.bookingmanagement.dto;

import com.dlim2012.clients.entity.BookingMainStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveSearchCriteria {
    private BookingMainStatus bookingMainStatus;
    private Integer months;
}
