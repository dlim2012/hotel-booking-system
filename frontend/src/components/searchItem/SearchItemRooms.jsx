import React from 'react';
import getBedText from "../../functions/bedText";
import './searchItemRooms.css'

function SearchItemRooms(props) {
    const roomsInfo = props.roomsInfo;
    if (roomsInfo == null || roomsInfo.recommended === 0){
        return;
    }

    var bedInfo = getBedText(roomsInfo.bedInfoList, roomsInfo.numBed)
    // for (var i=0; i<bedInfoList.length; i++){
    //     bedInfo = bedInfo + bedInfoList[i].quantity + " " + bedNameMap[bedInfoList[i].size]
    //     if (i !== bedInfoList.size -1 ){
    //         bedInfo += ", "
    //     }
    // }

    return (
        <div>
            <div className="searchItemRoomsContainer">

                <div className="searchItemRoomsSidebar">
                    <div className="searchItemRoomsQuantity">
                        {roomsInfo.recommended} ×
                    </div>
                </div>
                <div className="searchItemRoomsDetails">
                    <h5>{roomsInfo.displayName}</h5>
                    <span className="bedInfo">{bedInfo}</span> <br/>
                    <span className="cancelOp">Free cancellation</span>
                </div>
            </div>
        </div>
    );
}

export default SearchItemRooms;