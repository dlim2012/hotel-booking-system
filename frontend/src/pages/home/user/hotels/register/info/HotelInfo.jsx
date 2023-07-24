import React, {useState} from 'react';
import {propertyTypesMap, propertyRatings} from "../../../../../../assets/Lists";
import login from "../../../user/login/Login";

function HotelInfo(props) {

    const info = props.info;
    const setInfo = props.setInfo;
    const [checkInTime, setCheckInTime] = useState(
        {"hour": "6", "minute": "00", "period": "PM"}
    );
    const [checkOutTime, setCheckOutTime] = useState(
        {"hour": "11", "minute": "00", "period": "AM"}
    );
    if (info == null){
        return <div>...</div>
    }


    function onSave () {
        console.log(info)
    }

    return (
        <div>
             <div className="formItem">
                 <label className="formLabel">Name</label>
                 <input
                    value={info["name"]}
                    onChange={e => setInfo({...info, ["name"]: e.target.value})}
                 />
            </div>
            <div className="formItem">
                <label className="formLabel">Description</label>
                <input
                    value={info["description"]}
                    onChange={e => setInfo({...info, ["description"]: e.target.value})}/>
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
            <div className="formItem">
                <label className="formLabel">Phone</label>
                <input
                    value={info["phone"]}
                    onChange={e => setInfo({...info, ["phone"]: e.target.value})}/>
            </div>
            <div className="formItem">
                <label className="formLabel">Fax</label>
                <input
                    value={info["fax"]}
                    onChange={e => setInfo({...info, ["fax"]: e.target.value})}/>
            </div>
            <div className="formItem">
                <label className="formLabel">Website</label>
                <input
                    value={info["website"]}
                    onChange={e => setInfo({...info, ["website"]: e.target.value})}/>
            </div>
            <div className="formItem">
                <label className="formLabel">Email</label>
                <input
                    value={info["email"]}
                    onChange={e => setInfo({...info, ["email"]: e.target.value})}/>
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
            <button onClick={onSave}>Save</button>
        </div>
    );
}

export default HotelInfo;