import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../components/navbar/Navbar";
import {postWithJwt} from "../../../../../clients";
import {statusMap} from "../../../../../assets/Lists";
import './userBooking.css';
import UserBookingCard from "./BookingCard";


function UserBookings(props) {
    const [status, setStatus] = useState("Upcoming");
    const [bookings, setBookings] = useState([]);


    const fetchBookings = (status) => {
        var startDate = new Date();
        var endDate = new Date();
        startDate.setDate( startDate.getDate() - 100)
        endDate.setDate(endDate.getDate() + 2)

        // startDate inclusive, endDate exclusive
        var payload = {
            status: statusMap[status],
            startDate: startDate,
            endDate: null
        }
        console.log(payload)

        postWithJwt("/api/v1/booking-management/user/booking", payload)
            .then(response => response.json())
            .then(data => {
                for (var item of data){
                    item.startDate = new Date(item.startDateTime)
                    item.endDate = new Date(item.endDateTime)
                }
                data.sort(function(x, y){
                    return x.endDate < y.endDate ? 1 : -1;
                })
                console.log(data)
                setBookings(data);
            })
            .catch(e => {
                console.error(e)
            })
    }

    const onStatusButtonClick = (newStatus) => {
        setStatus(newStatus);
        fetchBookings(newStatus)
        // fetchBookings();
        // window.location.reload();
    }

    useEffect(() => {
        fetchBookings(status);
    }, [])



    return (
        <div>
            <Navbar />
            <div className="bookingsContainer">
                <h1>Booking History</h1>
                <div className="bookingsListNavBar">
                    <button
                        onClick={() => onStatusButtonClick("Upcoming")}
                    >Upcoming</button>
                    <button
                        onClick={() => onStatusButtonClick("Completed")}
                    >Completed</button>
                    <button
                        onClick={() => onStatusButtonClick("Cancelled")}
                    >Cancelled</button>
                </div>
                <div className="bookingsSearchContainer">
                    <span>(14) bookings placed in</span>
                    <select>
                        <option>past 30 days</option>
                        <option>past 3 months</option>
                        <option>2023</option>
                        <option>Archived Orders</option>
                    </select>
                    {/*<span>Status: </span>*/}
                    {/*<label>Reserved</label>*/}
                    {/*<input type={"checkbox"} defaultChecked={true}/>*/}
                    {/*<label>Booked</label>*/}
                    {/*<input type={"checkbox"} defaultChecked={true}/>*/}
                    {/*<label>Cancelled</label>*/}
                    {/*<input type={"checkbox"} defaultChecked={true}/>*/}
                    {/*<label>Completed</label>*/}
                    {/*<input type={"checkbox"} defaultChecked={true}/>*/}
                </div>
                <div className="bookingsListContainer">
                    {
                        bookings.map((item, index) => {
                            return <UserBookingCard item={item} />
                        })
                    }
                </div>
            </div>

        </div>
    );
}

export default UserBookings;