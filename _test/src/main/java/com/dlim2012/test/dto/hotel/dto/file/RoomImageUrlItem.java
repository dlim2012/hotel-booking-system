package com.dlim2012.test.dto.hotel.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomImageUrlItem{
    private Integer id;
    private String url;
}
