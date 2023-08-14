import "./hotel.css";
import Navbar from "../../../../components/navbar/Navbar";
import Header from "../../../../components/header/Header";
import MailList from "../../../../components/mailList/MailList";
import Footer from "../../../../components/footer/Footer";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCalendarDays,
  faCircleArrowLeft,
  faCircleArrowRight,
  faCircleXmark, faHeart,
  faLocationDot, faPerson, faStar as FaStar
} from "@fortawesome/free-solid-svg-icons";
import React, {useEffect, useState} from "react";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import login from "../../user/user/login/Login";
import {deleteWithJwt, getWithJwt, postWithJwt} from "../../../../clients";
import RecommendedTable from "../../../../components/tables/HotelPageTables/RecommendedTable";
import image1 from '../../../../assets/images/hotel-sub-images/hotel-sub-1.jpeg'
import image2 from '../../../../assets/images/hotel-sub-images/hotel-sub-2.jpeg'
import image3 from '../../../../assets/images/hotel-sub-images/hotel-sub-3.jpeg'
import image4 from '../../../../assets/images/hotel-sub-images/hotel-sub-4.jpeg'
import image5 from '../../../../assets/images/hotel-sub-images/hotel-sub-5.jpeg'
import image6 from '../../../../assets/images/hotel-sub-images/hotel-sub-6.jpeg'
import {faStar} from "@fortawesome/free-regular-svg-icons";
import {format} from "date-fns";
import {DateRange} from "react-date-range";
import {DateRangePicker} from "@syncfusion/ej2-react-calendars";
import {bedsMap, MAX_BOOKING_DAYS} from "../../../../assets/Lists";
import {GoogleMap, Marker} from "@react-google-maps/api";
import ScrollToTop from "../../../../components/scrollToTop/scrollToTop";
import {TailSpin} from "react-loader-spinner";


const photos = [
  {src: image1,},
  {src: image2,},
  {src: image3,},
  {src: image4,},
  {src: image5,},
  {src: image6,}
];

const containerStyle = {
    width: '600px',
    height: '600px'
};

const zoom = 10;

const Hotel = () => {
  const location = useLocation();
  const navigate = useNavigate();

    // console.log(localStorage)

    var today = new Date();
    var tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);

  const hotelInfo = location.state?.hotelInfo
  const [date, setDate] = useState(
      location.state == null ?
        [
            {
                startDate: today,
                endDate: tomorrow,
                key: "selection",
            },]

        : location.state.date)
  const options = location.state?.options


  const [slideNumber, setSlideNumber] = useState(0);
  const [openSlideNumber, setOpenSlideNumber] = useState(false);
  const searchItem = location.state;
  // const [searchItem, setSearchItem] = useState(location.state)
  const [hotelDetails, setHotelDetails] = useState({address: "", description: "", roomsInfoList: []});
  const [availability, setAvailability] = useState({})
  const [roomSelection, setRoomSelection] = useState(
      location?.state?.roomSelection == null ? {}: location.state.roomSelection);
  const [savedByUser, setSavedByUser] = useState(false);

  const [center, setCenter] = useState({})

  const [fetching1, setFetching1] = useState(false);
  const [fetching2, setFetching2] = useState(false);

  let {hotelId } = useParams();


    var minDay1 = new Date()
    var minDay2 = new Date()
    var maxDay1 = new Date()
    var maxDay2 = new Date()
    minDay2.setDate(minDay1.getDate() + 1)
    maxDay1.setDate(minDay1.getDate() + MAX_BOOKING_DAYS - 1)
    maxDay2.setDate(minDay2.getDate() + MAX_BOOKING_DAYS)
    minDay1 = minDay1.toISOString().substring(0, 10)
    minDay2 = minDay2.toISOString().substring(0, 10)
    maxDay1 = maxDay1.toISOString().substring(0, 10)
    maxDay2 = maxDay2.toISOString().substring(0, 10)

  const fetchHotelDetails = () => {

    setFetching1(true);
    getWithJwt(`/api/v1/hotel/public/hotel/${hotelId}/rooms`)
        .then(response => response.json())
        .then(data => {
            console.log(data)
            var addressComponents = [];
            if (data.addressLine2 != null && data.addressLine2.length > 0){
            addressComponents.push(data.addressLine2);
            }
            if (data.addressLine1 != null && data.addressLine1.length > 0){
            addressComponents.push(data.addressLine1);
            }
            if (data.city != null && data.city.length > 0){
            addressComponents.push(data.city);
            }
            if (data.state != null && data.state.length > 0){
            addressComponents.push(data.state);
            }
            if (data.zipcode != null && data.zipcode.length > 0){
            addressComponents.push(data.zipcode);
            }
            data["address"] = addressComponents.join(', ')

            var startDate = date[0].startDate
            var today = new Date();
            for (let rooms of data.roomsInfoList) {
                // if (rooms.noPrepaymentDays === 0){
                //     rooms.prepayUntil = new Date(date[0].startDate);
                //     rooms.freeCancellationUntil = new Date(date[0].startDate);
                // }

                var prepayUntil = new Date();
                var freeCancellationUntil = new Date();
                prepayUntil.setDate(startDate.getDate() - rooms.noPrepaymentDays)
                freeCancellationUntil.setDate(startDate.getDate() - rooms.freeCancellationDays)

                if (prepayUntil.getDate() >= today.getDate()) {
                    rooms.prepayUntil = prepayUntil;
                }
                if (freeCancellationUntil.getDate() >= today.getDate()) {
                    rooms.freeCancellationUntil = freeCancellationUntil;
                }
            }
            setCenter({lat: data.latitude, lng: data.longitude})
            setHotelDetails(data);
            setSavedByUser(data.saved)

            console.log(data)
        })
        .catch(e => {
          console.error(e)})
        .finally(
            () => {setFetching1(false)}
        )

  }

  const fetchAvailabilityInfo = (date) => {

    var payload = {
      // "hotelId": hotelId,
      "startDate": date[0].startDate,
      "endDate": date[0].endDate
    }
    setFetching2(true)
    postWithJwt(`/api/v1/search/hotel/${hotelId}/availability`, payload)
        .then(response => response.json())
        .then(data => {
            console.log(data)
            setAvailability(data.rooms);
        })
        .catch(error =>
            {
              console.error(error)
            }
        )
        .finally(
            () => {
              setFetching2(false)
            }
        )
  }

  const handleOpen = (i) => {
    setSlideNumber(i);
    setOpenSlideNumber(true);
  };

  const handleMove = (direction) => {
    let newSlideNumber;

    if (direction === "l") {
      newSlideNumber = slideNumber === 0 ? 5 : slideNumber - 1;
    } else {
      newSlideNumber = slideNumber === 5 ? 0 : slideNumber + 1;
    }

    setSlideNumber(newSlideNumber)
  };


  const onReserve = () => {
    // check-in, check-out, numdays, num rooms, num beds, price total
    if (numRoomsSelected === 0){
      return;
    }

    if (localStorage.getItem("jwt") == null){
      navigate("/user/login", {state: {from: `/hotels/${hotelId}`, state: {...location.state, ["roomSelection"]: roomSelection }}})
      return;
    }

    const roomsInfoList = hotelDetails.roomsInfoList;
    var rooms = []
    var maxCheckInTime = 0;
    var minCheckOutTime = 1440;
    for (let i=0; i<roomsInfoList.length; i++){
      if (roomSelection[roomsInfoList[i].id] === "0"){
        continue;
      }
      var quantity = parseInt(roomSelection[roomsInfoList[i].id]);
      for (let j=0; j<quantity; j++) {
        rooms.push(
            {
              id: roomsInfoList[i].id,
              displayName: roomsInfoList[i].displayName,
              bedInfoList: roomsInfoList[i].bedInfoList,
              freeCancellationUntil: roomsInfoList[i].freeCancellationUntil,
              prepayUntil: roomsInfoList[i].prepayUntil,
              breakfast: roomsInfoList[i].breakfast,
              checkInTime: roomsInfoList[i].checkInTime,
              checkOutTime: roomsInfoList[i].checkOutTime
            }
        )
        maxCheckInTime = Math.max(maxCheckInTime, roomsInfoList[i].checkInTime);
        minCheckOutTime = Math.min(minCheckOutTime, roomsInfoList[i].checkOutTime);
      }
    }

    var bookingInfo = {
      hotelDetails: hotelDetails,
      totalPrice: totalPrice,
      numRooms: numRoomsSelected,
      maxCheckInTime: maxCheckInTime,
      minCheckOutTime: minCheckOutTime,
      rooms: rooms
    }

    navigate(`/hotels/${hotelDetails.id}/booking`,
        {
          state: {
            bookingInfo: bookingInfo,
            // item: item,
            searchItem: {...searchItem, ["date"]: date},
          }
        }
    )

  }

  const onClickSaved = () => {
      if (localStorage.getItem("jwt") == null){
          navigate("/user/login", {state: {from: `/hotels/${hotelId}`, state: {...location.state, ["roomSelection"]: roomSelection }}})
          return;
      }


    var newSaved = !savedByUser;
    var payload = {
      hotelId: hotelId
    }
    if (newSaved){
      postWithJwt('/api/v1/hotel/saved', payload)
          .catch(e => {
            console.error(e)})
    } else {
      deleteWithJwt('/api/v1/hotel/saved', payload)
          .catch(e => {
            console.error(e)})
    }
    setSavedByUser(newSaved);
  }

    useEffect(() => {
        fetchHotelDetails();
        fetchAvailabilityInfo(date);
    }, [])


    const [map, setMap] = React.useState(null)
    const onLoad = React.useCallback(function callback(map) {
        // const bounds = new window.google.maps.LatLngBounds(center);
        // map.fitBounds(bounds);
        map.setZoom(zoom);
        setMap(map)
    }, [])
    const onUnmount = React.useCallback(function callback(map) {
        setMap(null)
    }, [])

  if (fetching1 || fetching2
      // || Object.keys(hotelDetails).length === 0 || Object.keys(availability).length === 0
  ){
    return (
        <div>
            <Navbar/>
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


    var dates = Math.round((date[0].endDate.getTime() - date[0].startDate.getTime()) / 86400000);
    var numRoomsSelected = 0
    var totalPrice = 0
    for (let key in roomSelection){
        var numRoom = parseInt(roomSelection[key]);
        numRoomsSelected += numRoom;
        totalPrice += numRoom * availability[key]?.price
    }

    var firstAvailable = true;

  return (
    <div>
        <ScrollToTop/>
      <Navbar />
      {/*<Header type="list" />*/}
      <div className="hotelContainer">
        {openSlideNumber && (
          <div className="slider">
            <FontAwesomeIcon
              icon={faCircleXmark}
              className="close"
              onClick={() => setOpenSlideNumber(false)}
            />
            <FontAwesomeIcon
              icon={faCircleArrowLeft}
              className="arrow"
              onClick={() => handleMove("l")}
            />
            <div className="sliderWrapper">
              <img src={photos[slideNumber].src} alt="" className="sliderImg" />
            </div>
            <FontAwesomeIcon
              icon={faCircleArrowRight}
              className="arrow"
              onClick={() => handleMove("r")}
            />
          </div>
        )}
        <div className="hotelWrapper">
          {/*<button className="bookNow">Reserve or Book Now!</button>*/}
            <div className="savedContainer">
                  { !savedByUser &&
                    <button
                        className="saved"
                        onClick={onClickSaved}
                    ><FontAwesomeIcon icon={faStar} /></button>
                  }
                  { savedByUser &&
                    <button
                        className="saved"
                        onClick={onClickSaved}
                    ><FontAwesomeIcon icon={FaStar} /></button>
                  }
            </div>
          {/*<button className="saved"><FontAwesomeIcon icon="fa-regular fa-heart" /></button>*/}
          <h1 className="hotelTitle">{hotelDetails.name}</h1>
          <div className="hotelAddress">
            <FontAwesomeIcon icon={faLocationDot} />
            <span>{hotelDetails.address}</span>
          </div>
            {hotelInfo != null && hotelInfo.distance != null &&
              <span className="hotelDistance">
                Excellent location – {Math.round(hotelInfo.distance * 10)/10} km from center
              </span>
            }
            {hotelInfo != null &&

                <span className="hotelPriceHighlight">
            Book a stay over ${hotelInfo.totalPrice / 100} at this property and get a free airport taxi
          </span>
            }
          <div className="hotelImages">
            {photos.map((photo, i) => (
              <div className="hotelImgWrapper" key={i}>
                <img
                  onClick={() => handleOpen(i)}
                  src={photo.src}
                  alt=""
                  className="hotelImg"
                />
              </div>
            ))}
          </div>
          {/*<div className="hotelDetails">*/}
          {/*  <div className="hotelDetailsTexts">*/}
          {/*    <h1 className="hotelTitle">Stay in the heart of City</h1>*/}
          {/*    <p className="hotelDesc">*/}
          {/*      {hotelInfo.description}*/}
          {/*    </p>*/}

          {/*  </div>*/}
          {/*  <div className="hotelDetailsPrice">*/}
          {/*    <h1>Perfect for a 9-night stay!</h1>*/}
          {/*    <span>*/}
          {/*      Located in the real heart of Krakow, this property has an*/}
          {/*      excellent location score of 9.8!*/}
          {/*    </span>*/}
          {/*    <h2>*/}
          {/*      <b>$945</b> (9 nights)*/}
          {/*    </h2>*/}
          {/*    <button>Reserve or Book Now!</button>*/}
          {/*  </div>*/}
          {/*</div>*/}

          <div className="hotelFacilityItems">
            <div className="hotelFacilityItemsTitle">
              <h2>Hotel facilities</h2>
            </div>
            <div className="hotelFacilityItemsList">
              { hotelDetails.facilityDisplayNameList &&
                  hotelDetails.facilityDisplayNameList.map((facility, index) => {
                    return <div className="hotelFacilityItem">{facility}</div>;
                  })
              }
            </div>
          </div>
          { hotelInfo != null &&
          <div className="Recommended">
            <h2>Recommended</h2>
            <RecommendedTable hotelInfo={hotelInfo} date={date} options={options}
                              hotelDetails={hotelDetails}
                              roomSelection={roomSelection} setRoomSelection={setRoomSelection}
            />
          </div>
          }

          <div className="hotelDetailsTexts">
            <h2 className="hotelTitle">Description</h2>
            <span className="hotelDesc">
              {hotelDetails.description}
            </span>
          </div>

          <div className="RoomInfo">
            <h2>Availability</h2>
            <div className="searchItems">
              <div className="searchItem">
                <span>Check-In</span>
                <input
                    type="date"
                    value={date[0].startDate.toISOString().substring(0, 10)}
                    min={minDay1}
                    max={maxDay1}
                    onChange={e => {
                      let newStartDate = new Date(e.target.value)
                      let newEndDate = date[0].endDate
                      if(newStartDate > newEndDate){
                        newEndDate.setDate(newStartDate.getDate()+1);
                      }
                      var newDate = [{...date[0],
                        ["startDate"]: newStartDate,
                        ["endDate"]: newEndDate
                      }]
                      setDate(newDate);
                        fetchAvailabilityInfo(newDate);
                    }}
                />
                <span>Check-Out</span>
                <input
                    type="date"
                    value={date[0].endDate.toISOString().substring(0, 10)}
                    min={minDay2}
                    max={maxDay2}
                    onChange={e => {
                      let newEndDate = new Date(e.target.value)
                      let newStartDate = date[0].startDate
                      if(newStartDate > newEndDate){
                        newStartDate.setDate(newEndDate.getDate()-1);
                      }
                      var newDate = [{...date[0],
                        ["startDate"]: newStartDate,
                        ["endDate"]: newEndDate
                      }]
                      setDate(newDate);
                        fetchAvailabilityInfo(newDate);
                    }}
                />
              </div>
            </div>
            <div>
              <table  id="availabilityTable" className="availabilityTable">
                <tr>
                  <th>Room Type</th>
                  <th>Price for {dates} night(s)</th>
                  <th>Your Choices</th>
                  <th>Select</th>
                  <th></th>
                </tr>
                {
                  hotelDetails.roomsInfoList.map((info, index) => {
                      if (!(info.id in availability) || (availability[info.id].quantity <= 0)){
                          return;
                      }
                    var avail = availability[info.id]
                      var addButton = firstAvailable;
                      firstAvailable = false;
                      // console.log(info.prepayUntil, info.prepayUntil == null)
                    return (
                        <tr className="availibilityTableRow">
                          <td>
                            <div className="roomName">{info.displayName}</div>
                              <div className="roomBeds">
                                  {info.bedInfoList.map((item, index) => {
                                      return (
                                        <div className="roomBedItem">{item.quantity} {bedsMap[item.size.toString()]} bed{item.quantity > 1 ? "s": ""}</div>
                                      );
                                  })}</div>
                            <div className="roomFacilities">
                                {info.facilityList.map((facility, index) => {
                                  return <div className="roomFacilityItem">{facility}</div>
                                })}
                            </div>
                          </td>
                          <td>
                            <div>
                              Max {info.maxAdult} adult(s), {info.maxChild} child(ren)
                            </div>
                            <div>
                              Price: ${availability[info.id]?.price / 100}
                            </div>
                            <div>
                              Tax included
                            </div>
                          </td>
                          <td>
                              {info.prepayUntil != null && info?.prepayUntil?.getDate() === date[0].startDate.getDate() &&
                                  <div>
                                      No prepayment needed - pay at the property
                                  </div>

                              }
                            {info.prepayUntil != null && info.prepayUntil.getDate() !== date[0].startDate.getDate() &&
                              <div>
                                  No prepayment until {info.prepayUntil.toISOString().substring(0, 10)}
                              </div>
                            }
                              { info.freeCancellationUntil != null && info.prepayUntil.getDate() !== today.getDate() &&
                                    <div>
                                      Free cancellation before {info.freeCancellationUntil.toISOString().substring(0, 10)}
                                    </div>
                              }
                          </td>
                          <td>
                            <div>
                              <select
                                  value={roomSelection[info.id]}
                                  onChange={event => {setRoomSelection({...roomSelection, [info.id]: event.target.value})}}
                              >
                                <option value="0">0 </option>
                                <option value="1">1 (${avail.price / 100}) </option>
                                {avail.quantity >= 2 && <option value="2">2 (${(avail.price / 100 * 2).toFixed(2)}) </option>}
                                {avail.quantity >= 3 && <option value="3">3 (${(avail.price / 100 * 3).toFixed(2)}) </option>}
                                {avail.quantity >= 4 && <option value="4">4 (${(avail.price / 100 * 4).toFixed(2)}) </option>}
                                {avail.quantity >= 5 && <option value="5">5 (${(avail.price / 100 * 5).toFixed(2)}) </option>}
                                {avail.quantity >= 6 && <option value="6">6 (${(avail.price / 100 * 6).toFixed(2)}) </option>}
                                {avail.quantity >= 7 && <option value="7">7 (${(avail.price / 100 * 7).toFixed(2)}) </option>}
                                {avail.quantity >= 8 && <option value="8">8 (${(avail.price / 100 * 8).toFixed(2)}) </option>}
                                {avail.quantity >= 9 && <option value="9">9 (${(avail.price / 100 * 9).toFixed(2)}) </option>}

                              </select>
                            </div>
                          </td>
                          {addButton &&
                              <td
                                  rowSpan={hotelDetails.roomsInfoList.length}
                                  className="availabilityTableReserveCol"
                              >
                                {numRoomsSelected > 0 &&
                                    <div>{numRoomsSelected} room(s) for ${totalPrice / 100}</div>
                                }
                                <button
                                    className="reserveBtn"
                                    onClick={onReserve}
                                >I'll reserve</button>
                                <ul>
                                  <li className="availibilityNote">Confirmation is immediate</li>
                                  <li className="availibilityNote">No booking or credit card fees!</li>
                                </ul>
                              </td>
                          }
                        </tr>
                    );
                  })
                }
              </table>
            </div>

          </div>
        </div>
        <div className="hotelMapContainer">
            <h2>Location</h2>
            <div className="hotelMap">
                <GoogleMap
                    mapContainerStyle={containerStyle}
                    center={center}
                    zoom={zoom}
                    onLoad={onLoad}
                    onUnmount={onUnmount}
                >
                    {/*<Marker lat={coordinates.lat} lng={coordinates.lng} />*/}
                    <Marker
                        position={center}
                        // draggable={true}
                        // onDrag={(coord, index)=> {
                        //     const {latLng} = coord;
                        //     setCoordinates({lat: latLng.lat(), lng: latLng.lng()})
                        // }}
                    />
                    <></>
                </GoogleMap>
            </div>
        </div>

      </div>
        <MailList />
        <Footer />
    </div>
  );
};

export default Hotel;
