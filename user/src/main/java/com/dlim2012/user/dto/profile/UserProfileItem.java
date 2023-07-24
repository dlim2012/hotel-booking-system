package com.dlim2012.user.dto.profile;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileItem {
    String firstName;
    String lastName;
    String displayName;
    String email;
    String phoneNumber;
    String year;
    String month;
    String day;
    String gender;
}
