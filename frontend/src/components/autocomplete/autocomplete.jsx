
export const extractAddress = (place, cities=false) => {

    var newAddress;
    if (cities){
        newAddress = {
            // neighborhood: "",
            city: "",
            county: "",
            state: "",
            country: "",
        }
    } else {
        newAddress = {
            neighborhood: "",
            street_number: "",
            route: "",
            city: "",
            county: "",
            state: "",
            zipcode: "",
            country: "",
            addressLine1: "",
            addressLine2: ""
        }
    }

    if (!Array.isArray(place?.address_components)){
        return newAddress;
    }
    console.log(place.address_components);

    place.address_components.forEach(component => {
        const types = component.types;
        const value = component.long_name;

        if (!cities) {
            if (types.includes("street_number")) {
                newAddress.street_number = value;
            }

            if (types.includes("route")) {
                newAddress.route = value;
            }

            if (types.includes("neighborhood")) {
                newAddress.neighborhood = value;
            }

            if (types.includes("postal_code")) {
                newAddress.zipcode = value;
            }

        }

        if (types.includes("locality")) {
            newAddress.city = value;
        }

        if (types.includes("administrative_area_level_2")){
            newAddress.county = value;
        }

        if (types.includes("administrative_area_level_1")){
            newAddress.state = value;
        }

        if (types.includes("country")){
            newAddress.country = value
        }
    });

    if (!cities){
        newAddress.addressLine1 = newAddress.street_number === "" ?
            newAddress.route : newAddress.street_number + " " + newAddress.route;
    }

    return newAddress;
}



// const reverseGeocode = ({ latitude: lat, longitude: lng}) => {
//     const url = `${geocodeJson}?key={}&latlng=${lat},${lng}`;
//     fetch(url)
//         .then(response => response.json())
//         .thens(location => {
//             console.log(location);
//         })
// }

// const findMyLocation = () => {
//     alert("find my location");
//     if (navigator.geolocation){
//         navigator.geolocation.getCurrentPosition(position => {
//             reverseGeocode(position.coords);
//         })
//     }
// }