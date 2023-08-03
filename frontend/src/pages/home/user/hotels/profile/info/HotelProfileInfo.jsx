import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import HotelProfileSidebar from "../HotelProfileSidebar";
import HotelInfo from "../../register/info/HotelInfo";
import {getWithJwt, putWithJwt} from "../../../../../../clients";
import {useParams} from "react-router-dom";
import {propertyRatings, propertyTypesMap} from "../../../../../../assets/Lists";
import './hotelProfileInfo.css'
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";
import {validateEmail} from "../../../utils/inputValidation";


function HotelProfileInfo(props) {
    const [info, setInfo] = useState({});
    const { hotelId } = useParams()
    const [saved, setSaved] = useState(false);

    var defaultOpenWarnings = {
        noName: false,
        invalidEmail: false
    };

    const [ openWarnings, setOpenWarnings ] = useState(defaultOpenWarnings)

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
        var invalid = false;
        var newOpenWarnings = {...openWarnings}
        if (info.name.length < 3){
            newOpenWarnings.noName = true;
            invalid = true;
        }
        if (!validateEmail(info.email)){
            newOpenWarnings.invalidEmail = true;
            invalid = true;
        }
        setOpenWarnings(newOpenWarnings)
        if (invalid){
            return;
        }

        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/info`, info)
            .then(() => {
                alert("Hotel information saved!")
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                fetchGeneralInfo();
                setOpenWarnings(defaultOpenWarnings)
                setSaved(true)
            })
    }

    useEffect(() => {
        fetchGeneralInfo();
        setOpenWarnings(defaultOpenWarnings)
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
                        <div className="profileFormItem">
                            <label className="formLabel">Name</label>
                            <input
                                value={info?.name}
                                onChange={e => setInfo({...info, ["name"]: e.target.value})}
                                maxLength="20"
                            />
                            {
                                openWarnings.noName &&
                                <span className="profileContentsWarning">
                                    Please enter a hotel name. (min length: 3)
                                </span>
                            }
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Description</label>
                            <input
                                value={info?.description}
                                onChange={e => setInfo({...info, ["description"]: e.target.value})}/>
                        </div>
                        <div className="profileFormItem">
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
                        <div className="profileFormItem">
                            <label className="formLabel">Phone</label>
                            <input
                                value={info?.phone}
                                onChange={e => setInfo({...info, ["phone"]: e.target.value})}/>
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Fax</label>
                            <input
                                value={info?.fax}
                                onChange={e => setInfo({...info, ["fax"]: e.target.value})}/>
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Website</label>
                            <input
                                value={info?.website}
                                onChange={e => setInfo({...info, ["website"]: e.target.value})}/>
                        </div>
                        <div className="profileFormItem">
                            <label className="formLabel">Email</label>
                            <input
                                value={info?.email}
                                onChange={e => setInfo({...info, ["email"]: e.target.value})}/>
                            { openWarnings.invalidEmail &&
                                <span className="profileContentsWarning">
                                    The email is invalid. (min length: 3)
                                </span>}
                        </div>
                        <div className="profileFormItem">
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
            <MailList/>
            <Footer/>
        </div>
    );
}

export default HotelProfileInfo;

