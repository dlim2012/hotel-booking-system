import React from 'react';
import {Navigate, Outlet, useLocation} from "react-router-dom";

function ProtectedRoutes(props) {
    const location = useLocation();
    return (localStorage.getItem('jwt') == null) ?
        <Navigate to="/user/register"  replace state={{from: location}}/>
        : <Outlet />;
}

export default ProtectedRoutes;