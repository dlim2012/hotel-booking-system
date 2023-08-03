import React, {useState} from 'react';
import {propertyTypesMap, propertyRatings} from "../../../../../../assets/Lists";
import login from "../../../user/login/Login";
import './hotelInfo.css'

function HotelInfo(props) {

    const { info, setInfo, openWarnings, setOpenWarnings } = props;

    return (
        <div className="hotelInfoContainer">
            <div className="formItems">
                <div className="formItem">
                    <label className="formLabel">Name</label>

                    <input
                        type="text"
                        value={info["name"]}
                        onChange={e => setInfo({...info, ["name"]: e.target.value})}
                    />
                    { openWarnings.phone &&
                        <div className="hotelLocationInputWarning">
                            Please enter the property name. (min length: 3)
                        </div>
                    }
                </div>
                <div className="formItem">
                    <label className="formLabel">Property type</label>
                    <select
                        value={info["propertyType"]}
                        onChange={e => setInfo({...info, ["propertyType"]: e.target.value})}
                    >
                        {
                            Object.keys(propertyTypesMap).map((key, index) => <option value={key}>{key}</option>)
                        }
                    </select>
                </div>

            </div>
            <div className="formItem">
                <label className="formLabel">Description</label>
                <textarea
                    // height="200px"
                    placeholder={"Add descriptions to introduce your hotel!"}
                    value={info["description"]}
                    onChange={e => setInfo({...info, ["description"]: e.target.value})}
                />
            </div>
            <div className="formItems">
                <div className="formItem">
                    <label className="formLabel">Phone</label>
                    <input
                        type="text"
                        value={info["phone"]}
                        onChange={e => {
                            setOpenWarnings({...openWarnings, phone: false})
                            setInfo({...info, ["phone"]: e.target.value})
                        }}/>
                    { openWarnings.phone &&
                        <div className="hotelLocationInputWarning">
                            Please enter a phone number.
                        </div>
                    }
                </div>
                <div className="formItem">
                    <label className="formLabel">Fax</label>
                    <input
                        type="text"
                        value={info["fax"]}
                        onChange={e => setInfo({...info, ["fax"]: e.target.value})}/>
                </div>
            </div>
            <div className="formItems">
                <div className="formItem">
                    <label className="formLabel">Website</label>
                    <input
                        type="text"
                        value={info["website"]}
                        onChange={e => setInfo({...info, ["website"]: e.target.value})}/>
                </div>
                <div className="formItem">
                    <label className="formLabel">Email</label>
                    <input
                        type="text"
                        value={info["email"]}
                        onChange={e => {
                            setOpenWarnings({...openWarnings, phone: false})
                            setInfo({...info, ["email"]: e.target.value})
                        }}/>
                    { openWarnings.email &&
                    <div className="hotelLocationInputWarning">
                        The email is invalid.
                    </div>
                    }
                </div>
            </div>
            <div className="formItem">
                <label className="formLabel">Property Rating</label>
                <select
                    value={info["propertyRating"]}
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
        </div>
    );
}

export default HotelInfo;