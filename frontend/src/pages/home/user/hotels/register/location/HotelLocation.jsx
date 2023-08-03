// import { Search, GpsFixed } from "material-ui/icons";
import React, {useEffect, useRef, useState} from "react";
import {GoogleMap, useJsApiLoader} from "@react-google-maps/api";
import {geocodeByAddress, getLatLng} from "react-places-autocomplete";
import {center_init} from "../../../../../../assets/Lists";
import {extractAddress} from "../../../../../../components/autocomplete/autocomplete";
import './hotelLocation.css'

const mapApiJs = "https://maps.googleapis.com/maps/api/js"

const containerStyle = {
    width: '600px',
    height: '600px'
};

const sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay))

const zoom = 15;

function HotelLocation(props) {

    const searchInput = useRef(null);
    const [searchAddress, setSearchAddress] = useState("");
    const [center, setCenter] = useState(center_init);
    const [marker, setMarker] = useState(null);

    const {address, setAddress, coordinates, setCoordinates, openWarnings, setOpenWarnings} = props;


    const handleFullAddress = (value) =>{
        setSearchAddress(value.target.value)
    }

    const placeMarker = async (newCoordinates) => {
        if (marker != null) {
            marker.setMap(null);
        }
        const newMarker = new window.google.maps.Marker({
            position: newCoordinates,
            map: map,
            draggable: true
        });
        setMarker(newMarker);
        setCenter(newCoordinates);

        window.google.maps.event.addListener(
            newMarker, 'drag', function(event) {
                setCoordinates({lat: newMarker.position.lat(), lng: newMarker.position.lng()});
            }
        )
    }

    const onChangeAddress = (autocomplete) => {
        const place = autocomplete.getPlace();
        const newAddress = extractAddress(place);
        console.log(place);
        var newCoordinates = {
            "lat": place.geometry.location.lat(),
            "lng": place.geometry.location.lng()
        }
        console.log(newAddress, newCoordinates)

        placeMarker(newCoordinates)
        console.log(newAddress)
        setCoordinates(newCoordinates);
        setAddress(newAddress)
    }

    const initAutocomplete = () => {
        if (!searchInput.current) return;

        // { types: ['(cities)']}
        const autocomplete = new window.google.maps.places.Autocomplete(searchInput.current); // only cities: ,
        autocomplete.setFields((["address_component", "geometry"]));
        autocomplete.addListener("place_changed", () => onChangeAddress(autocomplete))
    }


    // map // todo: remove google api key from code
    const { isLoaded } = useJsApiLoader({
        id: 'google-map-script',
        googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY
    })
    const [map, setMap] = React.useState(null)
    const onLoad = React.useCallback(function callback(map) {
        // This is just an example of getting and using the map instance!!! don't just blindly copy!
        // const bounds = new window.google.maps.LatLngBounds(center);
        // map.fitBounds(bounds);
        map.setZoom(zoom);
        setMap(map)
    }, [])
    const onUnmount = React.useCallback(function callback(map) {
        setMap(null)
    }, [])

    useEffect(() =>
        initAutocomplete()
    )

    console.log(address)
    return (

        <div className="hotelLocationContainer">
            <div className="hotelLocationSearch">
                <label>Search Location</label>
                <input
                    ref={searchInput}
                    type="text"
                    placeholder={"Search location..."}
                    value={searchAddress}
                    onChange={handleFullAddress}
                />
            </div>
            <div className="hotelLocationAddress">
                <h3>Hotel Location</h3>
                <label>Address line 1</label>
                <input
                    type="text"
                    maxLength="50"
                    value={address.addressLine1}
                    onChange={e => {
                        setOpenWarnings({...openWarnings, addressLine1: false})
                        setAddress({...address, addressLine1: e.target.value})
                    }}
                />
                { openWarnings.addressLine1 &&
                    <div className="hotelLocationInputWarning">
                        Please enter an address line.
                    </div>
                }
                <label>Address line 2</label>
                <input
                    type="text"
                    value={address.addressLine2}
                    onChange={e => {setAddress({...address, addressLine2: e.target.value})}}
                />
            <div className="addressItems">
                <div className="addressItemsElement">
                    <label>Neighborhood</label>
                    <input
                        type="text"
                        value={address.neighborhood}
                        onChange={e=>setAddress({...address, neighborhood: e.target.value})}>
                    </input>
                </div>
                <div className="addressItemsElement">
                    <label>City</label>
                    <div className="addressItemsElementInput">
                        <input
                            type="text"
                            value={address.city}
                            onChange={e => {
                                setOpenWarnings({...openWarnings, city: false})
                                setAddress({...address, city: e.target.value})
                            }}

                        />
                        { openWarnings.city &&
                            <div className="hotelLocationInputWarning">
                                Please enter the city name.
                            </div>
                        }
                    </div>
                </div>
                <div className="addressItemsElement">
                    <label>Zipcode</label>
                    <input
                        type="text"
                        value={address.zipcode}
                        onChange={e => {setAddress({...address, zipcode: e.target.value})}}
                />
            </div>
            </div>
            <div className="addressItems">
                <div className="addressItemsElement">
                    <label>State</label>
                    <input
                        type="text"
                        value={address.state}
                        onChange={e => {setAddress({...address, state: e.target.value})}}
                    />
                </div>
                <div className="addressItemsElement">
                    <label>Country</label>
                    <div className="addressItemsElementInput">
                        <input
                            type="text"
                            value={address.country}
                            onChange={e => {
                                setOpenWarnings({...openWarnings, country: false})
                                setAddress({...address, country: e.target.value})
                            }}
                        />
                        { openWarnings.country &&
                            <div className="hotelLocationInputWarning">
                                Please enter a country name.
                            </div>
                        }
                    </div>
                </div>
                <div className="addressItemsElement">
                </div>
            </div>
                <div className="hotelLocationMap">
                    <GoogleMap
                        mapContainerStyle={containerStyle}
                        center={center}
                        zoom={zoom}
                        onLoad={onLoad}
                        onUnmount={onUnmount}
                    >
                        { /* Child components, such as markers, info windows, etc. */ }
                        <></>
                    </GoogleMap>
                    <div className="addressLatLng">
                        <p>Coordinates: <span>({Math.round(coordinates.lat * 10000) / 10000}, {Math.round(coordinates.lng * 10000) / 10000})</span></p>
                    </div>
                </div>
            </div>
        </div>

    );
}

export default HotelLocation;