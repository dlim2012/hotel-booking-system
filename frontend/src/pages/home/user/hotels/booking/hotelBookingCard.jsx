import './bookingCard.css';
import {useNavigate, useParams} from "react-router-dom";

function HotelBookingCard(props) {
    const navigate = useNavigate();
    const item = props.item;

    const { hotelId } = useParams();
    console.log(hotelId)

    function onClickDetails(item) {
        if (item.mainStatus === "RESERVED" || item.mainStatus === "BOOKED") {
            navigate(`/user/hotel/${hotelId}/bookings/active/${item.id}`, {state: {item: item, user_role: "hotel-manager"}});
        } else {
            navigate(`/user/hotel/${hotelId}/bookings/archived/${item.id}`, {state: {item: item, user_role: "hotel-manager"}})
        }
    }

    return (
        <div className="bookingCard">
            {item.status} <br/>
            <span>Dates: {item.startDateTime.substring(0, 10)} ~ {item.endDateTime.substring(0, 10)}</span> <br/>
            {/*<span>Location: {item.address}</span> <br/>*/}
            <span>Hotel: {item.hotelName}</span> <br/>
            <span>Number of rooms: {item.quantity}</span> <br />
            <span>price: {item.priceInCents}</span> <br/>
            <button onClick={() => {onClickDetails(item)}}>Details</button>
        </div>
    );
}

export default HotelBookingCard;