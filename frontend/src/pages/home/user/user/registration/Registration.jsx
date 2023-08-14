import './registration.css'
import Navbar from "../../../../../components/navbar/Navbar";
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
    const [warning, setWarning] = useState(false)
    const [error, setError] = useState("");

    console.log(location)

    const register = () => {
        setError("")
        var newWarning = {
            "emptyFirstName": firstName.length === 0,
            "emptyLastName": lastName.length === 0,
            "shortPassword": password.length < 8,
            "passwordMismatch": password !== password2,
            "invalidEmail": !validEmail(email)
        };
        console.log(newWarning)
        setWarning(newWarning);
        for (let key of Object.keys(newWarning)){
            if (newWarning[key]){
                return;
            }
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
                if (data.errorMessage.length > 0){
                    setError(data.errorMessage);
                    return;
                }
                console.log(data)
                const jwt = data.jwt;
                const decoded = jwt_decode(jwt)
                localStorage.setItem("firstname", decoded.sub)
                localStorage.setItem("jwt", jwt);
                if (location.state.from != null){
                    navigate(location.state.from, {state: location.state.state});
                } else {
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
                        onChange = {e => {
                            if (e.target.value.length > 0) {
                                setWarning({...warning, ["emptyFirstName"]: false});
                            }
                            setFirstName(e.target.value)
                        }}
                    />
                </div>
                {   warning.emptyFirstName &&
                    <label className="validationLabel">First name can't be empty!</label>
                }
                <div className="formItem">
                    <label className="formLabel">Last Name</label>
                    <input
                        placeholder="Enter last name"
                        type="text"
                        required
                        maxLength="50"
                        onChange = {e => {
                            if (e.target.value.length > 0) {
                                setWarning({...warning, ["emptyLastName"]: false});
                            }
                            setLastName(e.target.value)
                        }}
                    />
                </div>
                {   warning.emptyLastName &&
                    <label className="validationLabel">Last name can't be empty!</label>
                }

                <div className="formItem">
                    <label className="formLabel">Email</label>
                    <input
                        placeholder="Enter email"
                        type="email"
                        maxLength="100"
                        onChange = {e => {
                            if (validEmail(e.target.value)) {
                                setWarning({...warning, ["invalidEmail"]: false});
                            }
                            setEmail(e.target.value);
                        }}
                        required />
                </div>
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
                            if (e.target.value.length >= 8){
                                setWarning({...warning, ["shortPassword"]: false});
                            }
                            setPassword(e.target.value)
                        }}
                        required />
                </div>
                {   warning.shortPassword &&
                    <label className="validationLabel">Too short password! (minimum 8)</label>
                }

                <div className="formItem">
                    <label className="formLabel">Repeat Password</label>
                    <input
                        placeholder="Repeat password"
                        type="password"
                        maxLength="30"
                        onChange = {e => {
                            setWarning({...warning, ["passwordMismatch"]: false})
                            setPassword2(e.target.value)
                        }}
                        required />
                </div>
                {   warning.passwordMismatch &&
                    <label className="validationLabel">Passwords don't match!</label>
                }

                <div className="formItem">
                    <button className="registerbtn" onClick={register}>Register</button>
                </div>
                { error.length > 0 && <label className="validationLabel">{error}</label>}
                <div className="signin">
                    <p>Already have an account? <button className="navLoginBtn" onClick={
                        () => {
                            navigate("/user/login", {state: location.state})
                    }}
                    >Sign in</button> </p>
                    {/*<p>Already have an account?<a href="http://ec2-35-171-6-79.compute-1.amazonaws.com/user/login">Sign in</a>.</p>*/}
                </div>
            </div>
        </div>
        <MailList/>
        <Footer/>
    </div>
  );
}

export default Registration;