import React from 'react';
import './profileSidebar.css'
import login from "../login/Login";

function ProfileSidebar(props) {

    console.log(props)
    const setSubject = props.args.setSubject;
    const subject = props.args.subject;

    return (
        <div className="profileSidebar">
            <ul className="profileSidebarList">
                <li className="profileSidebarItem">
                    <button
                        className={"profileSidebarBtn"}
                        onClick={e=>{setSubject("personal-details")}}
                    >Personal details</button>
                </li>
                <li className="profileSidebarItem">
                    <button
                        className={"profileSidebarBtn"}
                        onClick={e=>{setSubject("security")}}
                    >Security</button>
                </li>
                <li className="profileSidebarItem">
                    <button
                        className={"profileSidebarBtn"}
                        onClick={e=>{setSubject("email-notifications")}}
                    >Email notifications</button>
                </li>
            </ul>
        </div>
    );
}

export default ProfileSidebar;