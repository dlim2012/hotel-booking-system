package com.dlim2012.bookingmanagement.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingMainGuestInfo {
    String firstName;
    String lastName;
    String email;
}
