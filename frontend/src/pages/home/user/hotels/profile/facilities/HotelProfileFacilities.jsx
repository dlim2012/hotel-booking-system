import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import ProfileSidebar from "../../../user/profile/ProfileSidebar";
import HotelProfileSidebar from "../HotelProfileSidebar";
import HotelFacilities from "../../register/facilities/HotelFacilities";
import {hotelFacilities} from "../../../../../../assets/Lists";
import {getWithJwt, postWithJwt, putWithJwt} from "../../../../../../clients";
import {useParams} from "react-router-dom";

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
                        <div className="facility-list">
                            {Object.keys(info).map((item, index) => (
                                <div key={index}>
                                    <input
                                        type="checkbox"
                                        checked={info[item]}
                                        onClick={e => {
                                            setInfo({...info, [item]: e.target.checked});
                                        }}/>
                                    <span>{item}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                    <button onClick={onSave}>Save</button>
                </div>
            </div>
        </div>
    );
}

export default HotelProfileFacilities;