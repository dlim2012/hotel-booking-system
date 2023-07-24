package com.dlim2012.clients.kafka.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingNotification {
    Long bookingId;
    Integer hotelId;
    Integer userId;
    Integer hotelManagerId;
}
