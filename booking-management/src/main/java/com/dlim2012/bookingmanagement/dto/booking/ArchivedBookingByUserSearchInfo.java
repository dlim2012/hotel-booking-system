package com.dlim2012.bookingmanagement.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchivedBookingByUserSearchInfo {
    String bookingMainStatus;
    LocalDate endDate;
}
