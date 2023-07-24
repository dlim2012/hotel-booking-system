// import { Admin, Resource } from 'react-admin';
//
// import restProvider from 'ra-data-simple-rest';
import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";
import Home from "./pages/home/Home";
import Hotel from "./pages/home/search-list/hotel/Hotel";
import SearchList from "./pages/home/search-list/search-list";
import Login from "./pages/home/user/user/login/Login";
import HotelList from "./pages/home/user/hotels/HotelList";
import HotelLocation from "./pages/home/user/hotels/register/location/HotelLocation";
import HotelRegister from "./pages/home/user/hotels/register/HotelRegister";
import RoomRegister from "./pages/home/user/hotels/rooms/register/RoomRegister"
import HotelRoom from "./pages/home/user/hotels/rooms/HotelRoom";
import PersonalDetails from "./pages/home/user/user/profile/PersonalDetails";
import Registration from "./pages/home/user/user/registration/Registration";
import HotelProfileInfo from "./pages/home/user/hotels/profile/info/HotelProfileInfo";
import HotelProfileAddress from "./pages/home/user/hotels/profile/address/HotelProfileAddress";
import HotelProfileFacilities from "./pages/home/user/hotels/profile/facilities/HotelProfileFacilities";
import RoomsProfileInfo from "./pages/home/user/hotels/rooms/profile/info/RoomsProfileInfo";
import RoomsProfileFacilities from "./pages/home/user/hotels/rooms/profile/facilities/RoomsProfileFacilities";
import ProtectedRoutes from "./pages/home/user/ProtectedRoutes";
import HotelProfileImages from "./pages/home/user/hotels/profile/images/HotelProfileImages";
import BookingConfirmation from "./pages/home/search-list/hotel/booking/booking/BookingConfirmation";
import PaymentSuccess from "./pages/home/search-list/hotel/booking/payment/success/PaymentSuccess";
import PaymentCancelled from "./pages/home/search-list/hotel/booking/payment/cancelled/PaymentCancelled";
import PaymentError from "./pages/home/search-list/hotel/booking/payment/error/PaymentError";
import UserBookings from "./pages/home/user/bookings/booking/UserBookings";
import ProfileMain from "./pages/home/user/user/profile/ProfileMain";
import HotelBookings from "./pages/home/user/hotels/booking/HotelBookings";
import BookingDetails from "./pages/home/user/bookings/booking/bookingDetails/BookingDetails";
import RoomsDates from "./pages/home/user/hotels/rooms/profile/dates/RoomsDates";
import RoomsBooking from "./pages/home/user/hotels/rooms/profile/booking/RoomsBooking";
import HotelMain from "./pages/home/user/hotels/main/HotelMain";
import HotelDates from "./pages/home/user/hotels/dates/HotelDates";
import Saved from "./pages/home/user/saved/Saved";
import BookingArchived from "./pages/home/user/bookings/booking/bookingArchived/BookingArchived";

// const dataProvider = restProvider('http://localhost:9000');

function App() {
  return (
      <BrowserRouter>
          <Routes>
              <Route path="/" element={<Home/>}/>
              <Route path="/user/register" element={<Registration />} />
              <Route path="/user/login" element={<Login />} />
              <Route element={<ProtectedRoutes />}>
                  <Route path="/user/profile" element={<ProfileMain />} />
                  <Route path="/user/hotel" element={<HotelList />} />
                  <Route path="/user/hotel/register" element={<HotelRegister />} />
                  <Route path="/user/hotel/:hotelId" element={<HotelMain />} />
                  <Route path="/user/hotel/:hotelId/info" element={<HotelProfileInfo />} />
                  <Route path="/user/hotel/:hotelId/dates" element={<HotelDates />} />
                  <Route path="/user/hotel/:hotelId/bookings" element={<HotelBookings />} />
                  <Route path="/user/hotel/:hotelId/rooms" element={<HotelRoom />} />
                  <Route path="/user/hotel/:hotelId/rooms/register" element={<RoomRegister />} />
                  <Route path="/user/hotel/:hotelId/facilities" element={<HotelProfileFacilities />} />
                  <Route path="/user/hotel/:hotelId/image" element={<HotelProfileImages />} />
                  <Route path="/user/hotel/:hotelId/address" element={<HotelProfileAddress />} />
                  {/*<Route path="/user/hotel/:hotelId/rooms/:roomsId/dates" element={<RoomsDates />} />*/}
                  <Route path="/user/hotel/:hotelId/rooms/:roomsId/booking" element={<RoomsBooking />} />
                  <Route path="/user/hotel/:hotelId/rooms/:roomsId/info" element={<RoomsProfileInfo />} />
                  <Route path="/user/hotel/:hotelId/rooms/:roomsId/facilities" element={<RoomsProfileFacilities />} />
                  {/*<Route path="/user/hotel/:hotelId/booking" element={<RoomsProfileFacilities />} />*/}
                  <Route path="/user/bookings" element={<UserBookings />} />
                  <Route path="/user/bookings/active/:bookingId" element={<BookingDetails />} />
                  <Route path="/user/bookings/archived/:bookingId" element={<BookingArchived />} />
                  <Route path="/user/saved" element={<Saved />} />
              </Route>

              <Route path="/hotels" element={<SearchList/>}/>
              <Route path="/hotels/:hotelId" element={<Hotel/>}/>
              <Route path="/hotels/:hotelId/booking" element={<BookingConfirmation/>}/>
              <Route path="/hotels/booking/payment/success/:bookingId" element={<PaymentSuccess/>}/>/
              <Route path="/hotels/booking/payment/cancel/:bookingId" element={<PaymentCancelled/>}/>
              <Route path="/hotels/booking/payment/error/:bookingId" element={<PaymentError/>}/>
          </Routes>
      </BrowserRouter>
  );
}

export default App;
