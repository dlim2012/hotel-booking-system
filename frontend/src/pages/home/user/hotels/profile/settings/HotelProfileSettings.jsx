
import './hotelProfileSettings.css'
import HotelProfileSidebar from "../HotelProfileSidebar";
import Navbar from "../../../../../../components/navbar/Navbar";
import React, {useEffect, useState} from "react";
import {deleteWithJwt, getWithJwt, putWithJwt} from "../../../../../../clients";
import {useNavigate, useParams} from "react-router-dom";
import {put} from "axios";
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";
import {TailSpin} from "react-loader-spinner";

function HotelProfileSettings(props) {

    const navigate = useNavigate();

    const { hotelId } = useParams();

    const [isActiveInfo, setIsActiveInfo] = useState({});
    const [openConfirm, setOpenConfirm] = useState(false);
    const [fetching, setFetching] = useState(false);

    function fetchIsActive(){
        setFetching(true);
        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/is-active`)
            .then(response=>response.json())
            .then(data => {
                console.log(data)
                setIsActiveInfo(data)
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                setFetching(false);
            })
    }

    function onActivate(){
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/activate`)
            .then(() => {
                navigate('/user/hotel')
            })
            .catch(e => {
                console.error(e)
            })
            .finally(() => {
            })
    }

    function onInActivate(){
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/inactivate`)
            .then(() => {
                navigate('/user/hotel')
            })
            .catch(e => {
                console.error(e)
            })
            .finally(() => {
            })
    }

    function onDelete(){
        deleteWithJwt(`/api/v1/hotel/hotel/${hotelId}`)
            .then(() => {
                navigate('/user/hotel')
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
            })
    }

    useEffect(() => {
        fetchIsActive()
    }, [])

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
                    <div className="profileTitle">
                        <h1>Settings</h1>
                    </div>
                    { isActiveInfo.isActive &&
                    <div>
                        <button
                            onClick={() => {
                                if (openConfirm === "inactivate") {
                                    setOpenConfirm("")
                                } else {
                                    setOpenConfirm("inactivate")
                                }
                            }}
                        >Inactivate</button><br/>
                        {
                            openConfirm === "inactivate" && isActiveInfo.roomsActiveCount === 0 &&
                            <div>
                                <button
                                    onClick={onInActivate}
                                >Submit</button>
                            </div>
                        }
                        {
                            openConfirm === "inactivate" && isActiveInfo.roomsActiveCount > 0 &&
                            <span>All rooms should be inactivated to inactivate hotel. {isActiveInfo.roomsActiveCount} rooms are active.</span>
                        }
                    </div>
                    }
                    {
                        !isActiveInfo.isActive &&
                        <button

                            onClick={() => {
                                if (openConfirm === "activate") {
                                    setOpenConfirm("")
                                } else {
                                    setOpenConfirm("activate")
                                }
                            }}
                        >Activate</button>
                    }
                    {
                        openConfirm === "activate" &&
                        <div>
                            <button
                                onClick={onActivate}
                            >Submit</button>
                        </div>
                     }
                    <div>
                        <button
                            onClick={() => {
                                if (openConfirm === "delete") {
                                    setOpenConfirm("")
                                } else {
                                    setOpenConfirm("delete")
                                }
                            }}
                        >Delete</button>
                        {
                            openConfirm === "delete" && isActiveInfo.roomsActiveCount > 0 &&
                            <div>
                                <span>All rooms should be inactivated to inactivate hotel. {isActiveInfo.roomsActiveCount} rooms are active.</span>
                            </div>
                        }
                        {
                            openConfirm === "delete" && isActiveInfo.roomsActiveCount === 0 &&
                            <div>
                                <button
                                    onClick={onDelete}
                                >Submit</button>
                            </div>
                        }
                    </div>
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default HotelProfileSettings;

