import React from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import RoomProfileSidebar from "../RoomProfileSidebar";

function RoomsBooking(props) {
    return (
        <div>
            <Navbar/>

            <div className="profileContainer">
                <RoomProfileSidebar />
            </div>
        </div>
    );
}

export default RoomsBooking;