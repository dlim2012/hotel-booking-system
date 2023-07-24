import "./propertyList.css";
import hotelImage from '../../assets/images/home/property/hotel.webp'
import cabinImage from '../../assets/images/home/property/cabin.jpeg'
import resortImage from '../../assets/images/home/property/resort.jpeg'
import villaImage from '../../assets/images/home/property/villa.jpeg'
import apartmentImage from '../../assets/images/home/property/apartment.jpeg'
import motelImage from '../../assets/images/home/property/motel.jpeg'
import {useEffect, useState} from "react";
import {post} from "../../clients";
import {useNavigate} from "react-router-dom";
import {getDefaultPropertyTypeMap, propertyTypesMap} from "../../assets/Lists";

var startDate = new Date();
var endDate = new Date();
startDate.setDate(startDate.getDate())
endDate.setDate(endDate.getDate() + 1)


const state_common = {
    address: {"city": "", "state": "", "country": ""},
    // destination: "Property Type: Hotel",
    coordinates: {lat: null, lng: null},
    date: [{startDate: startDate, endDate: endDate, key: "selection"}],
    options: {adult: 1, children: 0, room: 1},
    useRecommended: true
}

const propertyTypes = [
    {
        displayName: "Hotel",
        image: hotelImage,
        name: "Hotel",
        state: {
            propertyType: {Hotel: true}
        },
    },
    {
        displayName: "Apartment",
        image: apartmentImage,
        name: "Apartment",
        state: {
            propertyType: {Apartment: true}
        },

    },
    {
        displayName: "Resort",
        image: resortImage,
        name: "Resort",
        state: {
            propertyType: {Resort: true}
        },
    },
    {
        displayName: "Villa",
        image: villaImage,
        name: "Villa",
        state: {
            propertyType: {Villa: true}
        },
    },
    {
        displayName: "Cabin",
        image: cabinImage,
        name: "Cabin",
        state: {
            propertyType: {Cabin: true}
        },
    },
]

const PropertyList = () => {

    const [counts, setCounts] = useState([null, null, null, null, null])

    const [fetching, setFetching] = useState(false)

    const navigate = useNavigate();

    const fetchCountByPropertyType = () => {
        var payload = []
        for (let propertyType of propertyTypes){
            payload.push(propertyType.name)
        }
        setFetching(true)

        post('/api/v1/search/count/property-type', payload)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                var newCounts = []
                for (let countItem of data){
                    newCounts.push(countItem.count)
                }
                setCounts(newCounts)
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                setFetching(false)
            })
    }

    useEffect( () => {
        fetchCountByPropertyType()
    }, [])


    if (fetching){
        return (
            <></>
        );
    }

    return (

        <div className="pList">
          {
              propertyTypes.map((item, index) => {

                  return (
                      <div className="pListItem">
                          <img
                              src={item.image}
                              alt=""
                              className="pListImg"
                              onClick={() => navigate("/hotels", {state: {...state_common, ...item.state}}
                                  )}
                          />
                          <div className="pListTitles">
                              <h1>{item.name}</h1>
                              <h2>{counts[index]} {item.name}{counts[index] > 1 ? "s" : ""}</h2>
                          </div>
                      </div>
                  );
              })

          }
        </div>
    );
};

export default PropertyList;
