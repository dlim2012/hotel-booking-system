import "./search-list.css";
import Navbar from "../../../components/navbar/Navbar";
import Header from "../../../components/header/Header";
import { useLocation } from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import { format } from "date-fns";
import { DateRange } from "react-date-range";
import SearchItem from "../../../components/searchItem/SearchItem";
import login from "../user/user/login/Login";
import {extractAddress} from "../../../components/autocomplete/autocomplete";
import {post, postWithJwt} from "../../../clients";
import {
  cancellationPolicies,
  getDefaultCancellationPoliciesMap,
  getDefaultHotelFacilitiesMap,
  getDefaultPropertyRatingMap,
  getDefaultPropertyTypeMap, getDefaultRoomAmenitiesMap,
  hotelFacilities,
  propertyRatings,
  propertyTypesMap,
  roomFacilities
} from "../../../assets/Lists";

import Slider from "@mui/material/Slider";
import MailList from "../../../components/mailList/MailList";
import Footer from "../../../components/footer/Footer";
import {TailSpin, ThreeDots} from "react-loader-spinner";

const searchListPath = "/hotels"


const SearchList = () => {
  const location = useLocation();
  const addressSearchInput = useRef(null);

  const useRecommended = location.state.useRecommended  == null ? true : location.state.useRecommended;
  console.log("useRecommended", useRecommended)

  const [destination, setDestination] = useState(location.state.destination);
  const [address, setAddress] = useState(location.state.address);
  const [coordinates, setCoordinates] = useState(location.state.coordinates);
  const [openDate, setOpenDate] = useState(false);
  const [date, setDate] = useState(location.state.date);
  const [openOptions, setOpenOptions] = useState(false);
  const [options, setOptions] = useState(location.state.options);

  const [priceRange, setPriceRange] = useState([0, 1000000])
  const [priceMaxRange, setPriceMaxRange] = useState([0, 10000])

  const [propertyType, setPropertyType] = useState(
      location.state.propertyType !== undefined ?
          location.state.propertyType
          : getDefaultPropertyTypeMap()
  );
  const [cancellationPolicy, setCancellationPolicy] = useState(
      location?.state.cancellationPolicy !== undefined ?
          location.state.cancellationPolicy
          : getDefaultCancellationPoliciesMap()
  );
  const [propertyRating, setPropertyRating] = useState(
          location.state?.propertyRating !== undefined?
              location.state.propertyRating
              : getDefaultPropertyRatingMap()
      )
  ;
  const [hotelFacility, setHotelFacility] = useState(
      location.state?.hotelFacilities !== undefined ?
          location.state.hotelFacilities
          : getDefaultHotelFacilitiesMap()
  );
  const [roomAmenity, setRoomAmenity] = useState(
      location.state?.roomAmenities !== undefined ?
          location.state.roomAmenities
          : getDefaultRoomAmenitiesMap()
  );

  const [searchResults, setSearchResults] = useState([])
  const [numSearchResults, setNumSearchResults] = useState(0)
  const [resultString, setResultString] = useState("")

  const [fetching, setFetching] = useState(false);

  const [openCriteria, setOpenCriteria] = useState(
      // {priceRange: false, propertyType: false, propertRating: false, hotelFacility: false, roomAmenity: false}
    {priceRange: true, propertyType: true, propertyRating: true, hotelFacility: true, roomAmenity: true}
  );

  useEffect(() => {

  })

  const onChangeAddress = (autocomplete) => {
    // setDestination(addressSearchInput.current.value)
    const place = autocomplete.getPlace();
    console.log(place)
    const newAddress = extractAddress(place, true);
    setAddress(newAddress);
    var newCoordinates = {
      "lat": place.geometry.location.lat(),
      "lng": place.geometry.location.lng()
    }
    setCoordinates(newCoordinates);
  }

  const initAutocomplete = () => {
    if (!addressSearchInput.current) return;
    const autocomplete = new window.google.maps.places.Autocomplete(addressSearchInput.current, { types: ['(cities)']}); // only cities: ,
    autocomplete.setFields((["address_component", "geometry"]));
    autocomplete.addListener("place_changed", () => onChangeAddress(autocomplete))
  }

  const requestSearch = (item) => {
    setFetching(true);
    var payload = {
      useRecommeded: useRecommended,
      startDate: date[0].startDate.toISOString(),
      endDate: date[0].endDate.toISOString(),
      city: address.city,
      state: address.state,
      country: address.country,
      numAdult: options.adult,
      numChild: options.children == null ? 0 : options.children,
      numRoom: options.room,
      numBed: -1,
      propertyTypes: [],
      propertyRating: [],
      hotelFacility: [],
      roomsFacility: [],
      priceMin: Math.floor(Math.exp(priceRange[0])),
      priceMax: Math.floor(Math.exp(priceRange[1]))
    }

    var payloadPropertyType = item?.propertyType == null ? propertyType : item.propertyType
    for (var key in payloadPropertyType){
      if (payloadPropertyType[key] === true){
        payload["propertyTypes"].push(key);
      }
    }

    var payloadPropertyRating = item?.propertyRating == null ? propertyRating : item.propertyRating;
    for (var key in payloadPropertyRating){
      if (payloadPropertyRating[key] === true){
        payload["propertyRating"].push(key);
      }
    }

    var payloadHotelFacility = item?.hotelFacility == null ? hotelFacility : item.hotelFacility
    for (var key in payloadHotelFacility){
      if (payloadHotelFacility[key] === true){
        payload["hotelFacility"].push(key);
      }
    }

    var payloadRoomAmenity = item?.roomAmenity == null ? roomAmenity : item.roomAmenity
    for (var key in payloadRoomAmenity){
      if (payloadRoomAmenity[key] === true){
        payload["roomsFacility"].push(key);
      }
    }

    if (coordinates.lat != null && coordinates.lng != null){
      payload["latitude"] = coordinates.lat;
      payload["longitude"] = coordinates.lng;
    }

    post("/api/v1/search/hotel", payload)
        .then(response => response.json())
        .then(data => {
          var list = data.hotelList;
          list.sort(function(x, y){
            if (x.score > y.score){
              return 1;
            } else if (x.score < y.score){
              return -1;
            } else {
              return 0;
            }
          })
            if (list.length > 10){
                list = list.slice(0, 10)
            }
          var newMaxPriceRange = [Math.log(data.minPrice / 1.1), Math.log(data.maxPrice * 1.1)]
          console.log(newMaxPriceRange)
          setPriceMaxRange(newMaxPriceRange)
          // setPriceRange(newMaxPriceRange)
          setSearchResults(list);
          setNumSearchResults(data.numResults);

          if (destination === "Where are you going?"){
            var newResultString = `San Francisco, California, United States, ${format(date[0].startDate, "MM/dd/yyyy")} to ${format(date[0].endDate, "MM/dd/yyyy")}, ${options.adult} adult${options.adult > 1 ? 's' : ''}, ${options.children == null ? 0 : options.children} children, and ${options.room} room${options.adult > 1 ? 's' : ''}`;
          }
          else if (destination != null) {
            var newResultString = `${destination}, ${format(date[0].startDate, "MM/dd/yyyy")} to ${format(date[0].endDate, "MM/dd/yyyy")}, ${options.adult} adult${options.adult > 1 ? 's' : ''}, ${options.children == null ? 0 : options.children} children, and ${options.room} room${options.adult > 1 ? 's' : ''}`;
          } else {
            var newResultString = `${format(date[0].startDate, "MM/dd/yyyy")} to ${format(date[0].endDate, "MM/dd/yyyy")}, ${options.adult} adult${options.adult > 1 ? 's' : ''}, ${options.children == null ? 0 : options.children} children, and ${options.room} room${options.adult > 1 ? 's' : ''}`;
          }
          setResultString(newResultString)
        })
        .catch((error) => {
          console.error("Error", error)
        }).finally(() => {
            console.log("setFetching(false);")
            setFetching(false);
        }
    )
  }

  useEffect(() => {
    requestSearch({})
  }, [])

  const onPriceRangeChange = (event, newValue) => {
      setPriceRange(newValue)
  }

  var headerAttr = {
    destination, setDestination,
  address, setAddress,
    coordinates, setCoordinates,
    openDate, setOpenDate,
    date, setDate,
    openOptions, setOpenOptions,
    options, setOptions,
    priceRange, setPriceRange,
    setPriceMaxRange,
    propertyType,
    cancellationPolicy,
    hotelFacility,
    roomAmenity,
    propertyRating,
    requestSearch
    // cancellationPolicy
  }

  console.log(openCriteria)
  console.log(openCriteria.priceRange)

  if (fetching){
    return (
        <div>
          <Navbar />
          <Header type="list" attrs={headerAttr}/>

            <div className="listContainer">
                <div className="listWrapper">
                    <SearchCriteriaSideBar
                        openCriteria={openCriteria}
                        onPriceRangeChange={onPriceRangeChange}
                        priceRange={priceRange}
                        priceMaxRange={priceMaxRange}
                        propertyType={propertyType}
                        setPropertyType={setPropertyType}
                        propertyRating={propertyRating}
                        setPropertyRating={setPropertyRating}
                        hotelFacility={hotelFacility}
                        setHotelFacility={setHotelFacility}
                        roomAmenity={roomAmenity}
                        setRoomAmenity={setRoomAmenity}
                        requestSearch={requestSearch}
                    />
                    <div className="listResult">
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
            </div>
        </div>
    );
  }

  return (
    <div>
      <Navbar />
      <Header type="list" attrs={headerAttr}/>
      <div className="listContainer">
        <div className="listWrapper">
          <SearchCriteriaSideBar
              openCriteria={openCriteria}
              onPriceRangeChange={onPriceRangeChange}
              priceRange={priceRange}
              priceMaxRange={priceMaxRange}
              propertyType={propertyType}
              setPropertyType={setPropertyType}
              propertyRating={propertyRating}
              setPropertyRating={setPropertyRating}
              hotelFacility={hotelFacility}
              setHotelFacility={setHotelFacility}
              roomAmenity={roomAmenity}
              setRoomAmenity={setRoomAmenity}
              requestSearch={requestSearch}
          />
          <div className="listResult">
            { address.city != null && address.city !=="" &&
              <div className="searchResult">
                <h2>{numSearchResults} results for {resultString}</h2>
              </div>
            }
            { !fetching &&
            <div>
              {searchResults.map((item, index) => <SearchItem hotelInfo = {item} date={date} options={options} useRecommeded={useRecommended}/>)}
            </div>
            }
          </div>

        </div>
      </div>
      <MailList/>
      <Footer/>
    </div>
  );
};

function SearchCriteriaSideBar(props){

  const {openCriteria, priceRange, onPriceRangeChange,
      priceMaxRange,
      propertyType, setPropertyType,
      propertyRating, setPropertyRating,
      hotelFacility, setHotelFacility,
      roomAmenity, setRoomAmenity,
      requestSearch
  } = props

  return (
      <div className="listSearch">
        <h1 className="lsTitle">Search criteria</h1>
        {/*<div className="lsItem">*/}
        {/*  <label>Destination</label>*/}
        {/*  <input ref={addressSearchInput} value={destination} onChange={event => {*/}
        {/*    setDestination(event.target.value)}} type="text" />*/}
        {/*</div>*/}
        {/*<div className="lsItem">*/}
        {/*  <label>Check-in Date</label>*/}
        {/*  <span onClick={() => setOpenDate(!openDate)}>{`${format(*/}
        {/*    date[0].startDate,*/}
        {/*    "MM/dd/yyyy"*/}
        {/*  )} to ${format(date[0].endDate, "MM/dd/yyyy")}`}</span>*/}
        {/*  {openDate && (*/}
        {/*    <DateRange*/}
        {/*      onChange={(item) => setDate([item.selection])}*/}
        {/*      minDate={new Date()}*/}
        {/*      ranges={date}*/}
        {/*    />*/}
        {/*  )}*/}
        {/*</div>*/}
        <div className="lsItem">
          <label
              // onClick={() => {setOpenCriteria({...openCriteria, ["priceRange"]: !openCriteria.priceRange})}}
          >Price Range</label>
          { openCriteria.priceRange &&
              <div className="lsPriceRange">
                <Slider
                    value={priceRange}
                    onChange={onPriceRangeChange}
                    min={priceMaxRange[0]}
                    max={priceMaxRange[1]}
                    step={0.0001}
                />
                <span className="lsPriceRangeText">Selected range: $ {Math.floor(Math.exp(priceRange[0]) /100)} ~ {Math.ceil(Math.exp(priceRange[1])/100)}</span>
              </div>
          }
        </div>
        <div className="lsItem">
          <label
              // onClick={() => setOpenCriteria({...openCriteria, ["propertyType"]: !openCriteria.propertyType})}
          >Property Type</label>
          { openCriteria.propertyType && Object.keys(propertyTypesMap).map((item, index) => {
            return <div className="lsOptions">
              <div className="lsOptionItem">
                                <span className="lsOptionText">
                                  <input
                                      type="checkbox"
                                      checked = {propertyType[item]}
                                      onChange={(e) => {
                                        var newPropertyType = {...propertyType, [item]: e.target.checked}
                                        setPropertyType(newPropertyType);
                                        requestSearch({propertyType: newPropertyType});
                                      }}
                                  />
                                  {item}
                                </span>
              </div>
            </div>
          })}
        </div>
        {/*<div className="lsItem">*/}
        {/*  <label>Cancellation Policy</label>*/}
        {/*  { cancellationPolicies.map((item, index) => {*/}
        {/*    return <div className="lsOptions">*/}
        {/*      <div className="lsOptionItem">*/}
        {/*        <span className="lsOptionText">*/}
        {/*          <input*/}
        {/*              type="checkbox"*/}
        {/*              onChange={(e) => {*/}
        {/*                setCancellationPolicy({...cancellationPolicy, [item]: e.target.checked});*/}
        {/*              }}*/}
        {/*          />*/}
        {/*          {item}*/}
        {/*        </span>*/}
        {/*      </div>*/}
        {/*    </div>*/}
        {/*  })}*/}
        {/*</div>*/}
        <div className="lsItem">
          <label
              // onClick={() => {setOpenCriteria({...openCriteria, ["propertyRating"]: !openCriteria.propertyRating})}}
          >Property rating</label>
          { openCriteria.propertyRating && propertyRatings.map((item, index) => {
            return <div className="lsOptions">
              <div className="lsOptionItem">
                    <span className="lsOptionText">
                      <input
                          type="checkbox"
                          checked = {propertyRating[item['value']]}
                          onChange={(e) => {
                            var newPropertyRating = {...propertyRating, [item['value']]: e.target.checked}
                            setPropertyRating(newPropertyRating);
                            requestSearch({propertyRating: newPropertyRating});
                          }}
                      />
                      {item['label']}
                    </span>
              </div>
            </div>
          })}
        </div>
        <div className="lsItem">
          <label
              // onClick={() => {setOpenCriteria({...openCriteria, ["hotelFacility"]: !openCriteria.hotelFacility})}}
          >Hotel Facilities</label>
          { openCriteria.hotelFacility && hotelFacilities.map((item, index) => {
            return <div className="lsOptions">
              <div className="lsOptionItem">
                      <span className="lsOptionText">
                        <input
                            type="checkbox"
                            checked = {hotelFacility[item]}
                            onChange={(e) => {
                              var newHotelFacility = {...hotelFacility, [item]: e.target.checked}
                              setHotelFacility(newHotelFacility);
                              requestSearch({hotelFacility: newHotelFacility});
                            }}
                        />
                        {item}
                      </span>
              </div>
            </div>
          })}
        </div>
        <div className="lsItem">
          <label
              // onClick={() => {setOpenCriteria({...openCriteria, ["roomAmenity"]: !openCriteria.roomAmenity})}}
          >Room Amenities</label>
          { openCriteria.roomAmenity && roomFacilities.map((item, index) => {
            return <div className="lsOptions">
              <div className="lsOptionItem">
                      <span className="lsOptionText">
                        <input
                            type="checkbox"
                            checked={roomAmenity[item]}
                            onChange={(e) => {
                              var newRoomAmenity = {...roomAmenity, [item]: e.target.checked}
                              setRoomAmenity(newRoomAmenity);
                              requestSearch({roomAmenity: newRoomAmenity});
                            }}
                        />
                        {item}
                      </span>
              </div>
            </div>
          })}
        </div>
        {/*<button>Search</button>*/}
      </div>
  );
}

export default SearchList;
