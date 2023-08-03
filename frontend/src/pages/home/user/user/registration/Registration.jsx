import Navbar from "../../../../../components/navbar/Navbar";
import './registration.css'
import {format} from "date-fns";
import {DateRange} from "react-date-range";
import {useLocation, useNavigate} from "react-router-dom";
import {useState} from "react";
import {post, getWithJwt} from "../../../../../clients";
import jwt_decode from 'jwt-decode'
import { validEmail } from "../../utils/inputValidation";
import Cookies from "universal-cookie"
// import '../../../../node_modules/bootstrap/dist/css/bootstrap.min.css';
import axios from "axios";
import MailList from "../../../../../components/mailList/MailList";
import Footer from "../../../../../components/footer/Footer";

function Registration() {
    const navigate = useNavigate();
    const location = useLocation();
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [password2, setPassword2] = useState("");
    // const [firstName, setFirstName] = useState("");
    // const [lastName, setLastName] = useState("");
    // const [email, setEmail] = useState("");
    // const [password, setPassword] = useState("");
    // const [password2, setPassword2] = useState("");
    const [error, setError] = useState(false)
    const [emailError, setEmailError] = useState(false);



    const register = () => {
        setEmailError(!validEmail(email));
        if (emailError
            || firstName.length === 0
            || lastName.length === 0
            || password !== password2
        ){
            setError(true);
            return;
        }
        var path = '/api/v1/user/register'
        // var path = 'http://localhost/api/v1/user/register'
        var payload = {
            'firstName': firstName,
            'lastName': lastName,
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
                try{
                    if (location.state.from != null){
                        navigate(location.state.from);
                    }
                } catch (e){
                } finally {
                    navigate("/");
                }
            })
            .catch(error =>{
                console.log(error);
            })
    }

    return (
    <div className="registration">
        <Navbar />
        <div className="formContainer">
            <div className="formWrapper">
                <h1 className="formTitle">Create your account</h1>
            </div>

            <div className="formList">
                <div className="formItem">
                    <label className="formLabel">First Name</label>
                    <input
                        placeholder="Enter first name"
                        type="text"
                        maxLength="50"
                        onChange = {e => {setFirstName(e.target.value)}}
                    />
                </div>
                {   firstName.length === 0 && error &&
                    <label className="validationLabel">First name can't be empty!</label>
                }
                <div className="formItem">
                    <label className="formLabel">Last Name</label>
                    <input
                        placeholder="Enter last name"
                        type="text"
                        required
                        maxLength="50"
                        onChange = {e => {setLastName(e.target.value)}}
                    />
                </div>
                {   lastName.length === 0 && error &&
                    <label className="validationLabel">Last name can't be empty!</label>
                }

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
                    <label className="formLabel">Repeat Password</label>
                    <input
                        placeholder="Repeat password"
                        type="password"
                        maxLength="30"
                        onChange = {e => {setPassword2(e.target.value)}}
                        required />
                </div>
                {   password !== password2 && error &&
                    <label className="validationLabel">Passwords don't match!</label>
                }

                <div className="formItem">
                    <button className="registerbtn" onClick={register}>Register</button>
                </div>
                <div className="container signin">
                    <p>Already have an account? <a href="http://ec2-35-171-6-79.compute-1.amazonaws.com/user/login">Sign in</a>.</p>
                </div>
            </div>
        </div>
        <MailList/>
        <Footer/>
    </div>
  );
}

export default Registration;