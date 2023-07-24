package com.dlim2012.test.dto.hotel.dto.rooms.registration;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BedInfo {
    String size;
    Integer quantity;
}