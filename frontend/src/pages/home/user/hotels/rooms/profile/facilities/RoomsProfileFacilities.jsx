import './roomsProfileFacilities.css'
import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import RoomProfileSidebar from "../RoomProfileSidebar";
import RoomFacilities from "../../register/facilities/RoomFacilities";
import {hotelFacilities, roomFacilities} from "../../../../../../../assets/Lists";
import {getWithJwt, putWithJwt} from "../../../../../../../clients";
import {useParams} from "react-router-dom";
import MailList from "../../../../../../../components/mailList/MailList";
import Footer from "../../../../../../../components/footer/Footer";
import {TailSpin} from "react-loader-spinner";

function RoomsProfileFacilities(props) {

    const { hotelId, roomsId } = useParams();

    const [info, setInfo] = useState({});
    const [saved, setSaved] = useState(false);
    const [fetching, setFetching] = useState(false);

    function fetchRoomsFacilities(){
        setFetching(true);
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
            .finally(() => {
                setFetching(false);
            })
    }

    function onSave(){
        var payloadFacilities = []
        for (var key in info){
            if (info[key]){
                payloadFacilities.push(key)
            }
        }
        var payload = { 'facility': payloadFacilities }
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/facility`, payload)
            .then(() => {
                setSaved(true)
            })
            .catch(e => {
                console.error(e)
            })
            .finally(() => {
                    fetchRoomsFacilities();
            })
        ;
    }

    useEffect(() => {
        fetchRoomsFacilities()
    }, [])

    if (fetching){
        return (
            <div>
                <Navbar />
                <div className="profileContainer">
                    <RoomProfileSidebar />
                    <div className="loading">
                        <TailSpin
                            height="80"
                            width="80"
                            color="#0071c2"
                            ariaLabel="tail-spin-loading"
                            radius="1"
                            wrapperStyle={{}}
                            wrapperClass=""
                            visible={true}
                        />
                    </div>
                </div>
            </div>
        )
    }

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
                                            setSaved(false);
                                            setInfo({...info, [item]: e.target.checked});
                                        }}/>
                                    <label>{item}</label>
                                </div>
                            ))}
                        </div>
                    </div>
                    <button onClick={onSave}>Save</button>
                    {saved && <p className="roomsFacilitySaved">Saved!</p>}
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default RoomsProfileFacilities;