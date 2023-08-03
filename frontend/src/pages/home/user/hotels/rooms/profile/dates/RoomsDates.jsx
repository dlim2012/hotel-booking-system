import './roomDates.css'
import Navbar from "../../../../../../../components/navbar/Navbar";
import RoomProfileSidebar from "../RoomProfileSidebar";
import {useEffect, useState} from "react";
import {getWithJwt} from "../../../../../../../clients";
import {useParams} from "react-router-dom";

function RoomsDates(props) {




    return (
        <div>
            <Navbar />
            <div className="datesContainer">
                <RoomProfileSidebar />
                <div className="profileContents">
                    <h1>Date availability</h1>
                    <div>
                        <select />
                    </div>

                </div>
            </div>

        </div>
    );
}

export default RoomsDates;


// Need booking
// Need dates
// Need