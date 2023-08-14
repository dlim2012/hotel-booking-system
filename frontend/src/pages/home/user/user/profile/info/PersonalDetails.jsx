import './personalDetails.css';
import Navbar from "../../../../../../components/navbar/Navbar";
import Header from "../../../../../../components/header/Header";
import React, {useEffect, useState} from "react";
import ProfileSidebar from "../ProfileSidebar";
import {getWithJwt, postWithJwt} from "../../../../../../clients";
import {TailSpin} from "react-loader-spinner";

const genderMap = {
    DEFAULT: "Select your gender",
    MALE: "Male",
    FEMALE: "Female",
    NONBINARY: "Non-binary",
    PREFERNOTTOMENTION: "Not selected"
}

function PersonalDetails(props) {
    var [fetchedData, setFetchedData] = useState({});
    var [updatedData, setUpdatedData] = useState({});
    var [edit, setEdit] = useState(false);
    var [fetching, setFetching] = useState(false);

    const fetchProfile = () => {
        setFetching(true);
        getWithJwt("/api/v1/user/profile")
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setFetchedData(data);
                var dataCopy = Object.assign({}, data);
                setUpdatedData(dataCopy);
            })
            .catch(e =>
                console.error(e)
            )
            .finally(() =>
                setFetching(false)
            )
    }

    const postProfile = () => {
        postWithJwt("/api/v1/user/profile/edit", updatedData)
            .catch(e => {
                console.error(e)}
            )
        // fetchProfile();
    }

    useEffect(() => {
        fetchProfile();
    }, [])

    var states = {
        "edit": edit,
        "setEdit": setEdit,
        "updatedData": updatedData,
        "setUpdatedData": setUpdatedData,
        "fetchedData": fetchedData,
        "setFetchedData": setFetchedData,
        "fetchProfile": fetchProfile
    }

    if (fetching){
        return (

            <div className="loading">
                <TailSpin
                    height="80"
                    width="80"
                    color="#0071c2"
                    ariaLabel="tail-spin-loading"
                    radius="1"
                    wrapperStyle={{}}
                    wrapperClass=""
                    visible={true}
                />
            </div>
        )
    }

    return (
        <div>
            <ul className="profileList">
                <ProfileItem
                    item={"Name"}
                    value={updatedData["firstName"] + " " + updatedData["lastName"]}
                    states = {states}
                    columnName={["firstName", "lastName"]}
                    inputs={
                        <div className="profileItemContainer">
                            <div className="profileItemInput">
                                <label>First name</label>
                                <input
                                    type="text"
                                    value={updatedData["firstName"]}
                                    onChange={e => {
                                        setUpdatedData(prevData => ({...prevData, firstName: e.target.value}))
                                    }}
                                ></input>
                            </div>
                            <div className="profileItemInput">
                                <label>Last name</label>
                                <input
                                    type="text"
                                    value={updatedData["lastName"]}
                                    onChange={e => {
                                        setUpdatedData(prevData => ({...prevData, lastName: e.target.value}))
                                    }}
                                ></input>
                            </div>
                        </div>
                    }
                />
                <ProfileItem
                    item={"Display name"}
                    value={fetchedData["displayName"] == null ? "Choose a display name" : fetchedData["displayName"]}
                    states = {states}
                    columnName={["displayName"]}
                    inputs={
                        <div className="profileItemContainer">
                            <div className="profileItemInput">
                                <label>Display name</label>
                                <input
                                    type="text"
                                    value={updatedData["displayName"]}
                                    onChange={e => {
                                        setUpdatedData(prevData => ({...prevData, displayName: e.target.value}))
                                    }}
                                ></input>
                            </div>
                        </div>
                    }
                />
                <ProfileItem
                    item={"Email address"}
                    value={fetchedData["email"]}
                    states = {states}
                    columnName={["email"]}
                    inputs={
                        <div className="profileItemContainer">
                            <div className="profileItemInput">
                                <label>Email address</label>
                                <input
                                    type="text"
                                    value={updatedData["email"]}
                                    onChange={e => {
                                        setUpdatedData(prevData => ({...prevData, email: e.target.value}))
                                    }}
                                ></input>
                            </div>
                        </div>
                    }
                />
                <ProfileItem
                    item={"Phone number"}
                    value={fetchedData["phoneNumber"] == null ? "Add your phone number" : fetchedData["phoneNumber"]}
                    states = {states}
                    columnName={["phoneNumber"]}
                    inputs={
                        <div className="profileItemContainer">
                            <div className="profileItemInput">
                                <label>Phone number</label>
                                <input
                                    type="text"
                                    value={updatedData["phoneNumber"]}
                                    onChange={e => {
                                        setUpdatedData(prevData => ({...prevData, phoneNumber: e.target.value}))
                                    }}
                                ></input>
                            </div>
                        </div>
                    }
                />
                <ProfileItem
                    item={"Date of birth"}
                    value={fetchedData["year"] == null ? "Add date of birth" : fetchedData["year"] + "/" + fetchedData["month"] + "/" + fetchedData["day"]}
                    states = {states}
                    columnName={["month", "day", "year"]}
                    inputs={
                        <div className="profileItemContainer">
                                <div className="profileItemInput">
                                    <label>Month</label>
                                    <select
                                        value={updatedData["month"]}
                                        onChange={e => {
                                            console.log(e)
                                            setUpdatedData(prevData => ({...prevData, month: e.target.value}))
                                        }}
                                    >
                                        <option value="default">Month</option>
                                        <option value="1">January</option>
                                        <option value="2">Febraury</option>
                                        <option value="3">March</option>
                                        <option value="4">April</option>
                                        <option value="5">May</option>
                                        <option value="6">June</option>
                                        <option value="7">July</option>
                                        <option value="8">August</option>
                                        <option value="9">September</option>
                                        <option value="10">October</option>
                                        <option value="11">November</option>
                                        <option value="12">December</option>
                                    </select>
                                </div>
                                <div className="profileItemInput">
                                    <label>Day</label>
                                    <label class="dobInput">
                                        <input
                                            type="text"
                                            value={updatedData["day"]}
                                            onChange={e => {
                                                setUpdatedData(prevData => ({...prevData, day: e.target.value}))
                                            }}
                                            placeholder="DD"></input>
                                    </label>
                                </div>
                                <div className="profileItemInput">
                                    <label>Year</label>
                                    <input
                                        className="dobInput"
                                        type="text"
                                        value={updatedData["year"]}
                                        onChange={e => {
                                            setUpdatedData(prevData => ({...prevData, year: e.target.value}))
                                        }}
                                        placeholder="YYYY"></input>
                                </div>
                        </div>}
                />
                {/*<ProfileItem*/}
                {/*    item={"Nationality"}*/}
                {/*    value={"Value"} />*/}
                <ProfileItem
                    item={"Gender"}
                    value={fetchedData["gender"] == null ? "Select your gender" : genderMap[fetchedData["gender"]]}
                    states = {states}
                    columnName={["gender"]}
                    inputs={
                        <div className="profileItemContainer">
                            <div className="profileItemInput">
                                <label>Gender</label>
                                <select
                                    value={updatedData["gender"]}
                                    onChange={e => {
                                        console.log(e.target)
                                        setUpdatedData(prevData => ({...prevData, gender: e.target.value}))
                                    }}>
                                    <option value="DEFAULT">Select your gender</option>
                                    <option value="MALE">I'm a man</option>
                                    <option value="FEMALE">I'm a woman</option>
                                    <option value="NONBINARY">I'm a non-binary</option>
                                    <option value="PREFERNOTTOMENTION">I prefer not to say</option>
                                </select>
                            </div>
                        </div>}
                />
                {/*<ProfileItem*/}
                {/*    item={"Address"}*/}
                {/*    value={"Value"} />*/}
            </ul>

        </div>
    );
}

function ProfileItem (props){
    var [editItem, setEditItem] = useState(false);
    var columns = props.columnName;

    const handleEdit = () => {
        // revert updates
        props.states.setEdit(!props.states.edit);

        props.states.setUpdatedData(props.states.fetchedData)
        // for (var col in columns) {
        //     props.states.setUpdatedData(prevData => {
        //         var newData = {...prevData}
        //         newData[col] = props.states.fetchedData[col];
        //         return newData;
        //     })
        // }
        if (props.states.edit === editItem){
            props.states.setEdit(!props.states.edit);
            setEditItem(!editItem)
        }
    };

    const handleSave = () => {
        console.log(props.states.updatedData);
        console.log(columns)
        // todo: validate data
        var payload = {};
        for (var key in columns){
            var col = columns[key];
            payload[col] = props.states.updatedData[col];
            console.log(col, props.states.updatedData)
        }
        console.log(payload);
        postWithJwt("/api/v1/user/profile/edit", payload)
            .catch(e => console.log(e))
            .finally(() =>
            {
                props.states.setEdit(false);
                setEditItem(false)
                props.states.fetchProfile()
            });
    }

    return (
        <li className="profileItem">
            <div className="profileItemRow">
                <div className="profileItemCol" onClick={handleEdit}>
                    <div className="profileKey">
                        {props.item}
                    </div>
                    {!editItem &&
                    <div className="profileValue">
                        {props.value}
                    </div>
                    }
                </div>
                {props.states.edit  && !editItem &&
                    <button className="editBtn2">
                        Edit
                    </button>
                }
                {!props.states.edit &&
                    <button className="editBtn1" onClick={handleEdit}>
                        Edit
                    </button>
                }
            </div>
            { editItem &&
                <div className="profileItemRow2">
                    {props.inputs}
                    <div className="onEditBtns">
                        <button
                            className="cancelBtn onEditBtn"
                            onClick={handleEdit}>Cancel</button>
                        <button
                            className="saveBtn onEditBtn"
                            onClick={handleSave}>Save</button>
                    </div>
                </div>
            }
        </li>
    )
}


export default PersonalDetails;