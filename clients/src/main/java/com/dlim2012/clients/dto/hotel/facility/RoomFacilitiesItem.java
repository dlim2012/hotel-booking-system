package com.dlim2012.clients.dto.hotel.facility;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomFacilitiesItem {
    Integer roomId;
    List<Integer> facilityIds;
    List<Boolean> isActive;
}
