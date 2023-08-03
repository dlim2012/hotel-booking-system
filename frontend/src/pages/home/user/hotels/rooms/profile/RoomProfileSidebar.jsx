import React from 'react';
import {useNavigate, useParams} from "react-router-dom";

function RoomProfileSidebar(props) {
    const navigate = useNavigate();
    const { hotelId, roomsId } = useParams();

    const navBooking = () => {

    }

    const navDates = () => {
        navigate(`/user/hotel/${hotelId}/rooms/${roomsId}/dates`)
    }

    const navInfo = () => {
        navigate(`/user/hotel/${hotelId}/rooms/${roomsId}/info`)
    }

    const navFacilities = () => {
        navigate(`/user/hotel/${hotelId}/rooms/${roomsId}/facilities`)
    }

    const navSettings = () => {
        navigate(`/user/hotel/${hotelId}/rooms/${roomsId}/settings`)
    }

    return (
        <div className="profileSidebar">
            <ul className="profileSidebarList">
                {/*<li*/}
                {/*    className="profileSidebarItem"*/}
                {/*    onClick={navBooking}*/}
                {/*>*/}
                {/*    Booking*/}
                {/*</li>*/}
                {/*<li*/}
                {/*    className="profileSidebarItem"*/}
                {/*    onClick={navDates}*/}
                {/*>*/}
                {/*    Dates*/}
                {/*</li>*/}
                <li
                    className="profileSidebarItem"
                    onClick={navInfo}
                >
                    Information
                </li>
                <li
                    className="profileSidebarItem"
                    onClick={navFacilities}
                >
                    Facilities
                </li>
                <li
                    className="profileSidebarItem"
                    onClick={navSettings}
                >
                    Settings
                </li>
            </ul>
        </div>
    );
}

export default RoomProfileSidebar;