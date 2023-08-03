import './personalSecurity.css'
import React, {useState} from 'react';
import ProfileSidebar from "../ProfileSidebar";
import {postWithJwt} from "../../../../../../clients";

function PersonalSecurity(props) {
    const [prevPassword, setPrevPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [newPassword2, setNewPassword2] = useState("");
    const [openWarnings, setOpenWarnings] = useState(
        { confirm: false, minLength: false, wrongPassword: false }
    )

    function savePassword(){
        var newOpenWarnings = {
            confirm: newPassword !== newPassword2,
            minLength: newPassword.length < 8
        }
        for (let key of Object.keys(newOpenWarnings)){
            if (newOpenWarnings[key]){
                setOpenWarnings(newOpenWarnings)
                return;
            }
        }

        var payload = {
            prevPassword: prevPassword,
            newPassword: newPassword
        }

        postWithJwt(`/api/v1/user/password`, payload)
            .then(response => response.json())
            .then(data => {
                if (!data.passwordMatch){
                    setOpenWarnings({...openWarnings, ["wrongPassword"]: true})
                    return;
                }
                if (data.success){
                    //
                }
            })
            .catch(e => {
                console.error(e)})
            .finally(() => {

            })
    }

    return (
        <div>
            <div className="profileTitle">
                <h2>Change password</h2>
                <div className="userPasswordItem">
                <label>Previous password</label>
                <input
                    type="password"
                    value={prevPassword}
                    placeholder={"Enter previous password."}
                    onChange={e=>{
                        setOpenWarnings({...openWarnings, ["wrongPassword"]: false})
                        setPrevPassword(e.target.value)
                    }}
                />
                </div>

                { openWarnings.wrongPassword &&
                    <div>
                        <span className="userPasswordWarning">Wrong password.</span><br/>
                    </div>
                }
                <div className="userPasswordItem">
                <label>New password</label>
                <input  type="password"
                        value={newPassword}
                        maxLength="20"
                        placeholder={"Enter new password."}
                        onChange={e=>{
                            if (e.target.value.length >= 8) {
                                setOpenWarnings({...openWarnings, ["confirm"]: false, ["minLength"]: false})
                            } else {
                                setOpenWarnings({...openWarnings, ["confirm"]: false})
                            }
                            setNewPassword(e.target.value)
                        }}
                />
                </div>

                { openWarnings.minLength &&
                    <div>
                        <span className="userPasswordWarning">New password is too short (min Length: 8)</span><br/>
                    </div>
                }
                <div className="userPasswordItem">
                <label>New password confirmation</label>
                <input  type="password"
                        value={newPassword2}
                        maxLength="20"
                        placeholder={"Enter new password again."}
                        onChange={e=>{
                            setOpenWarnings({...openWarnings, ["confirm"]: false})
                            setNewPassword2(e.target.value)
                        }}
                />
                </div>
                { openWarnings.confirm &&
                    <div>
                        <span className="userPasswordWarning">New passwords do not match.</span> <br/>
                    </div>
                }
                <button
                    className="userPasswordSaveBtn"
                    onClick={savePassword}
                >Submit</button>
            </div>
        </div>
    );
}

export default PersonalSecurity;