import React from 'react';
import Navbar from "../../../../../../components/navbar/Navbar";
import {deleteWithJwt} from "../../../../../../clients";
import {useNavigate} from "react-router-dom";

function UserSettings(props) {
    const navigate = useNavigate();

    function onDeleteSubmit(){
        deleteWithJwt('/api/v1/user/delete')
            .then(() => {

                localStorage.removeItem('jwt');
                localStorage.removeItem("firstname");
                localStorage.removeItem("test-user");
                navigate('/')
            })
            .catch(e => {
                console.error(e)})
    }

    return (
        <div>
            <button
                onClick={onDeleteSubmit}
            >Delete account</button>

        </div>
    );
}

export default UserSettings;