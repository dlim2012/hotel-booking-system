import React from 'react';
import {Link, useNavigate, useParams} from "react-router-dom";

function HotelProfileSidebar(props) {
    const navigate = useNavigate();
    const { hotelId } = useParams();

    // const navMain = () => {
    //
    // }
    //
    // const navBooking = () => {
    //     navigate(`/user/hotel/${hotelId}/bookings`)
    // }

    const navInfo = () => {
        navigate(`/user/hotel/${hotelId}/info`)
    }

    const navRooms = () => {
        navigate(`/user/hotel/${hotelId}/rooms`)
    }

    const navFacilities = () => {
        navigate(`/user/hotel/${hotelId}/facilities`)
    }

    const navAddress = () => {
        navigate(`/user/hotel/${hotelId}/address`)
    }

    const navImages = () => {
        navigate(`/user/hotel/${hotelId}/image`)
    }

    return (
        <div className="profileSidebar">
            <ul className="profileSidebarList">
                {/*<li*/}
                {/*    className="profileSidebarItem"*/}
                {/*    onClick={navMain}*/}
                {/*>*/}
                {/*    Main*/}
                {/*</li>*/}
                {/*<li*/}
                {/*    className="profileSidebarItem"*/}
                {/*    onClick={navBooking}*/}
                {/*>*/}
                {/*    BookingConfirmation*/}
                {/*</li>*/}
                <li
                    className="profileSidebarItem"
                    onClick={navInfo}
                >
                    Information
                </li>
                <li
                    className="profileSidebarItem"
                    onClick={navRooms}
                >
                    Rooms
                </li>
                <li
                    className="profileSidebarItem"
                    onClick={navFacilities}
                >
                    Facilities
                </li>
                <li
                    className="profileSidebarItem"
                    onClick= {navImages}
                >
                    Images
                </li>
                <li
                    className="profileSidebarItem"
                    onClick= {navAddress}
                >
                    Address
                </li>
            </ul>
        </div>
    );
}

export default HotelProfileSidebar;