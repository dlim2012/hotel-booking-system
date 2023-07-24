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

  const [priceRange, setPriceRange] = useState([0, 0])
  const [usePriceRange, setUsePriceRange] = useState(false);
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
    console.log(newAddress, newCoordinates)
  }

  const initAutocomplete = () => {
    if (!addressSearchInput.current) return;
    const autocomplete = new window.google.maps.places.Autocomplete(addressSearchInput.current, { types: ['(cities)']}); // only cities: ,
    autocomplete.setFields((["address_component", "geometry"]));
    autocomplete.addListener("place_changed", () => onChangeAddress(autocomplete))
  }


  const requestSearch = () => {
    setFetching(true);

    console.log(priceRange)

    var payload = {
      useRecommeded: useRecommended,
      startDate: date[0].startDate.toISOString(),
      endDate: date[0].endDate.toISOString(),
      city: address.city,
      state: address.state,
      country: address.country,
      numAdult: options.adult,
      numChild: options.child == null ? 0 : options.child,
      numRoom: options.room,
      numBed: -1,
      propertyTypes: [],
      propertyRating: [],
      hotelFacility: [],
      roomsFacility: [],
      priceMin: usePriceRange ? Math.floor(Math.exp(priceRange[0])): null,
      priceMax: usePriceRange ? Math.floor(Math.exp(priceRange[1])) : null
    }

    console.log(propertyType)
    for (var key in propertyType){
      if (propertyType[key] === true){
        payload["propertyTypes"].push(key);
      }
    }
    for (var key in propertyRating){
      if (propertyRating[key] === true){
        payload["propertyRating"].push(key);
      }
    }
    for (var key in hotelFacility){
      if (hotelFacility[key] === true){
        payload["hotelFacility"].push(key);
      }
    }
    for (var key in roomAmenity){
      if (roomAmenity[key] === true){
        payload["roomsFacility"].push(key);
      }
    }

    if (coordinates.lat != null && coordinates.lng != null){
      payload["latitude"] = coordinates.lat;
      payload["longitude"] = coordinates.lng;
    }

    console.log("search request", payload)
    post("/api/v1/search/hotel", payload)
        .then(response => response.json())
        .then(data => {
          console.log(data)
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
          var newMaxPriceRange = [Math.log(data.minPrice / 1.1), Math.log(data.maxPrice * 1.1)]
          setPriceMaxRange(newMaxPriceRange)
          if (priceRange[1] === 0){
            setPriceRange(newMaxPriceRange)
          }
          setSearchResults(list);
          setNumSearchResults(data.numResults);

          if (destination != null) {
            var newResultString = `${destination}, ${format(date[0].startDate, "MM/dd/yyyy")} to ${format(date[0].endDate, "MM/dd/yyyy")}, ${options.adult} adult${options.adult > 1 ? 's' : ''}, ${options.child == null ? 0 : options.child} children, and ${options.room} room${options.adult > 1 ? 's' : ''}`;
          } else {
            var newResultString = `${format(date[0].startDate, "MM/dd/yyyy")} to ${format(date[0].endDate, "MM/dd/yyyy")}, ${options.adult} adult${options.adult > 1 ? 's' : ''}, ${options.child == null ? 0 : options.child} children, and ${options.room} room${options.adult > 1 ? 's' : ''}`;
          }
          setResultString(newResultString)
        })
        .catch((error) => {
          console.error(error)
        }).finally(() => {
            console.log("setFetching(false);")
            setFetching(false);
        }

    )
  }

  useEffect(() => {
    console.log("useEffect() in search-list")
    requestSearch()
  }, [])

  const onPriceRangeChange = (event, newValue) => {
      setPriceRange(newValue)
      setUsePriceRange(true)
  }

  var headerAttr = {
    "destination": destination,
    "setDestination": setDestination,
    "address": address,
    "setAddress": setAddress,
    "coordinates": coordinates,
    "setCoordinates": setCoordinates,
    "openDate": openDate,
    "setOpenDate": setOpenDate,
    "date": date,
    "setDate": setDate,
    "openOptions": openOptions,
    "setOpenOptions": setOpenOptions,
    "options": options,
    "setOptions": setOptions,
    "priceRange": priceRange,
    "setPriceMaxRange": setPriceMaxRange,
    // "setPriceRange": setPriceRange,
    "propertyType": propertyType,
    // "setPropertyType": setPropertyType,
    "cancellationPolicy": cancellationPolicy,
    // "setCancellationPolicy": setCancellationPolicy,
    "hotelFacilities": hotelFacility,
    // "setHotelFacilities": setHotelFacilities,
    "roomAmenities": roomAmenity,
    // "setRoomAmenities": setRoomAmenities,
    "propertyRating": propertyRating,
    // "setPropertyRating": setPropertyRating
    "requestSearch": requestSearch

  }

  if (fetching){
    return <>...</>
  }

  return (
    <div>
      <Navbar />
      <Header type="list" attrs={headerAttr}/>
      <div className="listContainer">
        <div className="listWrapper">
          <div className="listSearch">
            <h1 className="lsTitle">Search</h1>
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
              <label>Price Range</label>
              <div className="lsPriceRange">

                  <Slider
                      value={priceRange}
                      onChange={onPriceRangeChange}
                      min={priceMaxRange[0]}
                      max={priceMaxRange[1]}
                      step={0.0001}
                  />
                  <span className="lsPriceRangeText">Selected range: {Math.floor(Math.exp(priceRange[0]) /100)} ~ {Math.ceil(Math.exp(priceRange[1])/100)}</span>
              </div>
            </div>
            <div className="lsItem">
              <label>Property Type</label>
              { Object.keys(propertyTypesMap).map((item, index) => {
                return <div className="lsOptions">
                          <div className="lsOptionItem">
                                <span className="lsOptionText">
                                  <input
                                      type="checkbox"
                                      checked = {propertyType[item]}
                                      onChange={(e) => {
                                        setPropertyType({...propertyType, [item]: e.target.checked});
                                        requestSearch();
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
              <label>Property rating</label>
              { propertyRatings.map((item, index) => {
                return <div className="lsOptions">
                  <div className="lsOptionItem">
                    <span className="lsOptionText">
                      <input
                          type="checkbox"
                          checked = {propertyRating[item['value']]}
                          onChange={(e) => {
                            setPropertyRating({...propertyRating, [item['value']]: e.target.checked});
                            requestSearch();
                          }}
                      />
                      {item['label']}
                    </span>
                  </div>
                </div>
              })}
            </div>
            <div className="lsItem">
              <label>Hotel Facilities</label>
              { hotelFacilities.map((item, index) => {
                return <div className="lsOptions">
                  <div className="lsOptionItem">
                      <span className="lsOptionText">
                        <input
                            type="checkbox"
                            checked = {hotelFacility[item]}
                            onChange={(e) => {
                              setHotelFacility({...hotelFacility, [item]: e.target.checked});
                              requestSearch();
                            }}
                        />
                        {item}
                      </span>
                  </div>
                </div>
              })}
            </div>
            <div className="lsItem">
              <label>Room Amenities</label>
              { roomFacilities.map((item, index) => {
                return <div className="lsOptions">
                  <div className="lsOptionItem">
                      <span className="lsOptionText">
                        <input
                            type="checkbox"
                            checked={roomAmenity[item]}
                            onChange={(e) => {
                              setRoomAmenity({...roomAmenity, [item]: e.target.checked});
                              requestSearch();
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
    </div>
  );
};

export default SearchList;
