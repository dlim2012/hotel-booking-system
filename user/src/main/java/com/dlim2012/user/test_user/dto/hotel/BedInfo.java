package com.dlim2012.user.test_user.dto.hotel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BedInfo {
    String size;
    Integer quantity;
}