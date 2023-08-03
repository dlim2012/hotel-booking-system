import './reserved.css'
import Navbar from "../../../../../../../components/navbar/Navbar";
import {useLocation, useNavigate} from "react-router-dom";
import getDateTextShort from "../../../../../../../functions/dateTextShort";
import MailList from "../../../../../../../components/mailList/MailList";
import Footer from "../../../../../../../components/footer/Footer";

const getTimeText = (timeInteger) => {
    var hour = Math.floor(timeInteger / 60);
    var minute = timeInteger - hour * 60;
    minute = minute < 10 ? "0" + minute.toString() : minute.toString()
    if (hour >= 12){
        return `${hour - 12}:${minute} PM`
    } else {
        return `${hour}:${minute} AM`
    }
}


function Reserved(props) {

    const location = useLocation();
    const navigate = useNavigate();
    const { payload, hotelDetails } = location.state;

    console.log(payload.rooms)
    return (
        <div>
            <Navbar />
            <div className="reservedContainer">
                <h1>Reserved</h1>
                <div className="BookingItemInfo">
                    <span>Hotel: {payload.hotelName}</span> <br/>
                    <span>Address: {hotelDetails.address}</span> <br/>
                    <span>Check-in time: {getDateTextShort(payload.startDate)} {getTimeText(payload.checkInTime)}</span> <br/>
                    <span>Check-out time: {getDateTextShort(payload.endDate)} {getTimeText(payload.checkOutTime)}</span> <br/>
                    <span>Rooms</span>
                    {/*<ul className="roomList">*/}
                    {payload.rooms.map((room, index) => {
                        return <li>{room.roomsName}</li>
                    })}
                    {/*</ul>*/}
                </div>
                {/*<button>Retry Payment</button> <br />*/}
                <button
                    onClick={() => {navigate('/')}}
                >Home page</button>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default Reserved;