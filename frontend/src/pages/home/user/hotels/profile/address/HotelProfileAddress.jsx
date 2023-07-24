import React, {useEffect, useRef, useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import HotelProfileSidebar from "../HotelProfileSidebar";
import {extractAddress} from "../../../../../../components/autocomplete/autocomplete";
import {GoogleMap, useJsApiLoader} from "@react-google-maps/api";
import {getWithJwt, putWithJwt} from "../../../../../../clients";
import {useParams} from "react-router-dom";

const mapApiJs = "https://maps.googleapis.com/maps/api/js"

const containerStyle = {
    width: '600px',
    height: '600px'
};

const zoom = 15;

function HotelProfileAddress(props) {
    const searchInput = useRef(null);
    const [address, setAddress] = useState({});
    const [coordinates, setCoordinates] = useState({});
    const [searchAddress, setSearchAddress] = useState("");
    const [center, setCenter] = useState({});
    const [marker, setMarker] = useState(null);


    const {hotelId} = useParams();


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
        setAddress(newAddress)
        var newCoordinates = {
            "lat": place.geometry.location.lat(),
            "lng": place.geometry.location.lng()
        }
        console.log(newAddress, newCoordinates)
        setCoordinates(newCoordinates);
        setCenter(newCoordinates)

        placeMarker(newCoordinates)
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

    function fetchAddress(){
        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/address`)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setAddress(data)
                var newCoordinates = {lat: data.latitude, lng: data.longitude};
                setCoordinates(newCoordinates)
                setCenter(newCoordinates);
                placeMarker(newCoordinates)
            })
    }

    function onSave(){
        var payload = {...address, ["latitude"]: coordinates.lat, ["longitude"]: coordinates.lng}
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/address`, payload);
    }

    useEffect(() =>
        initAutocomplete()
    )

    useEffect(() => {
        fetchAddress()
    }, [])

    // if (Object.keys(address).length === 0){
    //     return;
    // }

    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <HotelProfileSidebar />
                <div className="profileContents">
                    <h1>Address</h1>

                    <div>
                        <div className="search">
                            <span></span>
                            <p>Search Location:</p>
                            <input ref={searchInput} type="text" placeholder={"Search location..."}
                                   value={searchAddress} onChange={handleFullAddress}/>
                            {/*<button>GpsFixed</button>*/}
                        </div>
                        <div className="address">
                            <p>Address line 1:  <input
                                type="text"
                                value={address.addressLine1}
                                onChange={e => {setAddress({...address, addressLine1: e.target.value})}}></input></p>
                            <p>Address Line 2: <input value={address.addressLine2} onChange={e=>setAddress({...address, addressLine2: e.target.value})}></input></p>
                            <p>Neighborhood: <input value={address.neighborhood} onChange={e=>setAddress({...address, neighborhood: e.target.value})}></input></p>
                            <p>City: <input value={address.city} onChange={e => {setAddress({...address, city: e.target.value})}}/></p>
                            <p>State: <input value={address.state} onChange={e => {setAddress({...address, state: e.target.value})}} /></p>
                            <p>Country: <input value={address.country} onChange={e => {setAddress({...address, country: e.target.value})}} /></p>
                            <p>Zipcode: <input value={address.zipcode} onChange={e => {setAddress({...address, zipcode: e.target.value})}} /></p>
                        </div>
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
                    <button onClick={onSave}>Save</button>
                </div>
            </div>
        </div>
    );
}

export default HotelProfileAddress;