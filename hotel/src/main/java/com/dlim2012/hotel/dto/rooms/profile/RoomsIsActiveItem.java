package com.dlim2012.hotel.dto.rooms.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsIsActiveItem {
    Boolean isActive;
}
