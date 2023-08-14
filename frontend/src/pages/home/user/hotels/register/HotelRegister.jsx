import React, {useState} from 'react';
import Navbar from "../../../../../components/navbar/Navbar";
import HotelLocation from "./location/HotelLocation";
import HotelInfo from "./info/HotelInfo";
import HotelFacilities from "./facilities/HotelFacilities";
import ProgressBar from "./ProgressBar";
import {center_init, hotelFacilities} from "../../../../../assets/Lists";
import {postWithJwt} from "../../../../../clients";
import {useNavigate} from "react-router-dom";
import './hotelRegister.css'
import MailList from "../../../../../components/mailList/MailList";
import Footer from "../../../../../components/footer/Footer";
import {validateEmail} from "../../utils/inputValidation";


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

    var defaultLocationWarnings = {addressline1: false, city: false, country: false}
    var defaultInfoWarnings = {name: false, phone: false, email: false}
    const [openWarnings, setOpenWarnings] = useState(defaultLocationWarnings)

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
                <div className="progressBarContainer">
                    <ProgressBar bgcolor="#febb02" page={page} numPages={FormTitles.length}/>
                </div>
                <div className="form-container">
                    <div className="hotelRegisterHeader">
                        <h1>{FormTitles[page]}</h1>
                    </div>
                    <div className="body">
                        {page === 0 &&
                            <HotelLocation
                                address={address}
                                setAddress={setAddress}
                                coordinates={coordinates}
                                setCoordinates={setCoordinates}
                                openWarnings={openWarnings}
                                setOpenWarnings={setOpenWarnings}
                            />
                        }
                        {page === 1 &&
                            <HotelInfo
                                info={hotelInfo}
                                setInfo={setHotelInfo}
                                openWarnings={openWarnings}
                                setOpenWarnings={setOpenWarnings}
                            />
                        }
                        {page === 2 &&
                            <HotelFacilities
                                info={facilities}
                                setInfo={setFacilities}
                            />
                        }
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
                                if (page === 0){
                                    var newOpenWarnings = {
                                        addressLine1: address.addressLine1.length === 0,
                                        city: address.city.length === 0,
                                        country: address.country.length === 0
                                    }
                                    for (let key of Object.keys(newOpenWarnings)){
                                        if (newOpenWarnings[key]){
                                            setOpenWarnings(newOpenWarnings);
                                            return;
                                        }
                                    }
                                    setOpenWarnings(defaultInfoWarnings);
                                    setPage((page) => page + 1)
                                } else if (page === 1) {
                                    var newOpenWarnings = {
                                        name: hotelInfo.name.length < 3,
                                        phone: hotelInfo.phone.length === 0,
                                        email: !validateEmail(hotelInfo.email)
                                    }
                                    console.log(newOpenWarnings)
                                    for (let key of Object.keys(newOpenWarnings)){
                                        if (newOpenWarnings[key]){
                                            setOpenWarnings(newOpenWarnings);
                                            return;
                                        }
                                    }
                                    setPage((page) => page + 1)
                                } else {
                                        setPage((page) => page + 1)
                                    }
                                }
                        }>Next</button>
                        }
                        { page === FormTitles.length - 1 &&
                            <button
                                onClick={()=>{handleSubmit()}}
                            >Submit</button>
                        }
                    </div>
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default HotelRegister;