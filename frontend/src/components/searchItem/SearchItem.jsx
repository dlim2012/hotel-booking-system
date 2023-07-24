import "./searchItem.css";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
import SearchItemRooms from "./SearchItemRooms";

const SearchItem = (props) => {
  const navigate = useNavigate();
  // const hotelId = props.hotelInfo.hotelId;
  // const hotelInfo = props.hotelInfo;
  // const date = props.date;
  // const options = props.options;
  const { hotelInfo, date, options, useRecommeded } = props;
  const hotelId = hotelInfo.hotelId;
  console.log("useRecommended", useRecommeded)

  // console.log(props)

  var address = ""
  // if (item.neighborhood != null){
  //   address = address +  item.neighborhood + ", "
  // }
  address += hotelInfo.city
  if (hotelInfo.state != null){
    address += ", " + hotelInfo.state
  }
  // console.log(item)

  const handleSiBtn = () => {
    navigate(`/hotels/${hotelId}`, { state: {hotelInfo: hotelInfo, date: date, options: options} })
  }

  // console.log(item)

  return (
    <div className="searchItem">
      <img
        src="https://cf.bstatic.com/xdata/images/hotel/square600/261707778.webp?k=fa6b6128468ec15e81f7d076b6f2473fa3a80c255582f155cae35f9edbffdd78&o=&s=1"
        alt=""
        className="siImg"
      />
      <div className="siDesc">
        <h1 className="siTitle">{hotelInfo.hotelName}</h1>
        <span>{address}</span>
        { hotelInfo.distance != null &&
          <span>{Math.round(hotelInfo.distance * 100) / 100}km from center</span>
        }
        { hotelInfo.breakfast && <span>Breakfast included</span>}
        { useRecommeded &&
          <div className="recommendation">
            <span className="recommendBox">Recommended for your group</span>
            <div className="recommendationRooms">

              {
                hotelInfo.roomsList.map((roomsInfo, index) => <SearchItemRooms roomsInfo={roomsInfo} />)
              }

            </div>

          </div>
        }

      </div>
      <div className="siDetails">
        <div className="siDetailTexts">
          <span className="siPrice">${Math.round(hotelInfo.totalPrice/ 100)}</span>
          <span className="siTaxOp">Includes taxes and fees</span>
          <button className="siCheckButton" onClick = {handleSiBtn}>See availability</button>
        </div>
      </div>
    </div>
  );
};

export default SearchItem;
