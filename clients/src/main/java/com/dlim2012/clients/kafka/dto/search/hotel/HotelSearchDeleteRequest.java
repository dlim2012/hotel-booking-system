package com.dlim2012.clients.kafka.dto.search.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchDeleteRequest {
    Integer hotelId;
}
