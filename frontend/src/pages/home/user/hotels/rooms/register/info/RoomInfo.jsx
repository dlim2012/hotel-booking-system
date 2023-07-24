import React from 'react';
import {bedsMap, propertyTypesMap} from "../../../../../../../assets/Lists";
import {TimePickerComponent} from "@syncfusion/ej2-react-calendars";


function Roominfo(props) {
    const {info, setInfo, bedInfo, setBedInfo} = props;

    return (

        <div>
             <div className="formItem">
                 <label className="formLabel">Display Name</label>
                 <input
                    value={info["displayName"]}
                    onChange={e => setInfo({...info, ["displayName"]: e.target.value})}
                 />
             </div>
            <div className="formItem">
                <label className="formLabel">Short Name</label>
                <input
                    value={info["shortName"]}
                    onChange={e => setInfo({...info, ["shortName"]: e.target.value})}
                />
            </div>
                 <div className="formItem">
                     <label className="formLabel">Description</label>
                     <input
                         value={info["description"]}
                         onChange={e => setInfo({...info, ["description"]: e.target.value})}
                     />
                 </div>
                <div className="formItem">
                    <label className="formLabel">Number of beds</label>
                    {
                        bedsMap.map((item, index) => {
                            return (
                                <div className="formItemSub">
                                    <label className="formLabel">{item}</label>
                                    <input
                                        type="number"
                                        min="0"
                                        max="1000"
                                        value={bedInfo[item] == null ? 0 : bedInfo[item]}
                                        onChange={e => setBedInfo({...bedInfo, [item]: e.target.value})}
                                    />
                                </div>
                            );
                        })
                    }
                </div>
                 <div className="formItem">
                     <label className="formLabel">Maximum adults</label>
                     <input
                         type="number"
                         min="0"
                         max="1000"
                         value={info["maxAdult"]}
                         onChange={e => setInfo({...info, ["maxAdult"]: e.target.value})}
                         />
                 </div>
                 <div className="formItem">
                     <label className="formLabel">Maximum children</label>
                     <input
                         type="number"
                         min="0"
                         max="1000"
                         value={info["maxChild"]}
                         onChange={e => setInfo({...info, ["maxChild"]: e.target.value})}
                         />
                 </div>
                 <div className="formItem">
                     <label className="formLabel">Quantity</label>
                     <input
                         type="number"
                         min="0"
                         max="1000"
                         value={info["quantity"]}
                         onChange={e => setInfo({...info, ["quantity"]: e.target.value})}
                     />
                 </div>
                <div className="formItem">
                    <label className="formLabel">Min price</label>
                    <input
                        type="number"
                        step="0.01"
                        min="0.00"
                        value={info["priceMin"]}
                        onChange={e => setInfo({...info, ["priceMin"]: e.target.value})}
                    />
                </div>
                <div className="formItem">
                 <label className="formLabel">Max price</label>
                 <input
                     type="number"
                     step="0.01"
                     min="0.00"
                     value={info["priceMax"]}
                     onChange={e => setInfo({...info, ["priceMax"]: e.target.value})}
                 />
                </div>
            <div className="formItem">
                <label className="formLabel">Free cancellation days</label>
                <input
                    type="number"
                    min="0"
                    max="1000"
                    value={info["freeCancellationDays"]}
                    onChange={e => setInfo({...info, ["freeCancellationDays"]: e.target.value})}
                />
            </div>
            <div className="formItem">
                <label className="formLabel">No prepayment days</label>
                <input
                    type="number"
                    min="0"
                    max="1000"
                    value={info["noPrepaymentDays"]}
                    onChange={e => setInfo({...info, ["noPrepaymentDays"]: e.target.value})}
                />
            </div>
                <div className="formItem">
                    <label className="formLabel">Check-out time</label>
                    <input
                        type="time"
                        value = {info["checkOutTime"]}
                        onChange={e => setInfo({...info, ["checkOutTime"]: e.currentTarget.value})}
                    />
                </div>
                 <div className="formItem">
                     <label className="formLabel">Check-in time</label>
                     <input
                         type="time"
                         value = {info["checkInTime"]}
                         onChange={e => setInfo({...info, ["checkInTime"]: e.currentTarget.value})}
                     />
                 </div>
            <div className="formItem">
                <label className="formLabel">Activate</label>
                <input
                    type="checkbox"
                    value = {info["isActive"]}
                    onChange={e => setInfo({...info, ["isActive"]: e.target.checked})}
                />
            </div>
            { info["isActive"] &&
                <div className="formItem">
                    <label className="formLabel">First active date</label>
                    <input
                        type="date"
                        value = {info["availableFrom"]}
                        onChange={e => setInfo({...info, ["availableFrom"]: e.target.value})}
                    />
                </div>
            }
            { info["isActive"] &&
                <div className="formItem">
                    <label className="formLabel">Last active date</label>
                    <input
                        type="date"
                        value = {info["availableUntil"]}
                        onChange={e => setInfo({...info, ["availableUntil"]: e.target.value})}
                    />
                </div>
            }

        </div>
    );
            }

export default Roominfo;