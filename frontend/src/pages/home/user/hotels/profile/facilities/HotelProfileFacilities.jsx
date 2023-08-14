import './hotelProfileFacilities.css'
import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import HotelProfileSidebar from "../HotelProfileSidebar";
import {hotelFacilities} from "../../../../../../assets/Lists";
import {getWithJwt, postWithJwt, putWithJwt} from "../../../../../../clients";
import {useParams} from "react-router-dom";
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";
import {TailSpin} from "react-loader-spinner";

function HotelProfileFacilities(props) {
    const [info, setInfo] = useState({});
    const { hotelId } = useParams()
    const [saved, setSaved] = useState(false);
    const [fetching, setFetching] = useState(false);

    function fetchHotelFacilities(){
        setFetching(true);
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
        console.log(payload)
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/facility`, payload)
            .then(() => {
                setSaved(true)
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                fetchHotelFacilities();
            })
    }

    useEffect(() => {
        fetchHotelFacilities()
    }, [])


    if (fetching){
        return (
            <div>
                <Navbar />
                <div className="profileContainer">
                    <HotelProfileSidebar />
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
                                            setSaved(false);
                                            setInfo({...info, [item]: e.target.checked});
                                        }}/>
                                    <label>{item}</label>
                                </div>
                            ))}
                        </div>
                    </div>
                    <button onClick={onSave}>Save</button>
                    {saved && <p className="hotelProfileFacilitiesSaved">Saved!</p>}
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}



export default HotelProfileFacilities;