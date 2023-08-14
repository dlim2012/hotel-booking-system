import "./paymentSuccess.css"
import Navbar from "../../../../../../../components/navbar/Navbar";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect} from "react";
import {post, postWithJwt} from "../../../../../../../clients";
import login from "../../../../../user/user/login/Login";
import MailList from "../../../../../../../components/mailList/MailList";
import Footer from "../../../../../../../components/footer/Footer";

function PaymentSuccess(props) {
    const navigate = useNavigate();

    const {bookingId,  } = useParams();
    const queryParameters = new URLSearchParams(window.location.search)
    const paymentId = queryParameters.get("paymentId")
    const token = queryParameters.get("token")
    const PayerId = queryParameters.get("PayerID")
    console.log(bookingId, paymentId, token, PayerId)
    //
    // useEffect(() => {
    //     var payload = {
    //         paymentId: paymentId,
    //         payerId: PayerId
    //     }
    //     console.log(payload)
    //     postWithJwt(`/api/v1/booking/payment/success/${bookingId}`, payload)
    //         .then(response => response.json())
    //         .then(data => {
    //             console.log(data)
    //         })
    //         .catch(e => {
    //             console.error(e)})
    //
    // }, [])


    return (
        <div>
            <Navbar />
            <div className="paymentSuccessful">
                <div className="paymentSuccessfulTitle">
                    <h1>Payment Successful</h1>
                </div>
                {/*A confirmation email has been sent to () <br/>*/}
                <button onClick={() => {navigate("/")}}>Home page</button>
                <button onClick={() => {navigate("/user/bookings")}}>Results</button>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default PaymentSuccess;

