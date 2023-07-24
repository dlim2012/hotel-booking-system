import React from 'react';
import {roomFacilities} from "../../../../../../../assets/Lists";

function RoomFacilities(props) {
    return (
        <div>
            <div className="facility-list">
                {roomFacilities.map((item, index) => (
                    <div key={index}>
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