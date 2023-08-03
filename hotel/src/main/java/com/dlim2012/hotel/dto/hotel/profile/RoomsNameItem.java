package com.dlim2012.hotel.dto.hotel.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsNameItem {
    Integer id;
    String displayName;
    Boolean isActive;
    Integer quantity;
    LocalDate availableFrom;
    LocalDate availableUntil;
}
