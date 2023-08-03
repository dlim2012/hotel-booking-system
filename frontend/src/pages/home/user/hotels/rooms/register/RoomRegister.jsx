import React, {useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import ProgressBar from "../../register/ProgressBar";
import RoomGeneralInfo from "./info/RoomRegisterInfo";
import RoomFacilities from "./facilities/RoomFacilities";
import {postWithJwt} from "../../../../../../clients";
import {bedsMap, bedsMap2, roomFacilities} from "../../../../../../assets/Lists";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";

function RoomRegister(props) {
    var today = new Date();
    var year = today.toLocaleString("default", {year: "numeric"})
    var month = today.toLocaleString("default", {month: "2-digit"})
    var day = today.toLocaleString("default", {day: "2-digit"})
    var formattedDate = year + "-" + month + "-" + day;

    const { hotelId } = useParams()

    var bedInfoDtoList = {};
    for (let bed of Object.keys(bedsMap2)){
        bedInfoDtoList[bed] = "0";
    }

    const location = useLocation();
    const navigate= useNavigate();
    const [page, setPage] = useState(0);
    const FormTitles = ["Room Info", "Facilities"]
    const [generalInfo, setGeneralInfo] = useState({
        displayName: "", shortName: "", description: "",
        maxAdult: "1", maxChild: "0",
        quantity: "1", priceMax: "0.00", priceMin: "0.00",
        checkOutTime: "11:00", checkInTime: "18:00",
        isActive: false, freeCancellationDays: "0", noPrepaymentDays: "0", paymentOption: "PREPAYMENT",
        availableFrom: formattedDate, availableUntil: "2099-12-31"
    });
    const [bedInfo, setBedInfo] = useState(bedInfoDtoList);
    const [facilities, setFacilities] = useState(
        Object.fromEntries(roomFacilities.map(i => [i, false]))
    );

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
    const [openWarnings, setOpenWarnings] = useState(defaultRoomInfoWarning)

    const timeToInt = (time) => {
        var segments = time.split(':')
        return parseInt(segments[0]) * 60 + parseInt(segments[1]);
    }

    const handleSubmit = () => {
        var payload = {...generalInfo};

        payload["maxAdult"] = parseInt(payload["maxAdult"]);
        for (let key of ["maxAdult", "maxChild", "numberOfBeds", "quantity"]){
            payload[key] = parseInt(payload[key])
        }
        for (let key of ["priceMin", "priceMax"]){
            payload[key] = Math.round(parseFloat(payload[key]) * 100)
        }
        for (let key of ["checkInTime", "checkOutTime"]){
            payload[key] = timeToInt(payload[key])
        }

        var bedInfoDtoList = [];
        console.log("bedInfo", bedInfo)
        for (let bed of Object.keys(bedInfo)){
            console.log(bed)
            bedInfoDtoList.push({
                size: bed,
                quantity: bedInfo[bed]
            })
        }

        console.log(bedInfoDtoList)
        payload["bedInfoDtoList"] = bedInfoDtoList;

        var facilityDisplayNameList = [];
        for (let facility in facilities){
            if (facilities[facility]){
                facilityDisplayNameList.push(facility)
            }
        }
        payload["facilityDisplayNameList"] = facilityDisplayNameList;
        var path = `/api/v1/hotel/hotel/${hotelId}/room`;
        console.log(payload)
        postWithJwt(path, payload)
            .then(response => response.json())
            .then(data => {
                // console.log(data);
                navigate(`/user/hotel/${hotelId}/rooms`)
            })
            .catch(e => console.log(e))
            .finally()
    }

    return (
        <div>
            <Navbar />
            <div className="form">
                <ProgressBar bgcolor="#febb02" page={page} numPages={FormTitles.length}/>
                <div className="form-container">
                    <div className="hotelRegisterHeader">
                        <h1>{FormTitles[page]}</h1>
                    </div>
                    <div className="body">
                        {
                            page === 0 &&
                            <RoomGeneralInfo
                                info={generalInfo}
                                setInfo={setGeneralInfo}
                                bedInfo={bedInfo}
                                setBedInfo={setBedInfo}
                                openWarnings={openWarnings}
                                setOpenWarnings={setOpenWarnings}
                            />
                        }
                        {
                            page === 1 &&
                            <RoomFacilities
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
                                        var numBed = 0;
                                        for (let key of Object.keys(bedsMap2)){
                                            numBed += parseInt(bedInfo[key])
                                        }
                                        var newOpenWarnings = {
                                            displayName: generalInfo.displayName.length < 5,
                                            shortNameEmpty: generalInfo.shortName.length === 0,
                                            shortNameNumber: /\d/.test(generalInfo.shortName),
                                            numBed: numBed === 0,
                                            priceMax: generalInfo.priceMin === "0.00",
                                            priceMin: generalInfo.priceMax === "0.00",
                                            timeOrder: generalInfo.checkOutTime > generalInfo.checkInTime,
                                            dateOrder: generalInfo.availableFrom > generalInfo.availableUntil
                                        }
                                        for (let key of Object.keys(newOpenWarnings)){
                                            if (newOpenWarnings[key]){
                                                setOpenWarnings(newOpenWarnings);
                                                return;
                                            }
                                        }
                                        setPage((page) => page + 1)
                                    }

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
            <MailList/>
            <Footer/>
        </div>
    );
}

export default RoomRegister;