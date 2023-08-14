import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {postWithJwt, putWithJwt} from "../../../../../../clients";
import {getDateTime} from "../../../utils/stringFormatting";
import './bookingArchived.css'
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";
import {TailSpin} from "react-loader-spinner";

function BookingArchived(props) {

    const location = useLocation();
    const navigate = useNavigate();
    const { bookingId, hotelId } = useParams();
    const { item } = location.state;
    const [fetching, setFetching] = useState(false);
    const [data, setData] = useState({});

    const {user_role} = location.state;

    function fetchArchived(){
        setFetching(true)
        var payload = {
            bookingMainStatus: item.mainStatus,
            endDate: item.endDate
        }
        if (user_role === "app-user") {
            var pathname = `/api/v1/booking-management/user/booking/${bookingId}/archived`
        } else if (user_role === "hotel-manager"){
            // const {hotelId} = useParams();
            var pathname = `/api/v1/booking-management/hotel/${hotelId}/booking/${bookingId}/archived`
        }
        postWithJwt(pathname, payload)
            .then(response => response.json())
            .then(data => {
                setData(data)
            })
            .catch(e => {
                console.error(e)
            })
            .finally(() => {
                setFetching(false);
            })
    }

    useEffect(() => {
        fetchArchived();
    }, [])


    if (fetching){
        return (
            <div>
                <Navbar />
                <div className="loading">
                    <TailSpin
                        height="80"
                        width="80"
                        color="#0071c2"
                        ariaLabel="tail-spin-loading"
                        radius="1"
                        wrapperStyle={{}}
                        wrapperClass=""
                        visible={true}
                    />
                </div>
            </div>
        )
    }

    return (
        <div>
            <Navbar/>
            <div className="bookingArchived">
                <div className="bookingArchivedCard">
                    <label
                        onClick={() => {navigate(`/hotels/${data.hotelId}`)}}
                    >Hotel</label> <br/>
                    <span>Name: {data.hotelName}</span><br/>
                    <span>Location: {data.address}</span><br/>
                    <span>Dates: {getDateTime(data.startDateTime)} ~ {getDateTime(data.endDateTime)}</span> <br />
                    <span>Status: {data.status}</span><br />
                </div>
                <div className="bookingArchivedCard">
                    <label>Price Info</label> <br/>
                    <span>Price: ${data.priceInCents / 100}</span> <br/>
                    { data.invoiceId != null && data.invoiceConfirmTime != null &&
                        <div className="bookingArchivedInvoice">
                            <span>Invoice Id: {data.invoiceId}</span> <br/>
                            <span>Invoice confirm time: {data.invoiceConfirmTime}</span> <br/>
                            <span>Free Cancellation until {data.freeCancellationUntil}</span> <br/>
                        </div>
                    }
                </div>
                <div className="bookingArchivedCard">
                    <div className="bookingArchivedTitle">
                        <label>Booker Info</label>
                    </div>
                    <div>
                        <span>First Name: {data.firstName}</span> <br/>
                        <span>Last Name: {data.lastName}</span> <br/>
                        <span>Email: {data.email}</span> <br/>
                    </div>
                </div>
                {/*<span>Check-in time: {getTime(data.startDateTime)}</span> <br/>*/}
                {/*<span>Check-out time: {getTime(data.endDateTime)}</span> <br/>*/}

                <div className="bookingArchivedCard">
                    <label>Rooms</label>
                    {
                        data.rooms?.map((room, index) => {
                            return <BookingArchivedGuestInfoCard
                                room={room} index={index}
                                bookingId={bookingId}
                            />;
                        })
                    }
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}


function BookingArchivedGuestInfoCard(props){
    const { room, index, bookingId} = props;
    console.log(room)
    return (
        <div className="bookingRoomInfo">
            <span
                // onClick={onRoomClick}
            >{room.roomsName}</span> <br />
            <span>Dates: {room.startDateTime} ~ {room.endDateTime}</span> <br/>
            <span>Guest name: {room.guestName}</span> <br/>
            <span>Guest email: {room.guestEmail}</span> <br/>
        </div>
    );
}

export default BookingArchived;