import React from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";

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
        </div>
    );
}

export default PaymentCancelled;