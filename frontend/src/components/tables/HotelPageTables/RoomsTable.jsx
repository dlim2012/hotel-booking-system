import React, {useState} from 'react';
import {useNavigate} from "react-router-dom";

function RoomsTable(props) {
    const navigate = useNavigate();

    const item = props.item;
    const date = props.date;
    const options = props.options;
    const hotelInfo = props.hotelInfo;
    const recommended = item.roomsList;
    const roomsInfoList = hotelInfo.roomsInfoList;
    const prices = props.prices;
    const roomSelection = props.roomSelection;
    const setRoomSelection = props.setRoomSelection;
    const searchItem = props.searchItem;


    if (item == null || recommended == null || options == null || date == null || hotelInfo == null
    || roomsInfoList == null || prices == null || roomSelection == null || setRoomSelection == null
    ){
        return;
    }

    var dates = Math.round((date[0].endDate.getTime() - date[0].startDate.getTime()) / 86400000);
    var numRoomsSelected = 0
    var totalPrice = 0
    for (let key in roomSelection){
        var numRoom = parseInt(roomSelection[key]);
        numRoomsSelected += numRoom;
        totalPrice += numRoom * prices[key]
    }
    console.log(numRoomsSelected, totalPrice)



    return (
        <div>
            <table id="availabilityTable">
                <tr>
                    <th>Room Type</th>
                    <th>Price for {dates} night(s)</th>
                    <th>Your Choices</th>
                    <th>Select</th>
                    <th></th>
                </tr>
                {
                    roomsInfoList.map((info, index) => {
                        return (
                            <tr>
                                <td>
                                    <div className="roomName">{info.displayName}</div>
                                    <div className="roomBeds">(beds)</div>
                                    <div className="roomFacilities">
                                        {info.facilityList.map((facility, index) => {
                                            return <div>{facility}</div>
                                        })}
                                    </div>
                                </td>
                                <td>
                                    <div>
                                        Max {info.maxAdult} adult(s), {info.maxChild} child(ren)
                                    </div>
                                    <div>
                                        Price: ${prices[info.id] / 100}
                                    </div>
                                    <div>
                                        Tax included
                                    </div>
                                </td>
                                <td>
                                    <div>
                                        Free cancellation before ()
                                    </div>
                                    <div>
                                        Prepayment ()
                                    </div>
                                    <div>
                                        No prepayment needed - pay at the property
                                    </div>
                                </td>
                                <td>
                                    <div>
                                        <select
                                            value={roomSelection[info.id]}
                                            onChange={event => {setRoomSelection({...roomSelection, [info.id]: event.target.value})}}
                                       >
                                            <option value="0">0 </option>
                                            <option value="1">1 (${prices[info.id] / 100}) </option>
                                            {info.quantity >= 2 && <option value="2">2 (${prices[info.id] / 100 * 2}) </option>}
                                            {info.quantity >= 3 && <option value="3">3 (${prices[info.id] / 100 * 3}) </option>}
                                            {info.quantity >= 4 && <option value="4">4 (${prices[info.id] / 100 * 4}) </option>}
                                            {info.quantity >= 5 && <option value="5">5 (${prices[info.id] / 100 * 5}) </option>}
                                            {info.quantity >= 6 && <option value="6">6 (${prices[info.id] / 100 * 6}) </option>}
                                            {info.quantity >= 7 && <option value="7">7 (${prices[info.id] / 100 * 7}) </option>}
                                            {info.quantity >= 8 && <option value="8">8 (${prices[info.id] / 100 * 8}) </option>}
                                            {info.quantity >= 9 && <option value="9">9 (${prices[info.id] / 100 * 9}) </option>}


                                        </select>
                                    </div>
                                </td>
                                {index == 0 &&
                                    <td rowSpan={roomsInfoList.length}>
                                        {numRoomsSelected > 0 &&
                                            <div>{numRoomsSelected} room(s) for ${totalPrice / 100}</div>
                                        }
                                        <button onClick={onReserve}>I'll reserve</button>
                                        <ul>
                                            <li>Confirmation is immediate</li>
                                            <li>No booking or credit card fees!</li>
                                        </ul>
                                    </td>
                                }
                            </tr>
                        );
                    })
                }
            </table>
        </div>
    );
}

export default RoomsTable;