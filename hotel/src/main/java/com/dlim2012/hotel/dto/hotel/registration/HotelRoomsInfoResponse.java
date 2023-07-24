package com.dlim2012.hotel.dto.hotel.registration;

import com.dlim2012.hotel.dto.rooms.RoomsInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelRoomsInfoResponse {
    private Boolean isActive;
    private Integer id;
    private String name;
    private String description;
    private String propertyType;
    private String addressLine1;
    private String addressLine2;
    private String zipcode;
    private String neighborhood;
    private String city;
    private String state;
    private String country;
    private Double latitude;
    private Double longitude;
    private Integer propertyRating;
    private Boolean frontDesk24h;
    private List<String> facilityDisplayNameList;
    private List<RoomsInfo> roomsInfoList;
    private Boolean saved;
}
