import './login.css';
import React, {useState} from 'react';
import Navbar from "../../../../../components/navbar/Navbar";
import { validEmail } from "../../utils/inputValidation";
import {post} from "../../../../../clients";
import jwt_decode from "jwt-decode";
import {useLocation, useNavigate} from "react-router-dom";
import MailList from "../../../../../components/mailList/MailList";
import Footer from "../../../../../components/footer/Footer";

function Login(props) {
    const navigate = useNavigate();
    const location = useLocation();
    // const [email, setEmail] = useState("admin@hb.com");
    // const [password, setPassword] = useState("admin_user_password");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [warning, setWarning] = useState({});
    const [error, setError] = useState("");


    const register = () => {
        setError("")
        var newWarning = {
            "emptyEmail": email.length === 0,
            "shortPassword": password.length < 8,
            "invalidEmail": email.length !== 0 && !validEmail(email)
        }

        setWarning(newWarning);
        for (let key of Object.keys(newWarning)){
            if (newWarning[key]){
                return;
            }
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
                if (data.errorMessage.length > 0){
                    setError(data.errorMessage);
                    return;
                }
                console.log(data)

                const jwt = data.jwt;
                const decoded = jwt_decode(jwt)
                localStorage.setItem("firstname", decoded.sub)
                localStorage.setItem("jwt", jwt);
                if (location?.state?.from != null){
                    console.log(location.state.from)
                    navigate(location.state.from, {state: location.state.state});
                } else {

                    navigate("/")
                }
            })
            .catch(error =>{
                console.log(error);
            }).finally( () => {
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
                                if (warning.emptyEmail && e.target.value.length > 0){
                                    setWarning({...warning, ["emptyEmail"]: false})
                                } else if (warning.invalidEmail && validEmail(e.target.value)){
                                    setWarning({...warning, ["invalidEmail"]: false})
                                }
                                setEmail(e.target.value);
                            }}
                            required />
                    </div>
                    { warning.emptyEmail &&
                        <label className="validationLabel">Please enter the registered email.</label>
                    }
                    {   warning.invalidEmail &&
                        <label className="validationLabel">Email is invalid!</label>
                    }
                    <div className="formItem">
                        <label className="formLabel">Password</label>
                        <input
                            placeholder="Enter Password"
                            type="password"
                            maxLength="30"
                            onChange = {e => {
                                if (warning.shortPassword && e.target.value.length >= 8){
                                    setWarning({...warning, ["shortPassword"]: false})
                                }
                                setPassword(e.target.value)
                            }}
                            required />
                    </div>
                    {   warning.shortPassword &&
                        <label className="validationLabel">Too short password! (minimum 8)</label>
                    }


                    <div className="formItem">
                        <button className="registerbtn" onClick={register}>Login</button>
                    </div>
                    {
                        error.length > 0 &&
                        <label className="validationLabel">Authentication failed.</label>
                    }
                    <div className="container signin">
                        <button
                            className="navUserRegisterBtn"
                            onClick={() => {
                                navigate("/user/register", {state: location.state})
                            }}>Create a new account.</button>
                        {/*<p><a href="http://ec2-35-171-6-79.compute-1.amazonaws.com/user/register">Create a new account.</a></p>*/}
                    </div>
                </div>
            </div>
            <MailList/>
            <Footer/>
        </div>
    );
}

export default Login;