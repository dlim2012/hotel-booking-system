package com.dlim2012.test.dto.booking.dto.payment;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SuccessRecord {
    String paymentId;
    String payerId;
}
