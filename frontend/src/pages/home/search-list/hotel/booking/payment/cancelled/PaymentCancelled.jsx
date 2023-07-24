import React from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import {useLocation, useParams, useSearchParams} from "react-router-dom";

function PaymentCancelled(props) {
    const location = useLocation();
    // const status = URLSearchParams("status")
    const [searchParams] = useSearchParams();
    const status = searchParams.get("status")

    // console.log(status)
    return (
        <div>
            <Navbar />
            <div>
                <h1>Payment Failed</h1>
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