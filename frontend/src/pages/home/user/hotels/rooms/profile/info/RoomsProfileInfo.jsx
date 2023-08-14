import './roomsProfileInfo.css'
import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import HotelProfileSidebar from "../../../profile/HotelProfileSidebar";
import RoomProfileSidebar from "../RoomProfileSidebar";
import RoomInfo from "../../register/info/RoomRegisterInfo";
import {getWithJwt, putWithJwt} from "../../../../../../../clients";
import {useParams} from "react-router-dom";
import {bedsMap} from "../../../../../../../assets/Lists";
import MailList from "../../../../../../../components/mailList/MailList";
import Footer from "../../../../../../../components/footer/Footer";
import {TailSpin} from "react-loader-spinner";

function RoomsProfileInfo(props) {
    const [info, setInfo] = useState({});
    const {hotelId, roomsId} = useParams()
    var defaultRoomInfoWarning = {
        displayName: false,
        shortNameEmpty: false,
        shortNameNumber: false,
        numBed: false,
        priceMax: false,
        priceMin: false,
        timeOrder: false,
        dateOrder: false,
    }
    const [openWarnings, setOpenWarnings] = useState(defaultRoomInfoWarning);
    const [fetching, setFetching] = useState(false);
    const [saved, setSaved] = useState(false);

    function fetchRoomsProfile() {
        setFetching(true);
        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/info`)
            .then(response=>response.json())
            .then(data =>{
                console.log(data)
                // convert roomsBeds from list to map
                var roomsBeds = {}
                for (var bed of data.roomsBeds){
                    roomsBeds[bed.bed] = bed.quantity;
                }
                data.roomsBeds = roomsBeds;

                data.priceMin = data.priceMin / 100;
                data.priceMax = data.priceMax / 100;

                setInfo(data);
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                setFetching(false);
            })
    }

    function onSave() {
        // convert roomsBeds from map to list
        var roomsBeds = []
        var numBedZero = true;
        for (var key of Object.keys(info.roomsBeds)){
            if (info.roomsBeds[key] !== 0) {
                roomsBeds.push({bed: key, quantity: info.roomsBeds[key]})
            }
            var numBedZero = false;
        }


        var newOpenWarnings = {
            displayName: info.displayName.length < 5,
            shortNameEmpty: info.shortName.length === 0,
            shortNameNumber: /\d/.test(info.shortName),
            numBed: numBedZero,
            priceMax: info.priceMin === "0.00",
            priceMin: info.priceMax === "0.00",
            timeOrder: info.checkOutTime > info.checkInTime,
            dateOrder: info.availableFrom > info.availableUntil
        }
        for (let key of Object.keys(newOpenWarnings)){
            if (newOpenWarnings[key]){
                setOpenWarnings(newOpenWarnings);
                return;
            }
        }
        var payload = {...info, ["roomsBeds"]: roomsBeds}
        for (let key of ["priceMin", "priceMax"]){
            payload[key] = Math.round(parseFloat(payload[key]) * 100)
        }

        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/info`, payload)
            .then(() => {
                setSaved(true);
            })
            .catch(e => {
                console.error(e)})
    }

    useEffect(()=> {
        fetchRoomsProfile();
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
                        <h1>General Information</h1>
                    </div>
                    <div>
                        <div className="profileFormItem">
                            <label className="formLabel">Display Name</label>
                            <input
                                value={info["displayName"]}
                                onChange={e =>
                                {
                                    setSaved(false);
                                    if (e.target.value.length >= 5) {
                                        setOpenWarnings({...openWarnings, ["displayName"]: false})
                                    }
                                    setInfo({...info, ["displayName"]: e.target.value})
                                }
                            }
                            />
                            {openWarnings.displayName &&
                                <span className="roomRegisterInfoWarning">Please enter a display name. (min length: 5)</span>
                            }
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Short Name</label>
                            <input
                                value={info["shortName"]}
                                maxLength="10"
                                onChange={e =>
                                {
                                    setSaved(false);
                                    if (e.target.value.length > 0) {
                                        if (/\d/.test(e.target.value)) {
                                            setOpenWarnings({...openWarnings, ["shortNameEmpty"]: false, ["shortNameNumber"]: false})
                                        } else {
                                            setOpenWarnings({...openWarnings, ["shortNameEmpty"]: false});
                                        }
                                    } else if (/\d/.test(e.target.value)){
                                        setOpenWarnings({...openWarnings, ["shortNameNumber"]: false})
                                    }
                                    setInfo({...info, ["shortName"]: e.target.value})
                                }
                                }
                            />
                            {openWarnings.shortNameEmpty &&
                                <span className="roomRegisterInfoWarning">Please enter a short name.</span>
                            }
                            {openWarnings.shortNameNumber &&
                                <span className="roomRegisterInfoWarning">Short name shouldn't contain any digit.</span>
                            }
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Description</label>
                            <textarea
                                value={info["description"]}
                                onChange={e => {
                                    setSaved(false);
                                    setInfo({...info, ["description"]: e.target.value})
                                }}
                            />
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Quantity</label>
                            <input
                                type="number"
                                min="1"
                                max="1000"
                                value={info["quantity"]}
                                onChange={e => {
                                    setSaved(false);
                                    setInfo({...info, ["quantity"]: e.target.value})
                                }}
                            />
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Maximum adults</label>
                            <input
                                type="number"
                                min="1"
                                max="1000"
                                value={info["maxAdult"]}
                                onChange={e => {
                                    setSaved(false);
                                    setInfo({...info, ["maxAdult"]: e.target.value})
                                }}
                            />
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Maximum children</label>
                            <input
                                type="number"
                                min="0"
                                max="1000"
                                value={info["maxChild"]}
                                onChange={e => {
                                    setSaved(false);
                                    setInfo({...info, ["maxChild"]: e.target.value})
                                }}
                            />
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Number of beds</label>
                            <div className="profileFormItemRow">
                                {
                                    Object.keys(bedsMap).map((item, index) => {
                                        if (info.roomsBeds == null){
                                            return;
                                        }
                                        return (
                                            <div className="profileFormItemSub">
                                                <label className="formLabel">{item}</label>
                                                <input
                                                    type="number"
                                                    min="0"
                                                    max="100"
                                                    value={info.roomsBeds[item] == null ? 0 : info.roomsBeds[item]}
                                                    onKeyDown={e => {
                                                        if (!/[0-9\\/]+/.test(e.key)){
                                                            e.preventDefault();
                                                        }
                                                    }}
                                                    onChange={e => {
                                                        setSaved(false);
                                                        setOpenWarnings({...openWarnings, ["numBed"]: false})
                                                        setInfo({...info, ["roomsBeds"]: {...info.roomsBeds, [item]: Math.min(100, e.target.value)}})}
                                                    }
                                                />
                                            </div>
                                        );
                                    })
                                }
                            </div>
                            {openWarnings.numBed &&
                                <span className="roomRegisterInfoWarning">Rooms should have at least one bed.</span>
                            }
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Minimum Price (Unit: dollars)</label>
                            <input
                                type="number"
                                step="0.01"
                                min="0.00"
                                value={info["priceMin"]}
                                onChange={e => {
                                    setSaved(false);
                                    if (e.target.value !== "0.00") {
                                        setOpenWarnings({...openWarnings, ["priceMin"]: false})
                                    }
                                    setInfo({...info, ["priceMin"]: e.target.value})
                                }
                                }
                            />
                            {openWarnings.priceMax &&
                                <span className="roomRegisterInfoWarning">Please set a price bigger than $0.00.</span>
                            }
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Maximum Price (Unit: dollars)</label>
                            <input
                                type="number"
                                step="0.01"
                                min="0.00"
                                value={info["priceMax"]}
                                onChange={e => {
                                    setSaved(false);
                                    if (e.target.value !== "0.00") {
                                        setOpenWarnings({...openWarnings, ["priceMax"]: false})
                                    }
                                    setInfo({...info, ["priceMax"]: e.target.value})
                                }}
                            />
                            {openWarnings.priceMax &&
                                <span className="roomRegisterInfoWarning">Please set a price bigger than $0.00.</span>
                            }
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">No prepayment Days</label>
                            <input
                                type="number"
                                step="1"
                                min="0"
                                onKeyDown={e => {
                                    if (!/[0-9\\/]+/.test(e.key)){
                                        e.preventDefault();
                                    }
                                }}
                                value={info["noPrepaymentDays"]}
                                onChange={e => {
                                    setSaved(false);
                                    setInfo({...info, ["noPrepaymentDays"]: e.target.value})
                                }}
                            />
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Free cancellation days</label>
                            <input
                                type="number"
                                step="1"
                                min="0"
                                value={info["freeCancellationDays"]}
                                onKeyDown={e => {
                                    if (!/[0-9\\/]+/.test(e.key)){
                                        e.preventDefault();
                                    }
                                }}
                                onChange={e => {
                                    setSaved(false);
                                    setInfo({...info, ["freeCancellationDays"]: e.target.value})
                                }}
                            />
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Check-out time</label>
                            <input
                                type="time"
                                value = {info["checkOutTime"]}
                                onChange={e =>{
                                    setSaved(false);
                                    if (e.target.value <= info.checkInTime) {
                                        setOpenWarnings({...openWarnings, ["timeOrder"]: false})
                                    }
                                    setInfo({...info, ["checkOutTime"]: e.currentTarget.value})
                                }}
                            />
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Check-in time</label>
                            <input
                                type="time"
                                value = {info["checkInTime"]}
                                onChange={e => {
                                    setSaved(false);
                                    if (e.target.value >= info.checkOutTime) {
                                        setOpenWarnings({...openWarnings, ["timeOrder"]: false})
                                    }
                                    setInfo({...info, ["checkInTime"]: e.currentTarget.value})
                                }}
                            />
                            { openWarnings.timeOrder &&
                                <span className="roomRegisterInfoWarning">Check-In time has to be later than check-out time.</span>
                            }
                        </div>
                        {/*<div className="profileFormItemCheckBox">*/}
                        {/*    <label className="formLabel">Activate</label>*/}
                        {/*    <input*/}
                        {/*        type="checkbox"*/}
                        {/*        checked= {info["isActive"]}*/}
                        {/*        onChange={e => setInfo({...info, ["isActive"]: e.target.checked})}*/}
                        {/*    />*/}
                        {/*</div>*/}
                        { info["isActive"] &&
                            <div className="profileFormItem">
                                <label className="formLabel">First available check-in date</label>
                                <input
                                    type="date"
                                    value = {info["availableFrom"]}
                                    onChange={e => {
                                        setSaved(false);
                                        if (e.target.value < info.availableUntil) {
                                            setOpenWarnings({...openWarnings, ["dateOrder"]: false})
                                        }
                                        setInfo({...info, ["availableFrom"]: e.target.value})}
                                    }
                                />
                            </div>
                        }
                        { info["isActive"] &&
                            <div className="profileFormItem">
                                <label className="formLabel">Last available check-out date</label>
                                <input
                                    type="date"
                                    value = {info["availableUntil"]}
                                    onChange={e => {
                                        setSaved(false);
                                        if (info.availableFrom < e.target.value) {
                                            setOpenWarnings({...openWarnings, ["dateOrder"]: false})
                                        }
                                        setInfo({...info, ["availableUntil"]: e.target.value})}
                                    }
                                />
                            </div>
                        }
                        { info["isActive"] && openWarnings.dateOrder &&
                            <span className="roomRegisterInfoWarning">No available dates.</span>
                        }
                    </div>
                    <span className="profileFormItemNote">Note: Existing reservation/bookings will not be affected due to quantity and available dates changes.</span>
                    <button onClick={onSave}>Save</button>
                    {saved && <p className="roomsProfileInfoSaved">Saved!</p> }
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default RoomsProfileInfo;