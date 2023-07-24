package com.dlim2012.clients.kafka.dto.booking.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelBookingDeleteRequest {
    Integer hotelId;
}
