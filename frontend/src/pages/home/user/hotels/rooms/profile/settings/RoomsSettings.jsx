import React, {useEffect, useState} from 'react';
import Navbar from "../../../../../../../components/navbar/Navbar";
import RoomProfileSidebar from "../RoomProfileSidebar";
import {deleteWithJwt, getWithJwt, putWithJwt} from "../../../../../../../clients";
import {useNavigate, useParams} from "react-router-dom";
import MailList from "../../../../../../../components/mailList/MailList";
import Footer from "../../../../../../../components/footer/Footer";

function RoomsSettings(props) {
    const navigate = useNavigate();
    const { hotelId, roomsId } = useParams();
    const [ fetching, setFetching ] = useState(false);
    const [ isActive, setIsActive ] = useState(false);

    function fetchIsActive(){
        setFetching(true);
        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/is-active`)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setIsActive(data.isActive)
            })
            .catch(e => {
                console.error(e)
            })
            .finally(() => {
                setFetching(false);
            })

    }


    function onActivateSubmit(){
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/activate`)
            .then(() => {
                navigate(`/user/hotel/${hotelId}/rooms`)
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                navigate(`/user/hotel/${hotelId}/rooms`)
            })

    }

    function onInActivateSubmit(){
        putWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}/inactivate`)
            .then(() => {
                navigate(`/user/hotel/${hotelId}/rooms`)
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                navigate(`/user/hotel/${hotelId}/rooms`)
            })

    }

    function onDeleteSubmit(){
        deleteWithJwt(`/api/v1/hotel/hotel/${hotelId}/rooms/${roomsId}`)
            .then(() => {
                navigate(`/user/hotel/${hotelId}/rooms`)
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
            })
    }

    useEffect(() => {
        fetchIsActive();
    }, [])

    if (fetching){
        return (
            <div>
                <Navbar/>
            </div>
        )
    }

    return (
        <div>
            <Navbar/>

            <div className="profileContainer">
                <RoomProfileSidebar />

                <div className="profileContents">
                    <h1>Settings</h1>
                    { !isActive &&
                    <button
                        onClick={onActivateSubmit}
                    >Activate</button>
                    }
                    { isActive &&
                    <button
                        onClick={onInActivateSubmit}
                    >Inactivate</button>
                    }
                    <button
                        onClick={onDeleteSubmit}
                    >Delete</button>
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default RoomsSettings;