package com.dlim2012.test.dto.hotel.dto.hotel.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomNameItem {
    Integer id;
    String displayName;
    Boolean isActive;
}
