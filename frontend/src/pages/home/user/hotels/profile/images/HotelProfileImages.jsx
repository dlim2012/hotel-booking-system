import React, {useEffect, useState} from 'react';
import {hotelFacilities} from "../../../../../../assets/Lists";
import Navbar from "../../../../../../components/navbar/Navbar";
import HotelProfileSidebar from "../HotelProfileSidebar";
import HotelFacilities from "../../register/facilities/HotelFacilities";
import emptyImage from '../../../../../../assets/images/empty.jpg'
import login from "../../../user/login/Login";
import axios from "axios";
import {useParams} from "react-router-dom";
import {getWithJwt, postImageWithJwt} from "../../../../../../clients";
import MailList from "../../../../../../components/mailList/MailList";
import Footer from "../../../../../../components/footer/Footer";

function HotelProfileImages(props) {
    const { hotelId } = useParams();
    const [imageUrls, setImageUrls] = useState([]);

    const [imagePreview, setImagePreview] = useState(emptyImage);
    const [imageData, setImageData] = useState(null);
    const [imageName, setImageName] = useState("");

    const fetchImages = () => {

        getWithJwt(`/api/v1/hotel/hotel/${hotelId}/images`)
            .then(response => response.json())
            .then(data => {
                setImageUrls(data);
            })
            .catch(e => console.error(e))
            .finally()
    }

    const onImageChange = event => {
        if (event.target.files && event.target.files[0]) {
            let img = event.target.files[0];
            if (img.size > 1048576){
                alert("Image size is too big.")
                return;
            }
            setImagePreview(URL.createObjectURL(img));
            setImageData(img)
        }
    };

    const uploadImageAndName = () => {
        console.log(imageData)
        postImageWithJwt(`/api/v1/hotel/hotel/${hotelId}/image`, imageData)
            .catch(e => {
                console.error(e)})
            .finally(() => {
                    setImagePreview(emptyImage);
                    setImageData(null);
                    fetchImages()
                }
            )
    }

    useEffect(() => {
        fetchImages()
    }, [])

    return (
        <div>
            <Navbar />
            <div className="profileContainer">
                <HotelProfileSidebar />
                <div className="profileContents">
                    <div className="profileContent">

                        <h1>Images</h1>
                        <div>
                            {imageUrls.map((imageUrl) => (
                                <img
                                    src={imageUrl.url}
                                    key={imageUrl.id}
                                    width={"400px"}
                                    height={"300px"}
                                    onClick={(e) => {
                                        console.log(e) }}
                                />
                            ))}
                        </div>
                    </div>
                    <div className="profileContent">

                    <h1>Add Image</h1>
                    <div>
                        <img
                            alt={"Not Found"}
                            width={"400px"}
                            height={"300px"}
                            src = {imagePreview}
                            />
                        <br />
                        <button onClick={() => setImagePreview(emptyImage)}>Remove</button>
                    </div>
                    <br />
                    <br />

                    <input
                        type="file"
                        accept = "image/*"
                        name="myImage"
                        // value = {imageName}
                        onChange={onImageChange}
                    />
                    <input
                        type={"text"}
                        value={imageName}
                        onChange={(e) => setImageName(e.target.value)}
                    />
                    <button
                        onClick={uploadImageAndName}
                    >Upload</button>
                    </div>
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default HotelProfileImages;