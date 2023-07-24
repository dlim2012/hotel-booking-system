import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../components/navbar/Navbar";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {getWithJwt} from "../../../../../clients";
import Table from "../../../../../templates/Table";
import RoomTable from "../../../../../components/tables/RoomsMgmtTable/RoomsMgmtTable";
import HotelProfileSidebar from "../profile/HotelProfileSidebar";

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
                console.log(data);
                setData(data);
            })
            .catch(error => {
                console.log(error)
            })
            .finally(() => {
                setFetching(false);
                console.log(data)
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
            Header: 'Is Active',
            accessor: 'isActive'
        }
    ]

    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <HotelProfileSidebar />
                <div className="profileContents">
                    <button onClick={navRegister}>register</button>

                    {!fetching &&
                        <RoomTable columns={columns} data={data} />
                    }

                </div>
            </div>
        </div>
    );
}

export default HotelRoom;