import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../components/navbar/Navbar";
import {useLocation, useNavigate} from "react-router-dom";
import './hotelMain.css'
import {getWithJwt} from "../../../../../clients";



function HotelMain(props) {

    const location = useLocation();
    const navigate = useNavigate();
    const [info, setInfo] = useState({});

    const hotel = location.state.hotel;

    function navHotelInfo(){
        navigate(`/user/hotel/${hotel.id}/info`);
    }

    // function navRoomInfo(){
    //     navigate(`/user/hotel/${hotel.id}/info`);
    // }

    function navHotelBooking() {
        navigate(`/user/hotel/${hotel.id}/bookings`);
    }

    function navHotelDates() {
        navigate(`/user/hotel/${hotel.id}/dates`)
    }


    function fetchHotelMainInfo(){
        getWithJwt(`/api/v1/booking-management/hotel/${hotel.id}/main`)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setInfo(data);
            })
    }

    useEffect(() => {
        fetchHotelMainInfo()
    }, [])

    if (Object.keys(info).length === 0){
        return;
    }

    return (
        <div>
            <Navbar/>
            <div className="hotelMainContainer">
                <h1>Hotel Main Page
                    <button
                        onClick={() => {navigate(`/hotels/${hotel.id}`)}}
                    >View page</button></h1>
                <div className="hotelInfoContainer">
                    <span onClick={navHotelInfo}>Hotel info</span> <br/>
                    <span>Name: {hotel.name}</span> <br/>
                    <span>Address: {hotel.address}</span> <br/>
                    <span>#Room types: (5) Info</span> <br/>
                    <span>#rooms: (10)</span> <br/>
                </div>
                <div className="hotelInfoContainer">
                    <span
                        onClick={navHotelDates}
                    >Reservations</span> <br />
                    <span>------------------</span> <br/>
                    <span>Summary</span> <br/>
                    <span>#Available dates: {info.availableDates}</span> <br/>
                    <span>#Reservation: {info.numReserved}</span> <br/>
                    <span>#Reservation payment confirmed: {info.numBooked}</span> <br/>
                    <span>#Dates reserved: {info.numReservedDates}</span> <br/>
                    <span>#Dates payment confirmed: {info.numBookedDates}</span> <br/>
                    <span>------------------</span> <br/>

                    {/*{(info.numReservedOutOfRange > 0 || info.numBookedOutOfRange > 0 ) && */}
                        <div>
                        <span>Booking out of range (due to room info updates)</span> <br/>
                        <span>#Reservation: {info.numReservedOutOfRange}</span> <br/>
                        <span>#Reservation payment confirmed: {info.numBookedOutOfRange}</span> <br/>
                        <span>#Dates reserved: {info.numReservedDatesOutOfRange}</span> <br/>
                        <span>#Dates payment confirmed: {info.numBookedDatesOutOfRange}</span> <br/>
                    </div>
                    {/*// }*/}
                </div>
                <div className="hotelInfoContainer">
                    <span
                        onClick={navHotelBooking}
                    >Booking History</span><br />
                    <span>#Booking completed since {info.recordStartDate}: {info.recordNumBooking}</span> <br/>
                    <span>Total price last month: ${info.recordTotalPrice}</span> <br/>
                </div>

            </div>
        </div>
    );
}

export default HotelMain;