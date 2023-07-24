import React, {useState} from 'react';
import Navbar from "../../../../../components/navbar/Navbar";
import { validEmail } from "../../utils/inputValidation";
import {post} from "../../../../../clients";
import jwt_decode from "jwt-decode";
import {useLocation, useNavigate} from "react-router-dom";
import './login.css';
import '../registration/registration.css'

function Login(props) {
    const navigate = useNavigate();
    const location = useLocation();
    const [email, setEmail] = useState("admin@hotel-booking.com");
    const [password, setPassword] = useState("admin_user_password");
    const [error, setError] = useState(false)
    const [emailError, setEmailError] = useState(false);



    const register = () => {
        setEmailError(!validEmail(email));
        if (emailError || password.length < 1
        ){
            setError(true);
            return;
        }
        var path = '/api/v1/user/login'
        // var path = 'http://localhost/api/v1/user/register'
        var payload = {
            'email': email,
            'password': password
        }
        // axios.post(path, payload).then(response => console.log(response))
        post(path, payload)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                const jwt = data.jwt;
                const decoded = jwt_decode(jwt)
                localStorage.setItem("firstname", decoded.sub)
                localStorage.setItem("jwt", jwt);
            })
            .catch(error =>{
                console.log(error);
            }).finally( () => {
                if (location.state.from != null){
                    if (location.state.from.substring(0, 8) === "/hotels/") {
                        console.log("location", location)
                        console.log(location.state.from)
                        navigate(location.state.from, {state: location.state.state});
                        return;
                    }
                }
                navigate("/")
            })


        }


    return (
        <div className="registration">
            <Navbar />
            <div className="formContainer">
                <div className="formWrapper">
                    <h1 className="formTitle">Login</h1>
                </div>
                <div className="formList">
                    <div className="formItem">
                        <label className="formLabel">Email</label>
                        <input
                            placeholder="Enter email"
                            type="email"
                            maxLength="100"
                            onChange = {e => {
                                setEmail(e.target.value); setEmailError(!validEmail(e.target.value));
                            }}
                            required />
                    </div>
                    {   emailError && error &&
                        <label className="validationLabel">Email is invalid!</label>
                    }
                    <div className="formItem">
                        <label className="formLabel">Password</label>
                        <input
                            placeholder="Enter Password"
                            type="password"
                            maxLength="30"
                            onChange = {e => {
                                setPassword(e.target.value)}}
                            required />
                    </div>
                    {   password.length < 8 && error &&
                        <label className="validationLabel">Too short password! (minimum 8)</label>
                    }


                    <div className="formItem">
                        <button className="registerbtn" onClick={register}>Login</button>
                    </div>
                    <div className="container signin">
                        <p><a href="#">Create a new account.</a></p>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Login;