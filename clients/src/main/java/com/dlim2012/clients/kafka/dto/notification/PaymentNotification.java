package com.dlim2012.clients.kafka.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotification {
    Long bookingId;
    Integer hotelId;
    Integer userId;
    Integer hotelManagerId;
    Long priceInCents;
}
