import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import RoomProfileSidebar from "../RoomProfileSidebar";
import RoomFacilities from "../../register/facilities/RoomFacilities";
import {hotelFacilities, roomFacilities} from "../../../../../../../assets/Lists";
import {getWithJwt, putWithJwt} from "../../../../../../../clients";
import {useParams} from "react-router-dom";
import MailList from "../../../../../../../components/mailList/MailList";
import Footer from "../../../../../../../components/footer/Footer";
import './roomsProfileFacilities.css'

function RoomsProfileFacilities(props) {

    const { hotelId, roomsId } = useParams();

    const [info, setInfo] = useState({});

    function fetchRoomsFacilities(){
        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/facility`)
            .then(response=>response.json())
            .then(data => {
                let newFacilities = Object.fromEntries(roomFacilities.map(i => [i, false]))
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
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/facility`, payload);
    }

    useEffect(() => {
        fetchRoomsFacilities()
    }, [])

    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <RoomProfileSidebar />
                <div className="profileContents">
                    <div className="profileTitle">
                        <h1>Facilities</h1>
                    </div>
                    <div>
                        <div className="roomFacilityList">
                            {roomFacilities.map((item, index) => (
                                <div
                                    className="roomFacility"
                                    key={index}>
                                    <input
                                        type="checkbox"
                                        checked={info[item]}
                                        onChange={e => {
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

export default RoomsProfileFacilities;