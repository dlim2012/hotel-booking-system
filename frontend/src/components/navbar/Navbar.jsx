import "./navbar.css"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

import { useNavigate, useLocation } from "react-router-dom";
import {useState} from "react";
import image from '../../assets/Default_pfp.svg.png'
import {faBed, faUser} from "@fortawesome/free-solid-svg-icons";
import {useRef} from 'react';
import Demo from "../demo/Demo";
import {deleteWithJwt} from "../../clients";

const Navbar = () => {
  const navigate= useNavigate();
  const location = useLocation();
  const [open, setOpen] = useState(false);
  let menuRef = useRef();

  const handleRegister = () => {
    navigate("/user/register", { state: {from: location.pathname} });
  };
  const handleLogin = () => {
      navigate("/user/login", {state: {from: location.pathname} })
  }
  const handleOpen = () => {
      setOpen(!open)
  }

  const handleSignOut = () => {
    if (localStorage["test-user"]){
      deleteWithJwt('/api/v1/user/delete')
          .catch(e => {
            console.error(e)})
      localStorage.removeItem("test-user");
    }

    localStorage.removeItem('jwt');
    localStorage.removeItem("firstname");

    if (location.pathname.startsWith("/user/")){
      navigate("/");
    } else{
      window.location.reload();
    }
    // navigate(location?.state?.from === undefined ? '/' : location.state.from, {state: location.state});
    // window.location.reload(false);
    // navigate(location.state == null || location.state.from == null ? '/' : location.state.from)
  }

  const navProfile = () => {
    navigate("/user/profile")
  }

  const navBookings = () => {
    console.log("navigating to bookings.")
    navigate("/user/bookings")
  }

  const navSaved = () => {
    navigate("/user/saved");
  }

  const navHotelManagement = () => {
    navigate("/user/hotel")
  }

  const handleLogoClick= () => {
    console.log("logo click");
    navigate("/")
  }

  return (
  <div className="demoContainer">
    {/*<Demo />*/}
    <div className="navbar">
      <div className="navContainer">
        <span className="logo" onClick={handleLogoClick}>Hotel Booking</span>
        { localStorage["jwt"] == null ?
        <div className="navItems">
          <button className="navButton" onClick={handleRegister}>Register</button>
          <button className="navButton" onClick={handleLogin}>Login</button>
        </div> :
        <div className="navItems">

            <div className='menu-container' ref={menuRef}>
              <div className='menu-trigger' onClick={handleOpen}>
                <img src={image} />
              </div>
              { open &&
              <div className="dropdown-menu" >
                {/*<h3>{localStorage.getItem("firstname")}</h3>*/}
                <ul>
                  <DropdownItem icon = {faUser} text = {"Manage Account"} handle={navProfile}/>
                  <DropdownItem icon = {faUser} text = {"Bookings"} handle={navBookings}/>
                  <DropdownItem icon = {faUser} text = {"Saved"} handle={navSaved}/>
                  <DropdownItem icon = {faUser} text = {"Manage Hotels"} handle={navHotelManagement}/>
                  <DropdownItem icon = {faUser} text = {"Sign out"} handle={handleSignOut}/>
                </ul>
              </div>
              }
            </div>
        </div>
        }
      </div>
    </div>
  </div>
  )
}

function DropdownItem(props){
  return(
      <li className="dropdownItem" onClick={props.handle}>
        <div className="dropdownIcon">
          <FontAwesomeIcon icon={props.icon} /></div>
        <a>{props.text}</a>
      </li>
  )
}

export default Navbar