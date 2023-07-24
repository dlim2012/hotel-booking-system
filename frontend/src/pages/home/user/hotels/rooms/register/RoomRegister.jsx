import React, {useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import ProgressBar from "../../register/ProgressBar";
import RoomGeneralInfo from "./info/RoomInfo";
import RoomFacilities from "./facilities/RoomFacilities";
import {postWithJwt} from "../../../../../../clients";
import {bedsMap, roomFacilities} from "../../../../../../assets/Lists";
import {useLocation, useNavigate, useParams} from "react-router-dom";

function RoomRegister(props) {
    var today = new Date();
    var year = today.toLocaleString("default", {year: "numeric"})
    var month = today.toLocaleString("default", {month: "2-digit"})
    var day = today.toLocaleString("default", {day: "2-digit"})
    var formattedDate = year + "-" + month + "-" + day;

    const { hotelId } = useParams()

    var bedInfoDtoList = {};
    for (let bed of bedsMap){
        bedInfoDtoList[bed] = 0;
    }

    const location = useLocation();
    const navigate= useNavigate();
    const [page, setPage] = useState(0);
    const FormTitles = ["Room Info", "Facilities"]
    const [generalInfo, setGeneralInfo] = useState({
        displayName: "", shortName: "", description: "",
        maxAdult: "0", maxChild: "0",
        quantity: "0", priceMax: "0.00", priceMin: "0.00",
        checkOutTime: "11:00", checkInTime: "18:00",
        isActive: false, freeCancellationDays: "0", noPrepaymentDays: "0", paymentOption: "PREPAYMENT",
        availableFrom: formattedDate, availableUntil: "2099-12-31"
    });
    const [bedInfo, setBedInfo] = useState({});
    const [facilities, setFacilities] = useState(
        Object.fromEntries(roomFacilities.map(i => [i, false]))
    );

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
        for (let bed in Object.keys(bedInfo)){
            bedInfoDtoList.push({
                size: bed,
                quantity: bedInfo[bed]
            })
        }
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
                    <div className="header">
                        <h1>{FormTitles[page]}</h1>
                    </div>
                    <div className="body">
                        {page === 0 && <RoomGeneralInfo info={generalInfo} setInfo={setGeneralInfo} bedInfo={bedInfo} setBedInfo={setBedInfo} />}
                        {page === 1 && <RoomFacilities info={facilities} setInfo={setFacilities} />}
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

export default RoomRegister;