package com.dlim2012.user.test_user.dto.hotel;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomsRegisterRequest {
    String displayName;
    String shortName;
    String description;
    Integer maxAdult;
    Integer maxChild;
    Integer quantity;
    Long priceMin;
    Long priceMax;
    Integer checkOutTime;
    Integer checkInTime;
    Boolean isActive;
    LocalDate availableFrom;
    LocalDate availableUntil;
    Integer freeCancellationDays;
    Integer noPrepaymentDays;
    List<String> facilityDisplayNameList;
    List<BedInfo> bedInfoDtoList;

}
