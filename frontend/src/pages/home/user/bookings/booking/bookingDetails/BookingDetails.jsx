import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate, useParams} from "react-router-dom";
import Navbar from "../../../../../../components/navbar/Navbar";
import {getWithJwt, postWithJwt, putWithJwt} from "../../../../../../clients";
import {getDate, getDateTime, getTime} from "../../../utils/stringFormatting";
import './bookingDetails.css'
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";

function getEstimatedHour(i){
    if (i === 0){
        return "12:00 AM - 1:00 AM";
    } else if (i <= 12){
        return i + ":00 AM - " + (i+1) + ":00 AM"
    } else if (i < 24) {
        return i + ":00 PM - " + (i+1) + ":00 PM"
    } else if (i === 24){
        return "12:00 AM - 1:00 AM (next day)"
    } else if (i === 25){
        return "1:00 AM - 2:00 AM (next day)"
    }
}

function BookingDetails(props) {
    /* BookingConfirmation details for active bookings*/
    const location = useLocation();
    const navigate = useNavigate();

    const { bookingId } = useParams();

    const [data, setData] = useState({});
    const [fetching, setFetching] = useState(false);
    const [edit, setEdit] = useState(""); // { "", "booker", "details" }
    const [edittedInfo, setEdittedInfo] = useState({})

    const {user_role} = location.state;

    function fetchBookingInfo(){
        setFetching(true);
        if (user_role === "app-user") {
            var pathname = `/api/v1/booking-management/user/booking/${bookingId}/active`
        } else if (user_role === "hotel-manager"){
            var pathname = `/api/v1/booking-management/hotel/booking/${bookingId}/active`
        }
        getWithJwt(pathname)
            .then(response=>response.json())
            .then(data => {
                console.log(data)
                setData(data);
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                setEdit("")
                setFetching(false)
            })
    }

    function payReservation(){
        putWithJwt(`/api/v1/booking/booking/${bookingId}/pay/user`)
            .then(response=>response.json())
            .then(data => {
                console.log(data)
                if (data.reserveSuccess && data.redirectUrl != null && data.redirectUrl.length > 0){
                    window.location.replace(data.redirectUrl);
                }
            })
            .catch(e => {
                console.log(e);
            })
        }

    function cancelBooking(){
        putWithJwt(`/api/v1/booking/booking/${bookingId}/cancel/user`)
            .catch(e => {
                console.log(e)})
            .finally(() => {
                navigate(`/user/bookings`);
            })
    }

    function cancelBookingRoom(bookingRoomId){
        putWithJwt(`/api/v1/booking/booking/${bookingId}/booking-room/${bookingRoomId}/cancel/user`)
            .then(response => response.json())
            .then(data => {
                if (data.bookingCancelled){
                    navigate(`/user/bookings`);
                }
            })
            .catch(e => {
                console.log(e)
            }).finally(() => {
                fetchBookingInfo();
            })
    }

    function onBookerInfoSubmit(){
        putWithJwt(`/api/v1/booking-management/booking/${bookingId}/active/booker`, edittedInfo)
            .catch(e => {
                console.error(e)})
            .finally(()=>{
                fetchBookingInfo()
            })
    }

    function onDetailsInfoSubmit(){
        putWithJwt(`/api/v1/booking-management/booking/${bookingId}/active/details`, edittedInfo)
            .catch(e => {
                console.error(e)})
            .finally(()=>{
                fetchBookingInfo()
            })
    }

    useEffect(() => {
        fetchBookingInfo()
    }, [])

    if (fetching){
        return <div><Navbar/></div>;
    }

    return (
        <div>
            <Navbar />
            <div className="bookingDetails">
                <div className="bookingDetailsCard">
                    <label
                        onClick={() => {navigate(`/hotels/${data.hotelId}`)}}
                    >Hotel</label> <br/>
                    <span>Name: {data.hotelName}</span><br/>
                    <span>Location: {data.address}</span><br/>
                    <span>Dates: {getDateTime(data.startDateTime)} ~ {getDateTime(data.endDateTime)}</span> <br />
                    <span>Status: {data.status}</span><br />
                </div>
                <div className="bookingDetailsCard">
                    <label>Price Info</label> <br/>
                    <span>Price: ${data.priceInCents / 100}</span> <br/>
                    { data.invoiceId != null && data.invoiceConfirmTime != null &&
                        <div className="bookingDetailsInvoice">
                            <span>Invoice Id: {data.invoiceId}</span> <br/>
                            <span>Invoice confirm time: {data.invoiceConfirmTime}</span> <br/>
                            {   data.freeCancellationUntil != null &&
                                <div>
                                    <span>Free Cancellation until {data.freeCancellationUntil}</span> <br/>
                            </div>
                            }
                        </div>
                    }
                    {
                        data.invoiceId == null &&
                        <div className="bookingDetailsInvoice">
                            {
                                data.prepayUntil != null &&
                                <div>
                                    <span>No prepayment until {data.prepayUntil}</span> <br/>
                                </div>
                            }
                            {
                                data.freeCancellationUntil != null &&
                                <div>
                                <span>Free Cancellation until {data.freeCancellationUntil}</span> <br/>
                                </div>
                            }
                            { user_role === "app-user" &&
                                <div>
                            <button
                                onClick={payReservation}
                            >Pay with Paypal</button><br/>
                                </div>
                            }
                        </div>
                    }
                </div>
                <div className="bookingDetailsCard">
                    <div className="bookingDetailsTitle">
                        <label>Booker Info</label>
                        <button
                            onClick={() => {
                                if (edit === "booker") {
                                    setEdit("")
                                } else {
                                    setEdit("booker")
                                    setEdittedInfo(
                                        {
                                            firstName: data.firstName,
                                            lastName: data.lastName,
                                            email: data.email
                                        }
                                    )
                                }
                            }}
                        >Edit</button>
                    </div>
                    { edit !== "booker" &&
                        <div>
                            <span>First Name: {data.firstName}</span> <br/>
                            <span>Last Name: {data.lastName}</span> <br/>
                            <span>Email: {data.email}</span> <br/>
                        </div>
                    }
                    { edit === "booker" &&
                        <div>
                            <label>First Name</label>
                            <input
                                type="text"
                                value={edittedInfo.firstName}
                                onChange={e => setEdittedInfo({...edittedInfo, ["firstName"]: e.target.value})}
                            /><br/>
                            <label
                            >Last Name</label>
                            <input
                                type="text"
                                value={edittedInfo.lastName}
                                onChange={e => setEdittedInfo({...edittedInfo, ["lastName"]: e.target.value})}
                            /><br/>
                            <label
                            >Email</label>
                            <input
                                type="text"
                                value={edittedInfo.email}
                                onChange={e => setEdittedInfo({...edittedInfo, ["email"]: e.target.value})}
                            /><br/>
                            <button
                                onClick={onBookerInfoSubmit}
                            >Submit</button>
                        </div>
                    }
                </div>
                {/*<span>Check-in time: {getTime(data.startDateTime)}</span> <br/>*/}
                {/*<span>Check-out time: {getTime(data.endDateTime)}</span> <br/>*/}

                <div className="bookingDetailsCard">
                    <label>Rooms</label>
                {
                    data.room?.map((room, index) => {
                        return <BookingDetailsGuestInfoCard
                            cancelBookingRoom = {cancelBookingRoom}
                            room={room} index={index}
                            bookingId={bookingId} fetchBookingInfo={fetchBookingInfo}
                            edit={edit} setEdit={setEdit}
                            edittedInfo={edittedInfo} setEdittedInfo={setEdittedInfo}
                        />;
                    })
                }
                </div>
                <div className="bookingDetailsCard">
                    <div className="bookingDetailsTitle">
                        <label>Details</label>
                        <button
                            onClick={() => {
                                if (edit === "details") {
                                    setEdit("");
                                } else {
                                    setEdit("details")
                                    setEdittedInfo({
                                        specialRequests: data.specialRequests,
                                        estimatedArrivalHour: data.estimatedArrivalHour
                                    })
                                }
                            }}
                        >Edit</button>
                    </div>
                    { edit !== "details" &&
                    <div>
                        <span>Special requests: {data.specialRequests}</span> <br/>
                        <span>Estimated arrival: {data.estimatedArrivalHour === -1 ? "Not selected" : getEstimatedHour(data.estimatedArrivalHour)}</span> <br/>
                    </div>
                    }
                    { edit === "details" &&
                        <div>
                            <label>Special requests</label>
                            <input
                                type="text"
                                value={edittedInfo.specialRequests}
                                onChange={e => {setEdittedInfo({...edittedInfo, ["specialRequests"]: e.target.value})}}
                            />
                            <label>Estimated arrival hour (local time)</label>
                            <select
                                value={edittedInfo.estimatedArrivalHour}
                                onChange={e=>setEdittedInfo({...edittedInfo, ["estimatedArrivalHour"]: e.target.value})}
                            >
                                <option value="-1">Please select</option>
                                <option value="0">12:00 AM - 1:00 AM</option>
                                <option value="1">1:00 AM - 2:00 AM</option>
                                <option value="2">2:00 AM - 3:00 AM</option>
                                <option value="3">3:00 AM - 4:00 AM</option>
                                <option value="4">4:00 AM - 5:00 AM</option>
                                <option value="5">5:00 AM - 6:00 AM</option>
                                <option value="6">6:00 AM - 7:00 AM</option>
                                <option value="7">7:00 AM - 8:00 AM</option>
                                <option value="8">8:00 AM - 9:00 AM</option>
                                <option value="9">9:00 AM - 10:00 AM</option>
                                <option value="10">10:00 AM - 11:00 AM</option>
                                <option value="11">11:00 AM - 12:00 PM</option>
                                <option value="12">12:00 PM - 1:00 PM</option>
                                <option value="13">1:00 PM - 2:00 PM</option>
                                <option value="14">2:00 PM - 3:00 PM</option>
                                <option value="15">3:00 PM - 4:00 PM</option>
                                <option value="16">4:00 PM - 5:00 PM</option>
                                <option value="17">5:00 AM - 6:00 PM</option>
                                <option value="18">6:00 PM - 7:00 PM</option>
                                <option value="19">7:00 PM - 8:00 PM</option>
                                <option value="20">8:00 PM - 9:00 PM</option>
                                <option value="21">9:00 PM - 10:00 PM</option>
                                <option value="22">10:00 PM - 11:00 PM</option>
                                <option value="23">11:00 PM - 12:00 AM</option>
                                <option value="24">12:00 AM - 1:00 AM (next day)</option>
                                <option value="25">1:00 AM - 2:00 AM (next day)</option>
                            </select>
                            <button
                                onClick={onDetailsInfoSubmit}
                            >Submit</button>
                        </div>
                    }
                </div>
                <div className="bookingDetailsCard">

                    <button
                        onClick={cancelBooking}
                    >Cancel</button>
                </div>
            </div>
            <MailList/>
            <Footer/>

        </div>
    );
}



function BookingDetailsGuestInfoCard(props){
    const { room, index, cancelBookingRoom, bookingId, fetchBookingInfo,
        edit, setEdit, edittedInfo, setEdittedInfo
    } = props;


    function onBookingRoomGuestInfoSubmit(){
        putWithJwt(`/api/v1/booking-management/booking/${bookingId}/active/booking-room/${room.bookingRoomId}/guest`,
            edittedInfo)
            .catch(e => {
                console.error(e)})
            .finally(() => {
                    fetchBookingInfo();
                }
            )
    }

    return (
        <div className="bookingRoomInfo">
            <span
                // onClick={onRoomClick}
            >{room.roomsDisplayName}</span> <br />
            <div className="bookingRoomInfoMid">
                <div className="bookingRoomInfoCard">
                    <span>Dates: {room.startDateTime} ~ {room.endDateTime}</span> <br/>
                    <span>Status: {room.status}</span> <br/>
                    {
                        // openRoom &&
                        <div>
                            {
                                room.prepayUntil != null &&
                                <div>
                                <span>No prepayment until {room.prepayUntil}</span> <br/>
                                </div>
                            }
                            {
                                room.freeCancellationUntil != null &&
                                <div>
                                    <span>Free Cancellation until {room.freeCancellationUntil}</span> <br/>
                                </div>
                            }
                            <span>Price: {room.priceInCents / 100}</span> <br/>
                        </div>
                    }
                </div>
                <div className="bookingRoomInfoCard">
                    <span>Guest Info</span>
                    <button
                    onClick={() => {
                        if (edit === index) {
                            setEdit("");
                        } else {
                            setEdit(index)
                            setEdittedInfo({
                                guestName: room.guestName,
                                guestEmail: room.guestEmail
                            })
                        }
                    }}
                >Edit</button><br/>
                    { edit !== index &&
                        <div>
                            <span>Guest full name: {room.guestName}</span><br/>
                            <span>Guest email: {room.guestEmail}</span><br/>
                        </div>
                    }
                    { edit === index &&
                        <div>
                            <label>
                                Guest full name:
                            </label>
                            <input
                                type="text"
                                value={edittedInfo.guestName}
                                onChange={e => {setEdittedInfo({...edittedInfo, ["guestName"]: e.target.value})}}
                            />
                            <label>
                                Guest email:
                            </label>
                            <input
                                type="text"
                                value={edittedInfo.guestEmail}
                                onChange={e => {setEdittedInfo({...edittedInfo, ["guestEmail"]: e.target.value})}}
                            />
                            <button
                                onClick={onBookingRoomGuestInfoSubmit}
                            >Submit</button>
                        </div>
                    }
                </div>
            </div>
            <button
                onClick={() => cancelBookingRoom(room.bookingRoomId)}
            >Cancel</button>
        </div>
    );
}

export default BookingDetails;
