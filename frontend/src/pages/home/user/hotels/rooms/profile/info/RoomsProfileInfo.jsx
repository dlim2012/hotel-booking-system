import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import HotelProfileSidebar from "../../../profile/HotelProfileSidebar";
import RoomProfileSidebar from "../RoomProfileSidebar";
import RoomInfo from "../../register/info/RoomInfo";
import {getWithJwt, putWithJwt} from "../../../../../../../clients";
import {useParams} from "react-router-dom";
import {bedsMap} from "../../../../../../../assets/Lists";

function RoomsProfileInfo(props) {
    const [info, setInfo] = useState({});
    const {hotelId, roomsId} = useParams()

    const onChange = (key, value) => {
        setInfo({...info, [key]: value})
    }

    function fetchRoomsProfile() {
        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/info`)
            .then(response=>response.json())
            .then(data =>{
                // convert roomsBeds from list to map
                var roomsBeds = {}
                for (var bed of data.roomsBeds){
                    roomsBeds[bed.bed] = bed.quantity;
                }
                data.roomsBeds = roomsBeds;

                console.log(data);
                setInfo(data);
            })
            .catch(e => {
                console.error(e)})
    }

    function onSave() {
        // convert roomsBeds from map to list
        var roomsBeds = []
        for (var key of Object.keys(info.roomsBeds)){
            if (info.roomsBeds[key] !== 0) {
                roomsBeds.push({bed: key, quantity: info.roomsBeds[key]})
            }
        }
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/info`, {...info, ["roomsBeds"]: roomsBeds})
            .catch(e => {
                console.error(e)})
    }

    useEffect(()=> {
        fetchRoomsProfile();
    }, [])

    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <RoomProfileSidebar />
                <div className="profileContents">
                    <div className="profileTitle">
                        <h1>General Information</h1>
                    </div>
                    <div>
                        <div className="formItem">
                            <label className="formLabel">Display Name</label>
                            <input
                                value={info["displayName"]}
                                onChange={e => setInfo({...info, ["displayName"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Description</label>
                            <input
                                value={info["description"]}
                                onChange={e => setInfo({...info, ["description"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Number of beds</label>
                            {
                                bedsMap.map((item, index) => {
                                    if (info.roomsBeds == null){
                                        return;
                                    }
                                    return (
                                        <div className="formItemSub">
                                            <label className="formLabel">{item}</label>
                                            <input
                                                type="number"
                                                min="0"
                                                max="1000"
                                                value={info.roomsBeds[item] == null ? 0 : info.roomsBeds[item]}
                                                onChange={e => setInfo({...info, ["roomsBeds"]: {...info.roomsBeds, [item]: e.target.value}})}
                                            />
                                        </div>
                                    );
                                })
                            }
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Maximum adults</label>
                            <input
                                type="number"
                                min="0"
                                max="1000"
                                value={info["maxAdult"]}
                                onChange={e => setInfo({...info, ["maxAdult"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Maximum children</label>
                            <input
                                type="number"
                                min="0"
                                max="1000"
                                value={info["maxChild"]}
                                onChange={e => setInfo({...info, ["maxChild"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Quantity</label>
                            <input
                                type="number"
                                min="0"
                                max="1000"
                                value={info["quantity"]}
                                onChange={e => setInfo({...info, ["quantity"]: e.target.value})}
                            />
                            <span>Note: Existing reservation/bookings will not be affected even if there are not enough rooms after changing quantity. Room numbers may be automatically modified. Please cancel reservations manually through this link.</span>
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Min Price</label>
                            <input
                                type="number"
                                step="0.01"
                                min="0.00"
                                value={info["priceMin"]}
                                onChange={e => setInfo({...info, ["priceMin"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Max Price</label>
                            <input
                                type="number"
                                step="0.01"
                                min="0.00"
                                value={info["priceMax"]}
                                onChange={e => setInfo({...info, ["priceMax"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">No prepayment Days</label>
                            <input
                                type="number"
                                step="1"
                                min="0"
                                value={info["noPrepaymentDays"]}
                                onChange={e => setInfo({...info, ["noPrepaymentDays"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Free cancellation days</label>
                            <input
                                type="number"
                                step="1"
                                min="0"
                                value={info["freeCancellationDays"]}
                                onChange={e => setInfo({...info, ["freeCancellationDays"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Check-out time</label>
                            <input
                                type="time"
                                value = {info["checkOutTime"]}
                                onChange={e => setInfo({...info, ["checkOutTime"]: e.currentTarget.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Check-in time</label>
                            <input
                                type="time"
                                value = {info["checkInTime"]}
                                onChange={e => setInfo({...info, ["checkInTime"]: e.currentTarget.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Activate</label>
                            <input
                                type="checkbox"
                                checked= {info["isActive"]}
                                onChange={e => setInfo({...info, ["isActive"]: e.target.checked})}
                            />
                        </div>
                        { info["isActive"] &&
                            <div className="formItem">
                                <label className="formLabel">First available check-in date</label>
                                <input
                                    type="date"
                                    value = {info["availableFrom"]}
                                    onChange={e => setInfo({...info, ["availableFrom"]: e.target.value})}
                                />
                            </div>
                        }
                        { info["isActive"] &&
                            <div className="formItem">
                                <label className="formLabel">Last available check-out date</label>
                                <input
                                    type="date"
                                    value = {info["availableUntil"]}
                                    onChange={e => setInfo({...info, ["availableUntil"]: e.target.value})}
                                />
                            </div>
                        }

                    </div>
                    <button onClick={onSave}>Save</button>
                </div>
            </div>
        </div>
    );
}

export default RoomsProfileInfo;