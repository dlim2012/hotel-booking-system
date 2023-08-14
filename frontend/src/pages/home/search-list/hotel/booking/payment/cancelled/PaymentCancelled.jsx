import './cancelled.css'
import React from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import {useLocation, useParams, useSearchParams} from "react-router-dom";
import MailList from "../../../../../../../components/mailList/MailList";
import Footer from "../../../../../../../components/footer/Footer";

function PaymentCancelled(props) {
    const location = useLocation();
    // const status = URLSearchParams("status")
    const [searchParams] = useSearchParams();
    const status = searchParams.get("status")

    // console.log(status)
    return (
        <div>
            <Navbar />
            <div className="paymentFailed">
                <h1>Payment failed</h1>
                <div>
                    Payment failed due to {status}.
                </div>
                <button>Retry Payment</button>
                <button>Home page</button>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default PaymentCancelled;