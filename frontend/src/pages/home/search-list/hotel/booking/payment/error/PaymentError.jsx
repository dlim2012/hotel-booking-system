import React from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import MailList from "../../../../../../../components/mailList/MailList";
import Footer from "../../../../../../../components/footer/Footer";

function PaymentCancelled(props) {
    return (
        <div>
            <Navbar />
            <div>
                <h1>Payment Error</h1>
                <div className="BookingItemInfo">
                    (Hotel Name)
                </div>
                <button>Retry Payment</button> <br />
                <button>Home page</button>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default PaymentCancelled;