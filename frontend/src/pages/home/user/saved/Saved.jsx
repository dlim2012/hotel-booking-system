import './saved.css'
import React, {useEffect, useState} from 'react';
import Navbar from "../../../../components/navbar/Navbar";
import {getWithJwt} from "../../../../clients";
import {useNavigate} from "react-router-dom";
import noDataImage from "../../../../assets/images/No data.png"
import MailList from "../../../../components/mailList/MailList";
import Footer from "../../../../components/footer/Footer";
import {TailSpin} from "react-loader-spinner";

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
        )
    }

    return (
        <div>
            <Navbar/>
            <div className="savedHotelsContainer">
            <h1>Save Hotels</h1>
            {
                savedHotels.length === 0 &&
                <div className="savedHotelsNoData">
                    <img src={noDataImage} width="200px"/>
                </div>
            }
            {savedHotels.length > 0 &&
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
            }
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default Saved;