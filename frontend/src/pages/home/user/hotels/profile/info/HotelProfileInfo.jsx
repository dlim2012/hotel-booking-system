import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import HotelProfileSidebar from "../HotelProfileSidebar";
import HotelInfo from "../../register/info/HotelInfo";
import {getWithJwt, putWithJwt} from "../../../../../../clients";
import {useParams} from "react-router-dom";
import {propertyRatings, propertyTypesMap} from "../../../../../../assets/Lists";
import './hotelProfileInfo.css'

function HotelProfileInfo(props) {
    const [info, setInfo] = useState({});
    const { hotelId } = useParams()

    function fetchGeneralInfo(){
        console.log("fetching...")
        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/info`)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setInfo(data)
            })
            .catch(e => {
                console.error(e)
            })
    }

    function onSave () {
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/info`, info);
    }

    useEffect(() => {
        fetchGeneralInfo();
    }, [])

    if (Object.keys(info).length === 0){
        return;
    }

    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <HotelProfileSidebar />
                <div className="profileContents">
                    <div className="profileTitle">
                        <h1>General Information</h1>
                    </div>
                    <div>
                        <div className="formItem">
                            <label className="formLabel">Name</label>
                            <input
                                value={info?.name}
                                onChange={e => setInfo({...info, ["name"]: e.target.value})}
                            />
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Description</label>
                            <input
                                value={info?.description}
                                onChange={e => setInfo({...info, ["description"]: e.target.value})}/>
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Property type</label>
                            <select
                                value={info?.propertyType}
                                onChange={e => setInfo({...info, ["propertyType"]: e.target.value})}
                            >
                                {
                                    Object.keys(propertyTypesMap).map((key, index) => <option value={key}>{key}</option>)
                                }
                            </select>
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Phone</label>
                            <input
                                value={info?.phone}
                                onChange={e => setInfo({...info, ["phone"]: e.target.value})}/>
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Fax</label>
                            <input
                                value={info?.fax}
                                onChange={e => setInfo({...info, ["fax"]: e.target.value})}/>
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Website</label>
                            <input
                                value={info?.website}
                                onChange={e => setInfo({...info, ["website"]: e.target.value})}/>
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Email</label>
                            <input
                                value={info?.email}
                                onChange={e => setInfo({...info, ["email"]: e.target.value})}/>
                        </div>
                        <div className="formItem">
                            <label className="formLabel">Property Rating</label>
                            <select
                                value={info?.propertyRating}
                                onChange={
                                    e => setInfo({...info, ["propertyRating"]: e.target.selectedIndex})

                                }
                                onSelect={e => {
                                    console.log(e)
                                    // onChange("propertyRating", e.target)
                                }}
                            >
                                {
                                    propertyRatings.map(item => <option value={item.value.toString()}>{item.label}</option>)
                                }
                            </select>
                        </div>
                        <button onClick={onSave}>Save</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default HotelProfileInfo;

