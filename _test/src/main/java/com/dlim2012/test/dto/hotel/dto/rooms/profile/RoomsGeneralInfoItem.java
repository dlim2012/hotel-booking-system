package com.dlim2012.test.dto.hotel.dto.rooms.profile;

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
public class RoomsGeneralInfoItem {
    String displayName;
    String description;
    Integer maxAdult;
    Integer maxChild;
    Integer quantity;
    Long priceMin;
    Long priceMax;
    String checkInTime;
    String checkOutTime;
    LocalDate availableFrom;
    LocalDate availableUntil;
    Integer freeCancellationDays;
    Integer noPrepaymentDays;
    List<BedInfoItem> roomsBeds;
    Boolean isActive;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BedInfoItem{
        String bed;
        Integer quantity;
    }
}
