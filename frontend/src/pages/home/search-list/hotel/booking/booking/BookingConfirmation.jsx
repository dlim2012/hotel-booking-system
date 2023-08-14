import './bookingConfirmation.css'
import Navbar from "../../../../../../components/navbar/Navbar";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import hotel from "../../Hotel";
import getBedText from "../../../../../../functions/bedText";
import getDateTextShort, {getDateTextShort2} from "../../../../../../functions/dateTextShort";
import getDateText from "../../../../../../functions/dateTextLong";
import {useEffect, useState} from "react";
import {gu} from "date-fns/locale";
import {postWithJwt} from "../../../../../../clients";
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";
import ScrollToTop from "../../../../../../components/scrollToTop/scrollToTop";
import {TailSpin} from "react-loader-spinner";

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

function getDaysBefore(date, days){
    var newDate = new Date(date.getTime());
    newDate.setDate(newDate.getDate() - days);
    return newDate;
}

function BookingConfirmation(props) {
    const location = useLocation();
    const navigate = useNavigate();

    let { hotelId } = useParams();

    const searchItem = location.state.searchItem;
    const bookingInfo = location.state.bookingInfo;
    const hotelDetails = bookingInfo.hotelDetails;
    const rooms = bookingInfo.rooms;

    const [bookingResult, setBookingResult] = useState("");

    console.log(rooms)

    var defaultGuestInfo = []
    for (let i=0; i<rooms.length; i++){
        defaultGuestInfo.push({
            "guestName": "guestName",
            "guestEmail": "guestEmail"
        })
    }

    const [bookPersonalInfo, setbookPersonalInfo] = useState({
        "firstName": "firstName",
        "lastName": "lastName",
        "emailAddress": "emailAddress",
    });
    const [guestInfo, setGuestInfo] = useState(defaultGuestInfo);
    const [specialRequests, setSpecialRequests] = useState("specialRequest");
    const [arrivalTime, setArrivalTime] = useState("-1");
    const [ proceeding, setProceeding ] = useState(false);

    var dates = Math.round((searchItem.date[0].endDate.getTime() - searchItem.date[0].startDate.getTime()) / 86400000);

    function getPayload(){
        var payloadRooms = []
        for (let i=0; i< rooms.length; i++){
            if (rooms[i].noPrepayUntil === "None"){
                rooms[i].noPrepayUntil = searchItem.date[0]
            }
            payloadRooms.push({
                roomsId: rooms[i].id,
                roomsName: rooms[i].displayName,
                guestName: guestInfo[i].guestName,
                guestEmail: guestInfo[i].guestEmail,
                noPrepayUntil: rooms[i].noPrepayUntil,
                freeCancellationUntil: rooms[i].freeCancellationUntil
            })
        }

        var payload = {
            firstName: bookPersonalInfo.firstName,
            lastName: bookPersonalInfo.lastName,
            email: bookPersonalInfo.emailAddress,
            hotelName: hotelDetails.name,
            neighborhood: hotelDetails.neighborhood,
            city: hotelDetails.city,
            state: hotelDetails.state,
            country: hotelDetails.country,
            startDate: searchItem.date[0].startDate,
            endDate: searchItem.date[0].endDate,
            checkInTime: bookingInfo.maxCheckInTime,
            checkOutTime: bookingInfo.minCheckOutTime,
            specialRequests: specialRequests,
            estimatedArrivalHour: arrivalTime,
            priceInCents: bookingInfo.totalPrice,
            rooms: payloadRooms
        }
        console.log(payload)
        return payload
    }

    function onReserve() {
        setProceeding(true)
        var payload = getPayload();
        postWithJwt(`/api/v1/booking/hotel/${hotelId}/reserve`, payload)
            .then(response=>response.json())
            .then(data => {
                if (data.success) {
                    navigate(`/hotels/booking/reserved/${data.bookingId}`, {
                        state: {
                            payload: payload,
                            hotelDetails: hotelDetails
                        }
                    })
                } else {
                    navigate(`/hotels/booking/reserve-failed`, {
                        state: {
                            payload: payload,
                            hotelDetails: hotelDetails
                        }
                    })
                }
            })
            .catch(e => {
                console.log(e);
            })
            .finally(() => {
                setProceeding(false)
            })
    }

    function onBook() {
        setProceeding(true);
        var payload = getPayload();
        postWithJwt(`/api/v1/booking/hotel/${hotelId}/book`, payload)
            .then(response=>response.json())
            .then(data => {
                console.log(data)
                if (data.reserveSuccess && data.redirectUrl != null && data.redirectUrl.length > 0){
                    console.log(data.redirectUrl)
                    window.location.replace(data.redirectUrl);
                } else {
                    if (data.reserveSuccess){
                        alert("PayPal Sandbox did not respond for some reason. Room reserved.")
                        navigate(`/hotels/booking/reserved/${data.bookingId}`, {state: {payload: payload, hotelDetails: hotelDetails}})
                    } else {
                        alert("Room not available. Please try again.");
                    }
                }
            })
            .catch(e => {
                console.log(e);
            })
            .finally(() => {
                setProceeding(false);
            })
    }

    if (proceeding){
        return (
            <div>
                <ScrollToTop />
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
            <ScrollToTop/>
            <Navbar />
            <div className="bookingContents">
                <div className="bookingHotelInfo">
                    <div className="hotelInfoCard">
                        <h1>Hotel</h1>
                        <span className="hotelName">Name:  {hotelDetails.name}</span>
                        <span className="hotelAddress">Address: {hotelDetails.address}</span>
                    </div>
                    <div className="hotelInfoCard">
                        <h1>Your booking details</h1>
                        <span>Check-in: {getDateTextShort(searchItem.date[0].startDate)}</span>
                        <br/>
                        <span>From {getTimeText(bookingInfo.maxCheckInTime)}</span>
                        <br/>
                        <span>Check-out: {getDateTextShort(searchItem.date[0].endDate)}</span>
                        <br/>
                        <span>Until {getTimeText(bookingInfo.minCheckOutTime)}</span>
                        <br/>
                        <span>Total length of stay:</span>
                        <br/>
                        <span>{dates} night</span>
                    </div>
                    <div className="hotelInfoCard">
                        <h1>Your Price Summary</h1>
                        Total: ${(bookingInfo.totalPrice / 100).toFixed(2)} <br/>
                        includes taxes and fees <br/>
                        {/*Price information*/}
                    </div>
                </div>
                <div className="bookingInputInfo">
                    <div className="bookingInputInfoCard">
                        <div className="bookingInputInfoRow">
                            <h1>Enter your details</h1>
                            <div className="bookingInputInfoItem">
                                <label className="bookingInputInfoItemLabel">First Name</label>
                                <input
                                    type="text"
                                    value={bookPersonalInfo.firstName}
                                    maxLength={30}
                                    onChange={e => setbookPersonalInfo({...bookPersonalInfo, "firstName": e.target.value})}
                                />
                            </div>
                            <div className="bookingInputInfoItem">
                                <label className="bookingInputInfoItemLabel">Last Name</label>
                                <input type="text"
                                       value={bookPersonalInfo.lastName}
                                       maxLength={30}
                                       onChange={e => setbookPersonalInfo({...bookPersonalInfo, "lastName": e.target.value})}
                                />
                            </div>
                        </div>
                        <div className="bookingInputInfoItem">
                            <label className="bookingInputInfoItemLabel">Email Address</label>
                            <input type="text"
                                   value={bookPersonalInfo.emailAddress}
                                   maxLength={100}
                                   onChange={e => setbookPersonalInfo({...bookPersonalInfo, "emailAddress": e.target.value})}
                            />
                        </div>
                    </div>
                    { rooms.map((room, index) => {
                        console.log(room)
                        return (
                                <div className="bookingInputInfoCard">
                                    <h1 className="roomName">{room.displayName}</h1>
                                    <span>{getBedText(room.bedInfoList)}</span> <br/>
                                    {
                                        room.breakfast && <span>Breafast included in the price</span>
                                    }
                                    {
                                        room.prepayUntil !== undefined && room.prepayUntil.getDate() === searchItem.date[0].startDate.getDate() &&
                                        <div>
                                            No prepayment needed - pay at the property
                                        </div>
                                    }
                                    {
                                        room.prepayUntil !== undefined && room.prepayUntil.getDate() !== searchItem.date[0].startDate.getDate() &&
                                        <div>
                                            <span>No prepayments required before 11:59 PM on {getDateText(room.prepayUntil)}</span>
                                            <br/>
                                        </div>
                                    }
                                    {
                                        room.freeCancellationUntil !== undefined && room.prepayUntil.getDate() !== searchItem.date[0].startDate.getDate() &&
                                        <div>
                                            <span>Free cancellation before 11:59 PM on {getDateText(room.freeCancellationUntil)}</span>
                                            <br/>
                                        </div>
                                    }
                                    <label>Full guest name</label>
                                    <input
                                        type={"text"}
                                        value={guestInfo[index].guestName}
                                        maxLength={30}
                                        onChange={e => setGuestInfo({...guestInfo, [index]: {...guestInfo[index], ["guestName"]: e.target.value}})}
                                    />
                                    <label>Guest email</label> <span>(Optional)</span>
                                    <input
                                        type={"text"}
                                        value={guestInfo[index].guestEmail}
                                        maxLength={30}
                                        onChange={e => setGuestInfo({...guestInfo, [index]: {...guestInfo[index], ["guestEmail"]: e.target.value}})}
                                    />
                                </div>
                            );
                        })
                    }
                    <div className="bookingInputInfoCard">
                        <h1>Special requests</h1>
                        <span>Special requests can't be gauranteed, but the property will do its best to meet your needs. You can always make a special request after your booking is complete.</span>
                        <label>Please write your requests in English</label> <span>(optional)</span>
                        <input type={"text"}
                               maxLength={255}
                                value={specialRequests}
                               onChange={e=>setSpecialRequests(e.target.value)}
                        />
                        <input type={"checkbox"} /> <span>I want rooms close to each other (if available)</span>
                    </div>
                    <div className="bookingInputInfoCard">
                        <h1>Your arrival time</h1>
                        Your room will be ready for check-in at {getTimeText(bookingInfo.maxCheckInTime)} on {getDateTextShort2(searchItem.date[0].startDate)}<br/>
                        { hotelDetails.frontDesk24hr && <span>24-hour front desk - help whenever you need it</span> }
                        <label>Add your estimated arrival time (Local time)</label> <span>(optional)</span><br/>
                        <select onChange={e=>setArrivalTime(e.target.value)}>
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
                    </div>
                    <button onClick={onReserve}>Reserve</button>
                    <button onClick={onBook}>Continue with Paypal</button>
                    <br/>
                    <div className="paypalTestAccount">
                        <span>Paypal test account</span><br/>
                        <span>name: sb-gg0wr26334198@business.example.com</span><br/>
                        <span>password: aP4VNrx6ZH*T</span>
                    </div>
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default BookingConfirmation;