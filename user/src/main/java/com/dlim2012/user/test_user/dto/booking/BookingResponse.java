package com.dlim2012.user.test_user.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    Long bookingId;
    Boolean reserveSuccess;
    String redirectUrl;
}
