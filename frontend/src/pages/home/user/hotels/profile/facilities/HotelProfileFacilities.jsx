import './hotelProfileFacilities.css'
import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import HotelProfileSidebar from "../HotelProfileSidebar";
import {hotelFacilities} from "../../../../../../assets/Lists";
import {getWithJwt, postWithJwt, putWithJwt} from "../../../../../../clients";
import {useParams} from "react-router-dom";
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";

function HotelProfileFacilities(props) {
    const [info, setInfo] = useState({});
    const { hotelId } = useParams()

    function fetchHotelFacilities(){
        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/facility`)
            .then(response=>response.json())
            .then(data => {
                let newFacilities = Object.fromEntries(hotelFacilities.map(i => [i, false]))
                for (var key of data.facility){
                    newFacilities[key] = true;
                }
                console.log(newFacilities)
                setInfo(newFacilities)
            })
            .catch(e => {console.error(e)})
    }


    function onSave(){
        var payloadFacilities = []
        for (var key in info){
            if (info[key]){
                payloadFacilities.push(key)
            }
        }
        var payload = { 'facility': payloadFacilities }
        console.log(payload)
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/facility`, payload)
    }

    useEffect(() => {
        fetchHotelFacilities()
    }, [])

    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <HotelProfileSidebar />
                <div className="profileContents">
                    <h1>Facilities</h1>
                    <div>
                        <div className="hotelFacilityList">
                            {Object.keys(info).map((item, index) => (
                                <div
                                    className="hotelFacility"
                                    key={index}>
                                    <input
                                        type="checkbox"
                                        checked={info[item]}
                                        onClick={e => {
                                            setInfo({...info, [item]: e.target.checked});
                                        }}/>
                                    <label>{item}</label>
                                </div>
                            ))}
                        </div>
                    </div>
                    <button onClick={onSave}>Save</button>
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default HotelProfileFacilities;