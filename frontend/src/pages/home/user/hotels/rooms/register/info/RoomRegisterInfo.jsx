import './roomRegisterInfo.css'
import {bedsMap, bedsMap2, MAX_BOOKING_DAYS, propertyTypesMap} from "../../../../../../../assets/Lists";
import {TimePickerComponent} from "@syncfusion/ej2-react-calendars";


function RoomRegisterInfo(props) {
    const {info, setInfo, bedInfo, setBedInfo, openWarnings, setOpenWarnings} = props;

    return (

        <div className="roomRegisterContainer">
            <div className="roomRegisterTopic">
                <h2>Names</h2>
                 <div className="roomRegisterFormItem">
                     <label className="formLabel">Display Name</label>
                     <input
                         type="text"
                        value={info["displayName"]}
                        onChange={e => {
                            if (e.target.value.length >= 5) {
                                setOpenWarnings({...openWarnings, ["displayName"]: false})
                            }
                            setInfo({...info, ["displayName"]: e.target.value})
                        }
                        }
                     />
                     {openWarnings.displayName &&
                     <span className="roomRegisterInfoWarning">Please enter a display name. (min length: 5)</span>
                     }
                 </div>
                <div className="roomRegisterFormItem">
                    <label className="formLabel">Short Name</label>
                    <input
                        type="text"
                        value={info["shortName"]}
                        maxLength="10"
                        onChange={e => {

                            if (e.target.value.length > 0) {
                                if (/\d/.test(e.target.value)) {
                                    setOpenWarnings({...openWarnings, ["shortNameEmpty"]: false, ["shortNameNumber"]: false})
                                } else {
                                    setOpenWarnings({...openWarnings, ["shortNameEmpty"]: false});
                                }
                            } else if (/\d/.test(e.target.value)){
                                    setOpenWarnings({...openWarnings, ["shortNameNumber"]: false})
                            }
                            setInfo({...info, ["shortName"]: e.target.value})
                        }}
                    />
                    {openWarnings.shortNameEmpty &&
                        <span className="roomRegisterInfoWarning">Please enter a short name.</span>
                    }
                    {openWarnings.shortNameNumber &&
                        <span className="roomRegisterInfoWarning">Short name shouldn't contain any digit.</span>
                    }
                </div>
            </div>
            <div className="roomRegisterTopic">
                <h2>Description</h2>
                {/*<span className="roomRegisterInfoNote">The description will be displayed on the hotel view page.</span>*/}
                 <div className="roomRegisterFormItem">
                     {/*<label className="formLabel">Description</label>*/}
                     <textarea
                         value={info["description"]}
                         onChange={e => setInfo({...info, ["description"]: e.target.value})}
                     />
                 </div>
            </div>

            <div className="roomRegisterTopic">
                <h2>Size</h2>
                <div className="roomRegisterTopicContents">
                <div className="roomRegisterTopicItem">
                    <label className="formLabel">Quantity</label>
                    <input
                        type="number"
                        min="1"
                        max="1000"
                        value={info["quantity"]}
                        onChange={e => setInfo({...info, ["quantity"]: e.target.value})}
                    />
                </div>
                 <div className="roomRegisterTopicItem">
                     <label className="formLabel">Maximum adults</label>
                     <input
                         type="number"
                         min="1"
                         max="1000"
                         value={info["maxAdult"]}
                         onChange={e => setInfo({...info, ["maxAdult"]: e.target.value})}
                         />
                 </div>
                 <div className="roomRegisterTopicItem">
                     <label className="formLabel">Maximum children</label>
                     <input
                         type="number"
                         min="0"
                         max="1000"
                         value={info["maxChild"]}
                         onChange={e => setInfo({...info, ["maxChild"]: e.target.value})}
                         />
                 </div>
                </div>
            </div>

            <div className="roomRegisterTopic">
                <h2>Beds</h2>
                <div className="roomRegisterTopicContents">
                    {/*<label className="formLabel">Number of beds</label>*/}
                    {/*<div className="roomRegisterTopicItem">*/}
                    {
                        Object.keys(bedsMap2).map((item, index) => {
                            // console.log("bedsMap2 ", item)
                            return (
                                <div className="roomRegisterTopicItem">
                                    <label className="formLabel">{bedsMap2[item]}</label>
                                    <input
                                        type="number"
                                        min="0"
                                        max="1000"
                                        value={bedInfo[item] == null ? 0 : bedInfo[item]}
                                        onChange={e => {
                                            setOpenWarnings({...openWarnings, ["numBed"]: false})
                                            setBedInfo({...bedInfo, [item]: e.target.value})}
                                        }
                                    />
                                </div>
                            );
                        })
                    }
                </div>
                {openWarnings.numBed &&
                    <span className="roomRegisterInfoWarning">Rooms should have at least one bed.</span>
                }
                {/*</div>*/}
            </div>
            <div className="roomRegisterTopic">
                <h2>Time</h2>
                <div className="roomRegisterTopicContents">
                <div className="roomRegisterTopicItem">
                    <label className="formLabel">Check-out time</label>
                    <input
                        type="time"
                        value = {info["checkOutTime"]}
                        onChange={e =>{
                            if (e.target.value <= info.checkInTime) {
                                setOpenWarnings({...openWarnings, ["timeOrder"]: false})
                            }
                            setInfo({...info, ["checkOutTime"]: e.currentTarget.value})
                        }}
                    />
                </div>
                <div className="roomRegisterTopicItem">
                    <label className="formLabel">Check-in time</label>
                    <input
                        type="time"
                        value = {info["checkInTime"]}
                        onChange={e => {
                            if (e.target.value >= info.checkOutTime) {
                                setOpenWarnings({...openWarnings, ["timeOrder"]: false})
                            }
                            setInfo({...info, ["checkInTime"]: e.currentTarget.value})
                        }}
                    />
                </div>
                </div>
                { openWarnings.timeOrder &&
                    <span className="roomRegisterInfoWarning">Check-In time has to be later than check-out time.</span>
                }
            </div>
            <div className="roomRegisterTopic">
                <h2>Price</h2>
                <div className="roomRegisterTopicContents">
                <div className="roomRegisterTopicItem">
                    <label className="formLabel">Minimum price</label>
                    <span>$
                    <input
                        type="number"
                        step="0.01"
                        min="0.00"
                        value={info["priceMin"]}
                        onChange={e => {
                            var priceMin = e.target.value === "0.00" && openWarnings.priceMin;
                            var priceOrder = e.target.value > info["priceMax"] && openWarnings.priceOrder;
                            setOpenWarnings({...openWarnings, ["priceMin"]: priceMin, ["priceOrder"]: priceOrder})
                            setInfo({...info, ["priceMin"]: e.target.value})
                        }
                        }
                    /></span><br/>
                    {openWarnings.priceMin &&
                        <span className="roomRegisterInfoWarning">Please set the minimum price bigger than $0.00.</span>
                    }
                    { openWarnings.priceMin && openWarnings.priceOrder && <br/>}
                    {openWarnings.priceOrder &&
                        <span className="roomRegisterInfoWarning">Maximum price is lower than minimum price</span>
                    }
                </div>
                <div className="roomRegisterTopicItem">
                 <label className="formLabel">Maximum price</label>
                <span>$
                 <input
                     type="number"
                     step="0.01"
                     min="0.00"
                     value={info["priceMax"]}
                     onChange={e => {
                         if (e.target.value !== "0.00") {
                             setOpenWarnings({...openWarnings, ["priceMax"]: false})
                         }
                         setInfo({...info, ["priceMax"]: e.target.value})
                     }}
                 /></span><br/>
                    {openWarnings.priceMax &&
                        <span className="roomRegisterInfoWarning">Please set the maximum price bigger than $0.00.</span>
                    }
                </div>

                </div>
            </div>
            <div className="roomRegisterTopic">
                <h2>Payment Options</h2>
                <span className="roomRegisterInfoNote">Payment will not be required or revertable before the following days from the reservation start date</span>
                <div className="roomRegisterTopicContents">
                    <div className="roomRegisterTopicItem">
                        <label className="formLabel">No prepayment days</label>
                        <input
                            type="number"
                            min="0"
                            max="1000"
                            value={info["noPrepaymentDays"]}
                            onChange={e => setInfo({...info, ["noPrepaymentDays"]: e.target.value})}
                        />
                    </div>
                    <div className="roomRegisterTopicItem">
                        <label className="formLabel">Free cancellation days</label>
                        <input
                            type="number"
                            min="0"
                            max="1000"
                            value={info["freeCancellationDays"]}
                            onChange={e => setInfo({...info, ["freeCancellationDays"]: e.target.value})}
                        />
                    </div>
                </div>
            </div>
            <div className="roomRegisterTopic">
            <div className="roomRegisterFormItem">
                <h2>Dates</h2>
                <span className="roomRegisterInfoNote">Note: Maximum {MAX_BOOKING_DAYS} days will be made available for reservations.</span>
                <div className="roomRegisterTopicContents">
                    <div className="roomRegisterTopicItem">
                    <label className="formLabel">Activate</label>
                        <input
                            type="checkbox"
                            value = {info["isActive"]}
                            onChange={e => setInfo({...info, ["isActive"]: e.target.checked})}
                        />
                    </div>
            { info["isActive"] &&
                    <div className="roomRegisterTopicItem">
                        <label className="formLabel">First active date</label>
                        <input
                            type="date"
                            value = {info["availableFrom"]}
                            onChange={e => {
                                if (e.target.value < info.availableUntil) {
                                    setOpenWarnings({...openWarnings, ["dateOrder"]: false})
                                }
                               setInfo({...info, ["availableFrom"]: e.target.value})}
                            }
                        />
                    </div>
            }
                    { info["isActive"] &&
                    <div className="roomRegisterTopicItem">
                        <label className="formLabel">Last active date</label>
                        <input
                            type="date"
                            value = {info["availableUntil"]}
                            onChange={e => {
                                if (info.availableFrom < e.target.value) {
                                    setOpenWarnings({...openWarnings, ["dateOrder"]: false})
                                }
                                setInfo({...info, ["availableUntil"]: e.target.value})}
                            }
                        />
                    </div>
                    }
                </div>
                { info["isActive"] && openWarnings.dateOrder &&
                    <span className="roomRegisterInfoWarning">No available dates.</span>
                }
            </div>
            </div>

        </div>
    );
            }

export default RoomRegisterInfo;