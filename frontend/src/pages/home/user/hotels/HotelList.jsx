import './hotelList.css';
import Navbar from "../../../../components/navbar/Navbar";
import {useLocation, useNavigate} from "react-router-dom";
import Table from "../../../../templates/Table";
import {useEffect, useState} from "react";
import {getWithJwt} from "../../../../clients";
import HotelMgmtTable from "../../../../components/tables/HotelMgmtTable/HotelMgmtTable";

function HotelList(props) {
    const navigate= useNavigate();
    const [fetching, setFetching] = useState();
    const [data, setData] = useState([]);

    const navRegister = () => {
        navigate("/user/hotel/register")
    }

    const mock = [
        {'id': 1, 'displayName': 'Display Name', 'address': 'my address'}
    ]

    const fetchHotels = () => {
        setFetching(true);
        getWithJwt("/api/v1/hotel/manage")
            .then(response => response.json())
            .then(data => {
                console.log(data);
                let rowIndex = 0;
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
        fetchHotels()
    }, [])

    const columns = [
        {
            Header: 'Id',
            accessor: 'rowIndex'
        },
        {
            Header: 'Display Name',
            accessor: 'name'
        },
        {
            Header: 'Address',
            accessor: 'address'
        },
        // {
        //     Header: '',
        //     accessor: 'buttons'
        // }
    ]

    return (
        <div>
            <Navbar />
            <div className="hotelMgmtContainer">
                <div className="btnContainer">
                    <button onClick={navRegister}>Register</button>
                </div>
                {!fetching &&
                    <HotelMgmtTable columns={columns} data={data} />
                }
            </div>
        </div>
    );
}

export default HotelList;