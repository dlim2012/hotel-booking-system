package com.dlim2012.user.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordResponse {
    Boolean passwordMatch;
    Boolean success;
}
