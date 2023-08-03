import React from 'react';
import {roomFacilities} from "../../../../../../../assets/Lists";
import './roomFacilities.css'

function RoomFacilities(props) {
    return (
        <div>
            <div className="roomFacilityList">
                {roomFacilities.map((item, index) => (
                    <div
                        className="roomFacility"
                        key={index}>
                        <input
                            type="checkbox"
                            checked={props.info[item]}
                            onChange={e => {
                                props.setInfo({...props.info, [item]: e.target.checked});
                            }}/>
                        <span>{item}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default RoomFacilities;