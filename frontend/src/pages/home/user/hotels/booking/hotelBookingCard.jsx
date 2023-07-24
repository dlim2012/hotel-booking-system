import './bookingCard.css';
import {useNavigate} from "react-router-dom";

function HotelBookingCard(props) {
    const navigate = useNavigate();
    const item = props.item;

    function onClickDetails(item) {
        if (item.mainStatus === "RESERVED" || item.mainStatus === "BOOKED") {
            navigate(`/user/bookings/${item.id}`, {state: {item: item}});
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