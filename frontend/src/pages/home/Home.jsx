import Featured from "../../components/featured/Featured";
import FeaturedProperties from "../../components/featuredProperties/FeaturedProperties";
import Footer from "../../components/footer/Footer";
import Header from "../../components/header/Header";
import MailList from "../../components/mailList/MailList";
import Navbar from "../../components/navbar/Navbar";
import PropertyList from "../../components/propertyList/PropertyList";
import "./home.css";
import {getDefaultHeaderAttributes} from "../../components/header/HeaderAttributes";
import {useEffect, useState} from "react";
import {useLocation} from "react-router-dom";
import {post} from "../../clients";
import chicago from "../../assets/images/home/city/chicago.jpeg";
import newYork from "../../assets/images/home/city/new-york.jpeg";
import losAngelesImage from "../../assets/images/home/city/los-angeles.jpeg";


const Home = () => {


    const location = useLocation();


    const [fetchingUser, setFetchingUser] = useState(false);


    const [destination, setDestination] = useState(
        location.state == null? "Where are you going?" :
            location.state?.destination);
    // const [address, setAddress] = useState(location.state?.address);
    const [address, setAddress] = useState(
        // {}
        // {"city": "New York", "state": "New York", "country": "United States"}
        {"city": "San Francisco", "state": "California", "country": "United States"}
        // {"city": "Amherst", "state": "Massachusetts", "country": "United States"}
    );
    // const [coordinates, setCoordinates] = useState(location.state?.coordinates);
    const [coordinates, setCoordinates] = useState({
        'lat': 42.3732216, "lng": -72.5198537
    });
    const [openDate, setOpenDate] = useState(false);
    var today = new Date();
    var tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);
    const [date, setDate] = useState(
        (location.state == null ?
            [
                {
                    startDate: today,
                    endDate: tomorrow,
                    key: "selection",
                },]
            : location.state.date)
    );
    const [openOptions, setOpenOptions] = useState(false);
    const [options, setOptions] = useState(
        location.state == null ?
            {
                adult: 1,
                children: 0,
                room: 1,
            }: location.state.options
    );

    const [propertyTypeCounts, setPropertyTypeCounts] = useState([null, null, null, null, null])
    const [fetchingPropertyTypeCounts, setFetchingPropertyTypeCounts] = useState(false)

    var headerAttr = {
        destination, setDestination, address, setAddress, coordinates, setCoordinates,
        openDate, setOpenDate, date, setDate, openOptions, setOpenOptions, options, setOptions
    }


    if (fetchingUser){
        return;
    }

  return (
    <div>
      <Navbar />
      <Header
          attrs={headerAttr}
          fetching={fetchingUser}
          setFetching={setFetchingUser}
      />
      <div className="homeContainer">
        <Featured
            // cityCounts = {cityCounts}
            // setCityCounts = {setCityCounts}
            // setFetchingCityCounts={setFetchingCityCounts}
        />
        <h1 className="homeTitle">Browse by property type</h1>
        <PropertyList/>
        <h1 className="homeTitle">Homes guests love</h1>
        <FeaturedProperties/>
        <MailList/>
        <Footer/>
      </div>
    </div>
  );
};

export default Home;
