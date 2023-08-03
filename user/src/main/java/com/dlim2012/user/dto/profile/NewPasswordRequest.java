package com.dlim2012.user.dto.profile;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordRequest {
    String prevPassword;
    String newPassword;
}
