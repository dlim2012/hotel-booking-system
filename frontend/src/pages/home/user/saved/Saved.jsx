import React, {useEffect, useState} from 'react';
import Navbar from "../../../../components/navbar/Navbar";
import {getWithJwt} from "../../../../clients";
import {useNavigate} from "react-router-dom";
import './saved.css'

function Saved(props) {


    const [fetching, setFetching] = useState();

    const [savedHotels, setSavedHotels] = useState([]);
    const navigate = useNavigate();

    function fetchSaved(){
        setFetching(true)
        getWithJwt('/api/v1/hotel/saved')
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setSavedHotels(data)
            })
            .catch(e => {
                console.error(e)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    useEffect(() => {
        fetchSaved()
    }, [])

    console.log(fetching, savedHotels)
    if (fetching){
        return (
            <div>
                <Navbar />
            </div>
        )
    }

    return (
        <div>
            <Navbar/>
            {savedHotels.length > 0 &&
            <div className="savedHotelsContainer">
                <table>
                    <tr>
                        <th>Name</th>
                        <th>Address</th>
                        <th></th>
                    </tr>
                    {
                        savedHotels.map((item, index) => {
                            return (
                                <tr>
                                    <td>{item.name}</td>
                                    <td>{item.address}</td>
                                    <td>
                                        <button
                                            onClick={() => {
                                                navigate(`/hotels/${item.id}`)}
                                            }
                                        >View Hotel</button>
                                    </td>
                                </tr>
                            )
                        })
                    }
                </table>
            </div>
            }
        </div>
    );
}

export default Saved;