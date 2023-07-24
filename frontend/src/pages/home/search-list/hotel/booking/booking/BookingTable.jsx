import React from 'react';

function BookingCard(props) {
    const item = props.item;
    var address = ""
    if (item.neighborhood !== ""){
        address += item.neighborhood + ", ";
    }
    if (item.city !== ""){
        address += item.city + ", ";
    }
    if (item.state !== ""){
        address += item.state + ", ";
    }
    address += item.country;


    return (
        <div className={"bookingCard"}>
            {item.status} <br/>
            <span>Dates: {item.startDateTime} ~ {item.endDateTime}</span> <br/>
            <span>Address: {address}</span> <br/>
            <span>Hotel: {item.hotelName}</span> <br/>
            <span>Rooms</span> <br/>
            <button>Details</button>


        </div>
    );
}

export default BookingCard;