import React from 'react';
import {hotelFacilities, roomFacilities} from "../../../../../../assets/Lists";
import {getWithJwt, postWithJwt} from "../../../../../../clients";
import {useParams} from "react-router-dom";

function HotelFacilities(props) {
    const facilities = props.info;
    const setFacilities = props.setInfo
    const { hotelId } = useParams()

    if (Object.keys(facilities).length === 0){
        return;
    }


    return (
        <div>
            <div className="facility-list">
                {hotelFacilities.map((item, index) => (
                    <div key={index}>
                        <input
                            type="checkbox"
                            checked={facilities[item]}
                            onClick={e => {
                                setFacilities({...facilities, [item]: e.target.checked});
                            }}/>
                        <span>{item}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default HotelFacilities;