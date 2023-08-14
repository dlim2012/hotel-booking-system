import './reserveFailed.css'
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


function ReserveFailed(props) {

    const location = useLocation();
    const navigate = useNavigate();
    const { payload, hotelDetails } = location.state;

    console.log(payload.rooms)
    return (
        <div>
            <Navbar />
            <div className="reservedContainer">
                <h1>Reserve failed</h1>
                <button onClick={() => {navigate(`/hotels/${hotelDetails.id}`)}}>
                    Back to hotel
                </button>
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

export default ReserveFailed;