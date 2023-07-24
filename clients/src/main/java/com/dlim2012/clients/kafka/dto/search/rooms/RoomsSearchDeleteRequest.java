package com.dlim2012.clients.kafka.dto.search.rooms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsSearchDeleteRequest {
    Integer hotelId;
    Integer roomsId;
}
