import {
  faBed,
  faCalendarDays,
  faCar,
  faPerson,
  faPlane,
  faTaxi,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import "./header.css";
import { DateRange } from "react-date-range";
import {useEffect, useRef, useState} from "react";
import "react-date-range/dist/styles.css"; // main css file
import "react-date-range/dist/theme/default.css"; // theme css file
import { format } from "date-fns";
import {useLocation, useNavigate} from "react-router-dom";
import {extractAddress} from "../autocomplete/autocomplete";
import {post} from "../../clients";
import jwt_decode from "jwt-decode";

const Header = ({ type, attrs, fetching, setFetching }) => {
  const location = useLocation();


  const addressSearchInput = useRef(null);

  // console.log(attrs)

  const destination = attrs.destination;
  const setDestination = attrs.setDestination;
  const address = attrs.address;
  const setAddress = attrs.setAddress;
  const coordinates = attrs.coordinates;
  const setCoordinates = attrs.setCoordinates;
  const openDate = attrs.openDate;
  const setOpenDate = attrs.setOpenDate;
  const date = attrs.date;
  const setDate = attrs.setDate;
  const openOptions = attrs.openOptions;
  const setOpenOptions = attrs.setOpenOptions;
  const options = attrs.options;
  const setOptions = attrs.setOptions;
  const priceRange = attrs.priceRange;
  const propertyType = attrs.propertyType;
  const propertyRating = attrs.propertyRating;
  const cancellationPolicy = attrs.cancellationPolicy;
  const hotelFacilities = attrs.hotelFacilities;
  const roomAmenities = attrs.roomAmenities;

  const navigate = useNavigate();

  const handleOption = (name, operation) => {
    setOptions((prev) => {
      return {
        ...prev,
        [name]: operation === "i" ? options[name] + 1 : options[name] - 1,
      };
    });
  };

  const handleSearch = () => {
    // console.log(address, destination, coordinates, date, options);
    // console.log(priceRange, propertyType, cancellationPolicy, hotelFacilities, roomAmenities, propertyRating)
    if (Object.keys(address).length === 0){
      alert("Please enter a destination.")
      return;
    }
    if (location.pathname !== "/hotels") {
      navigate("/hotels", {
        state: {
          address, destination, coordinates, date, options,
          priceRange, propertyType, cancellationPolicy, hotelFacilities, roomAmenities, propertyRating
        }
      });
    } else {
      attrs.requestSearch();
    }
  };


  const onChangeAddress = (autocomplete) => {
    setDestination(addressSearchInput.current.value)
    const place = autocomplete.getPlace();
    const newAddress = extractAddress(place, true);
    setAddress(newAddress);
    var newCoordinates = {
      "lat": place.geometry.location.lat(),
      "lng": place.geometry.location.lng()
    }
    setCoordinates(newCoordinates);
    console.log(newAddress, newCoordinates);
  }

  const initAutocomplete = () => {
    if (!addressSearchInput.current) return;

    const autocomplete = new window.google.maps.places.Autocomplete(addressSearchInput.current, { types: ['(cities)']}); // only cities: ,
    autocomplete.setFields((["address_component", "geometry"]));
    autocomplete.addListener("place_changed", () => onChangeAddress(autocomplete))
  }

  useEffect(() => initAutocomplete())

  const handleLogin = () => {
    setFetching(true);
    post("/api/v1/user/test-user")
        .then(response => response.json())
        .then(data => {
          console.log(data)
          const jwt = data.jwt;
          const decoded = jwt_decode(jwt)
          localStorage.setItem("firstname", decoded.sub)
          localStorage.setItem("jwt", jwt);
        })
        .catch(e => {
          console.error(e)})
        .finally(() => {
          setFetching(false);
        })
  }

  if (fetching){
    return;
  }

  return (
    <div className="header">
      <div
        className={
          type === "list" ? "headerContainer listMode" : "headerContainer"
        }
      >
        {/*<div className="headerList">*/}
        {/*  <div className="headerListItem active">*/}
        {/*    <FontAwesomeIcon icon={faBed} />*/}
        {/*    <span>Stays</span>*/}
        {/*  </div>*/}
        {/*  <div className="headerListItem">*/}
        {/*    <FontAwesomeIcon icon={faPlane} />*/}
        {/*    <span>Flights</span>*/}
        {/*  </div>*/}
        {/*  <div className="headerListItem">*/}
        {/*    <FontAwesomeIcon icon={faCar} />*/}
        {/*    <span>Car rentals</span>*/}
        {/*  </div>*/}
        {/*  <div className="headerListItem">*/}
        {/*    <FontAwesomeIcon icon={faBed} />*/}
        {/*    <span>Attractions</span>*/}
        {/*  </div>*/}
        {/*  <div className="headerListItem">*/}
        {/*    <FontAwesomeIcon icon={faTaxi} />*/}
        {/*    <span>Airport taxis</span>*/}
        {/*  </div>*/}
        {/*</div>*/}
        {type !== "list" && (
          <>
            <h1 className="headerTitle">
              A demo website for hotel booking
            </h1>
            <p className="headerDesc">
              Register a free test user account to explore the website.
              {/*Get rewarded for your travels – unlock instant savings of 10% or*/}
              {/*more with a free account*/}
            </p>

            <button className="headerBtn" onClick={handleLogin}>Sign in / Register</button>

          </>
            )}
        <>
            <div className="headerSearch">
              <div className="headerSearchItem">
                <FontAwesomeIcon icon={faBed} className="headerIcon" />
                <input
                    ref = {addressSearchInput}
                  type="text"
                  placeholder={destination}
                  className="headerSearchInput"
                  // onChange={(e) => setAddress(e.target.value)}
                />
              </div>
              <div className="headerSearchItem">
                <FontAwesomeIcon icon={faCalendarDays} className="headerIcon" />
                <span
                  onClick={() => setOpenDate(!openDate)}
                  className="headerSearchText"
                >{`${format(date[0].startDate, "MM/dd/yyyy")} to ${format(
                  date[0].endDate,
                  "MM/dd/yyyy"
                )}`}</span>
                {openDate && (
                  <DateRange
                    editableDateInputs={true}
                    onChange={(item) => setDate([item.selection])}
                    moveRangeOnFirstSelection={false}
                    ranges={date}
                    className="date"
                    minDate={new Date()}
                  />
                )}
              </div>
              <div className="headerSearchItem">
                <FontAwesomeIcon icon={faPerson} className="headerIcon" />
                <span
                  onClick={() => setOpenOptions(!openOptions)}
                  className="headerSearchText"
                >{`${options.adult} adult · ${options.children} children · ${options.room} room`}</span>
                {openOptions && (
                  <div className="options">
                    <div className="optionItem">
                      <span className="optionText">Adult</span>
                      <div className="optionCounter">
                        <button
                          disabled={options.adult <= 1}
                          className="optionCounterButton"
                          onClick={() => handleOption("adult", "d")}
                        >
                          -
                        </button>
                        <span className="optionCounterNumber">
                          {options.adult}
                        </span>
                        <button
                          className="optionCounterButton"
                          onClick={() => handleOption("adult", "i")}
                        >
                          +
                        </button>
                      </div>
                    </div>
                    <div className="optionItem">
                      <span className="optionText">Children</span>
                      <div className="optionCounter">
                        <button
                          disabled={options.children <= 0}
                          className="optionCounterButton"
                          onClick={() => handleOption("children", "d")}
                        >
                          -
                        </button>
                        <span className="optionCounterNumber">
                          {options.children}
                        </span>
                        <button
                          className="optionCounterButton"
                          onClick={() => handleOption("children", "i")}
                        >
                          +
                        </button>
                      </div>
                    </div>
                    <div className="optionItem">
                      <span className="optionText">Room</span>
                      <div className="optionCounter">
                        <button
                          disabled={options.room <= 1}
                          className="optionCounterButton"
                          onClick={() => handleOption("room", "d")}
                        >
                          -
                        </button>
                        <span className="optionCounterNumber">
                          {options.room}
                        </span>
                        <button
                          className="optionCounterButton"
                          onClick={() => handleOption("room", "i")}
                        >
                          +
                        </button>
                      </div>
                    </div>
                  </div>
                )}
              </div>
              <div className="headerSearchItem">
                <button className="headerBtn" onClick={handleSearch}>
                  Search
                </button>
              </div>
            </div>
        </>
      </div>
    </div>
  );
};

export default Header;
