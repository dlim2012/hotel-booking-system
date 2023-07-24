package com.dlim2012.test.dto.hotel.dto.rooms;

import com.dlim2012.test.dto.hotel.dto.rooms.registration.BedInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsInfo {

    Integer id;
    String displayName;
    String description;
    Integer maxAdult;
    Integer maxChild;
    Integer quantity;
    Integer checkInTime;
    Integer checkOutTime;
    Integer freeCancellationDays;
    Integer noPrepaymentDays;
    Boolean breakfast;
    List<String> facilityList;
    List<BedInfo> bedInfoList;
}
