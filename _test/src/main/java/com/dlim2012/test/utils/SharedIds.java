package com.dlim2012.test.utils;

import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SharedIds {
    private List<String> hotelFacilityList = Arrays.asList(
            "24-hour front desk",
            "Air conditioning",
            "BBQ facilities",
            "Bar",
            "Beachfront",
            "Board game",
            "City view",
            "Concierge",
            "Conference Hall",
            "Dry cleaning",
            "Fitness center",
            "Free parking",
            "Game room",
            "Indoor swimming pool",
            "Laundry",
            "Outdoor Swimming pool",
            "Parking",
            "Personal care",
            "Pet friendly",
            "Playground",
            "Public computer",
            "Relaxation station",
            "Restaurant",
            "Sauna",
            "Spa",
            "Valet parking",
            "Waterfront",
            "Daily housekeeping"
    );

    private List<String> roomFacilityList = Arrays.asList(
            "Breakfast",
            "Kitchen",
            "Dining Area",
            "Refrigerator",
            "Dishwasher",
            "Toaster",
            "Free toiletries",
            "Towels",
            "Shower fridge",
            "Hot tub",
            "Hair dryer",
            "Closet",
            "Sofa",
            "Desk",
            "Air purifier",
            "Air conditioner",
            "TV",
            "Pay-per-view channels",
            "Telephone",
            "Minibar",
            "Coffee machine",
            "Wifi",
            "Room service",
            "Wake-up service/Alarm clock"
    );

    private Map<String, Integer> facilities = new HashMap<>();

    public SharedIds() {
        Integer facilityId = 1;
        for (String facilityName: hotelFacilityList){
            facilities.put(facilityName, facilityId);
//            System.out.println(facilityName + " " + facilityId);
            facilityId += 1;
        }
        for (String facilityName: roomFacilityList){
            facilities.put(facilityName, facilityId);
//            System.out.println(facilityName + " " + facilityId);
            facilityId += 1;
        }
    }

    public Integer getFacilityId(String name){
        return facilities.get(name);
    }
}
