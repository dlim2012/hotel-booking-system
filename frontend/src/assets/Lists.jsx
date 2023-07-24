
export const propertyTypesMap = {
    // Others: 0,
    Hotel: 1,
    // "Apartment",
    Lodge: 2,
    // "Homestay",
    Villa: 3,
    Resort: 4,
    // "Vacation home",
    Cabin: 5,
    Apartment: 6
};

export const hotelFacilities = [
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
]

export const roomFacilities = [
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
    "Wake-up service/Alarm clock",
]

export const propertyRatings = [
    { value: 0, label: "No rating"},
    { value: 1, label: "1-Star"},
    { value: 2, label: "2-Star"},
    { value: 3, label: "3-Star"},
    { value: 4, label: "4-Star"},
    { value: 5, label: "5-Star"}
]

export const center_init = {
    lat: 39.09,
    lng: -94.56
};

export const cancellationPolicies = [
    "No Prepayment",
    "Free Cancellation"
]

export const bedsMap = {
    KING: "King",
    QUEEN: "Queen",
    SOFA_BED: "Sofa",
    FULL: "Full",
    TWIN: "Twin",
    SINGLE: "Single"
}

export const statusMap = {
    "Upcoming": ["RESERVED", "BOOKED"],
    "Completed": ["COMPLETED"],
    "Cancelled": ["CANCELLED"]
}
// const featureLists = {
//     property: propertyTypes,
//     hotelFacility: hotelFacilities,
//     roomFacility: roomFacility
// }

export function getDefaultPropertyTypeMap () {
    var propertyTypeMap = {}
    for (var i=0; i<propertyTypesMap.length; i++){
        propertyTypeMap[propertyTypesMap[i]] = false;
    }
    return propertyTypeMap;
}

export function getDefaultCancellationPoliciesMap() {
    var cancellationPolicyMap = {}
    for (var i=0; i<cancellationPolicies.length; i++){
        cancellationPolicyMap[cancellationPolicies[i]] = false;
    }
    return cancellationPolicyMap;
}

export function getDefaultPropertyRatingMap(){
    var propertyRatingMap = {}
    for (var i=0; i<propertyRatings.length; i++) {
        propertyRatingMap[propertyRatings[i]['value']] = false;
    }
    return propertyRatingMap;
}

export function getDefaultHotelFacilitiesMap () {
    var hotelFacilitiesMap = {}
    for (var i=0; i<hotelFacilities.length; i++){
        hotelFacilitiesMap[hotelFacilities[i]] = false;
    }
    return hotelFacilitiesMap;
}

export function getDefaultRoomAmenitiesMap () {
    var roomAmenitiesMap = {}
    for (var i=0; i<roomFacilities.length; i++){
        roomAmenitiesMap[roomFacilities[i]] = false;
    }
    return roomAmenitiesMap;
}
