import "./featured.css";
import {useNavigate} from "react-router-dom";
import chicago from "../../assets/images/home/city/chicago.jpeg";
import newYork from "../../assets/images/home/city/new-york.jpeg";
import losAngelesImage from "../../assets/images/home/city/los-angeles.jpeg";
import {post} from "../../clients";
import {useEffect, useState} from "react";



var startDate = new Date();
var endDate = new Date();
startDate.setDate(startDate.getDate())
endDate.setDate(endDate.getDate() + 1)

const state_common = {
    date: [{startDate: startDate, endDate: endDate, key: "selection"}],
    options: {adult: 1, children: 0, room: 1},
    useRecommended: true
}

const featuredItems = [
    // {
    //     image: chicago,
    //     state: {
    //         address: {"city": "San Francisco", "state": "California", "country": "United States"},
    //         destination: "San Francisco, California, United States",
    //         coordinates: {},
    //     },
    //     name: "Chicago"
    // },
    {
        image: chicago,
        state: {
            address: {"city": "Chicago", "state": "Illinois", "country": "United States"},
            destination: "Chicago, Illinois, United States",
            coordinates: {lat: 41.8781136, lng: -87.6297982},
        },
        name: "Chicago"
    },
    {
        image: newYork,
        state:
            {
                address: {"city": "New York", "state": "New York", "country": "United States"},
                destination: "New York, New York, United States",
                coordinates: {lat: 40.7127753, lng: -74.0059728},
            },
        name: "New York"
    },
    {
        image: losAngelesImage,
        state: {
            address: {"city": "Los Angeles", "state": "California", "country": "United States"},
            destination: "Los Angeles, California, United States",
            coordinates: {lat: 34.0522342, lng: -118.2436849},
        },
        name: "Los Angeles"
    }
]


const Featured = (attrs) => {

    const [counts, setCounts] = useState([null, null, null])

    const [fetching, setFetching] = useState(false)
    // const { cityCounts, setCityCounts, setFetchingCityCounts} = attrs

    const navigate = useNavigate();




    const fetchCountByCity = () => {
        var payload = []
        for (let featuredItem of featuredItems){
            payload.push(featuredItem.state.address)
        }
        setFetching(true)

        console.log(payload)

        post('/api/v1/search/count/city', payload)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                var newCityCounts = []
                for (let countItem of data){
                    newCityCounts.push(countItem.count)
                }
                setCounts(newCityCounts)
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {
                setFetching(false)
            })
    }


    useEffect( () => {
        fetchCountByCity()
    }, [])


    if (fetching){
        return (
            <></>
        );
    }


  return (
    <div className="featured">
        {
            featuredItems.map((item, index) => {
                return (
                    <div className="featuredItem">
                        <img
                            src={item.image}
                            alt=""
                            className="featuredImg"
                            onClick={() => navigate("/hotels", {state: {...state_common, ...item.state}}
                            )}
                        />
                        <div className="featuredTitles">
                            <h1>{item.name}</h1>
                            <h2>{counts[index]} properties</h2>
                        </div>
                    </div>
                )
            })
        }
    </div>
  );
};

export default Featured;
