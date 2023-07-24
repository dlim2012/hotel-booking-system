package com.dlim2012.user.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserContactInfo {
    Integer id;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
}
