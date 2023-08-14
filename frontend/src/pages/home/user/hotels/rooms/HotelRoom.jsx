import './HotelRoom.css'
import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../components/navbar/Navbar";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {getWithJwt} from "../../../../../clients";
import Table from "../../../../../templates/Table";
import RoomTable from "../../../../../components/tables/RoomsMgmtTable/RoomsMgmtTable";
import HotelProfileSidebar from "../profile/HotelProfileSidebar";
import noDataImage from "../../../../../assets/images/No data.png";
import {getMaxAddDate} from "../../../../../assets/Lists";
import MailList from "../../../../../components/mailList/MailList";
import Footer from "../../../../../components/footer/Footer";
import {TailSpin} from "react-loader-spinner";

function HotelRoom(props) {

    const navigate= useNavigate();
    const location = useLocation();
    const [fetching, setFetching] = useState();
    const [data, setData] = useState([]);

    const { hotelId } = useParams()

    const navRegister = () => {
        console.log("register");
        navigate("register");
    }



    const fetchRooms = () => {
        setFetching(true);
        getWithJwt("/api/v1/hotel/hotel/" + hotelId + "/rooms/list")
            .then(response => response.json())
            .then(data => {
                console.log(data)

                for (let row of data){
                    if (row.isActive){
                        row.dates = row.availableFrom + " ~ " + row.availableUntil;
                    } else {
                        row.dates = "Inactive";
                    }
                    row.info = <button
                        onClick={() => navigate(`${row.id}/info`)}
                    >Edit</button>
                }
                setData(data);
            })
            .catch(error => {
                console.log(error)
            })
            .finally(() => {
                setFetching(false);
            })
    }

    useEffect(() => {
        fetchRooms()
    }, [])

    const columns = [
        {
            Header: 'Id',
            accessor: 'rowIndex'
        },
        {
            Header: 'Display Name',
            accessor: 'displayName'
        },
        {
            Header: 'Quantity',
            accessor: 'quantity'
        },
        {
            Header: 'Available dates',
            accessor: 'dates'
        },
        {
            Header: '',
            accessor: 'info'
        }
    ]


    if (fetching){
        return (
            <div>
                <Navbar />
                <div className="profileContainer">
                    <HotelProfileSidebar />
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
            </div>
        )
    }


    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <HotelProfileSidebar />
                <div className="profileContents">
                    <button onClick={navRegister}>register</button>
                    <div className="profileRooms">
                    {!fetching && data.length === 0 &&
                        <div className="roomsNoData">
                            <img src={noDataImage} width="200px"/>
                        </div>
                    }
                    {!fetching && data.length > 0 &&
                        <RoomTable columns={columns} data={data} />
                    }
                    </div>
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default HotelRoom;