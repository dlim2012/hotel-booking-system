package com.dlim2012.test.dto.hotel.dto.rooms.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsFacilityItem {
    List<String> facility;
}
