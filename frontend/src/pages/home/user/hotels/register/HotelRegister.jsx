import React, {useState} from 'react';
import Navbar from "../../../../../components/navbar/Navbar";
import HotelLocation from "./location/HotelLocation";
import HotelInfo from "./info/HotelInfo";
import HotelFacilities from "./facilities/HotelFacilities";
import ProgressBar from "./ProgressBar";
import {center_init, hotelFacilities} from "../../../../../assets/Lists";
import {postWithJwt} from "../../../../../clients";
import {useNavigate} from "react-router-dom";


function HotelRegister(props) {
    const navigate = useNavigate();
    const [page, setPage] = useState(0);
    const FormTitles = ["Address", "Hotel Info", "Facilities"];
    const [address, setAddress] = useState({
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
    });
    const [coordinates, setCoordinates] = useState(center_init);
    const [hotelInfo, setHotelInfo ] = useState({
        name: "", description: "", propertyType: "Hotel", phone: "", fax: "", website: "", email: "", stars: ""
    });

    const [facilities, setFacilities] = useState(Object.fromEntries(hotelFacilities.map(i => [i, false])));


    const handleSubmit = () => {
        delete address['street_number']
        delete address['route']
        var payload = {...address, ...hotelInfo}
        payload['latitude'] = coordinates.lat;
        payload['longitude'] = coordinates.lng;

        var payLoadFacilities = []
        for (let facility in facilities){
            if (facilities[facility]){
                payLoadFacilities.push(facility)
            }
        }
        payload['facilityDisplayNameList'] = payLoadFacilities
        console.log(payload);

        postWithJwt("/api/v1/hotel/hotel/register", payload)
            .then(response => response.json())
            .then(data => {
                console.log(data)

                // navigate("/hotel/rooms/manage", {state: {hotelId: data.hotelId}})
                navigate("/user/hotel")
            })
            .catch(e => console.error(e))
            .finally()
    }


    return (
        <div>
            <Navbar />
            <div className="form">
                <ProgressBar bgcolor="#febb02" page={page} numPages={FormTitles.length}/>
                <div className="form-container">
                    <div className="header">
                        <h1>{FormTitles[page]}</h1>
                    </div>
                    <div className="body">
                        {page === 0 && <HotelLocation address={address} setAddress={setAddress} coordinates={coordinates} setCoordinates={setCoordinates}/>}
                        {page === 1 && <HotelInfo info={hotelInfo} setInfo={setHotelInfo}/>}
                        {page === 2 && <HotelFacilities info={facilities} setInfo={setFacilities} />}
                    </div>
                    <div className="footer">
                        <button
                            disabled={page === 0}
                            onClick={()=> {
                                setPage((page) => page - 1)
                            }}
                        >Prev</button>
                        { page < FormTitles.length - 1 &&
                        <button
                            onClick={()=>{

                                console.log(address, coordinates, hotelInfo, facilities);
                            setPage((page) => page + 1
                            )
                        }}>Next</button>
                        }
                        { page === FormTitles.length - 1 &&
                            <button
                                onClick={()=>{handleSubmit()}}
                            >Submit</button>
                        }
                    </div>
                </div>
            </div>
        </div>
    );
}

export default HotelRegister;