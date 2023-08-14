import './HotelDates.css'
import Navbar from "../../../../../components/navbar/Navbar";
import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {deleteWithJwt, getWithJwt, postWithJwt, putWithJwt} from "../../../../../clients";
// import 'react-calendar-timeline/lib/Timeline.css'

import Timeline from 'react-calendar-timeline'
import CustomTimeLine from './CustomTimeline'
// make sure you include the timeline stylesheet or the timeline will not be styled

import moment from 'moment'
import generateFakeData from "./generate-fake-data";
import faker from "faker";
import randomColor from "randomcolor";
import booking from "../../../search-list/hotel/booking/booking/BookingConfirmation";
import {MAX_BOOKING_DAYS} from "../../../../../assets/Lists";
import MailList from "../../../../../components/mailList/MailList";
import Footer from "../../../../../components/footer/Footer";
import {TailSpin} from "react-loader-spinner";

const dateDashFormat = (date) => {
    var getYear = date.toLocaleString("default", { year: "numeric" });
    var getMonth = date.toLocaleString("default", { month: "2-digit" });
    var getDay = date.toLocaleString("default", { day: "2-digit" });
    return getYear + "-" + getMonth + "-" + getDay;
}


var keys = {
    groupIdKey: "id",
    groupTitleKey: "title",
    groupRightTitleKey: "rightTitle",
    itemIdKey: "id",
    itemTitleKey: "title",
    itemDivTitleKey: "title",
    itemGroupKey: "group",
    itemTimeStartKey: "start",
    itemTimeEndKey: "end",
    groupLabelKey: "title"
};

var bgColors = {
    "AVAILABLE": 'rgba(0, 112, 224, 0.6)',
    "RESERVED": 'rgba(100, 112, 30, 0.6)',
    "RESERVED_FOR_TIMEOUT": 'rgba(150, 150, 150, 0.6)',
    "BOOKED": 'rgba(100, 0, 30, 0.6)',
    'OUT_OF_DATE': 'rgba(200, 0, 0, 0.6)'
}

var selectedBgColors = {
    "AVAILABLE": 'rgba(247,124,177,1)',
    "RESERVED": 'rgba(247,124,177,1)',
    "RESERVED_FOR_TIMEOUT": 'rgba(247,124,177,1)',
    "BOOKED": 'rgba(247,124,177,1)',
    'OUT_OF_DATE': 'rgba(247,124,177,1)'
}


var selectedColors = {
    "AVAILABLE": 'white',
    "RESERVED": 'white',
    "BOOKED": 'white',
    'OUT_OF_DATE': 'red'
}



const handleItemMove = (itemId, dragTime, newGroupOrder) => {
    const { items, groups } = this.state;

    const group = groups[newGroupOrder];

    this.setState({
        items: items.map(item =>
            item.id === itemId
                ? Object.assign({}, item, {
                    start: dragTime,
                    end: dragTime + (item.end - item.start),
                    group: group.id
                })
                : item
        )
    });

};

const handleItemResize = (itemId, time, edge) => {
    const { items } = this.state;

    this.setState({
        items: items.map(item =>
            item.id === itemId
                ? Object.assign({}, item, {
                    start: edge === "left" ? time : item.start,
                    end: edge === "left" ? item.end : time
                })
                : item
        )
    });

};


function HotelDates(props) {
    const navigate = useNavigate();

    let randomSeed = Math.floor(Math.random() * 1000);
    const minDate = new Date();
    const maxDate = new Date();
    maxDate.setDate(minDate.getDate() + MAX_BOOKING_DAYS);

    const [roomMap, setRoomMap] = useState();
    const [bookingMap, setBookingMap] = useState();
    const [groups, setGroups] = useState([]);
    const [items, setItems] = useState([]);
    const [itemMap, setItemMap] = useState({});
    const [selectedItemIds, setSelectedItemIds] = useState([]);
    const [openAddAvailability, setOpenAddAvailability] = useState(false);
    const [openAdd, setOpenAdd] = useState(false);
    const [openEdit, setOpenEdit] = useState(false);
    const [openRemove, setOpenRemove] = useState(false);
    const [editItem, setEditItem] = useState(null);
    const [newRoomId, setNewRoomId] = useState("default");
    const [newDates, setNewDates] = useState({start: null, end: null, checkInTime: null, checkOutTime: null});
    const [payed, setPayed] = useState(false);
    const [mainGuestInfo, setMainGuestInfo] = useState({});

    const [fetching, setFetching] = useState(false);

    const { hotelId } = useParams();



    function fetchDates(){
        setFetching(true);
        getWithJwt(`/api/v1/booking-management/hotel/${hotelId}/dates`)
            .then(response => response.json())
            .then(data => {
                console.log(data)

                var newGroups = []
                var newItems = []
                var groupId = 1;
                var itemId = 1;
                var newBookingMap = {};
                for (let entry of Object.entries(data.roomMap)){
                    let roomId = parseInt(entry[0])
                    let room = entry[1]
                    let roomItemIds = []
                    newGroups.push({id: roomId, title: room.title, bgColor: 'black'})
                    for (let dates of room.dates){
                        var start = new Date(dates.startDateTime)
                        var end = new Date(dates.endDateTime);
                        var checkInTime = start.toTimeString().substring(0, 5)
                        var checkOutTime = end.toTimeString().substring(0, 5)
                        start.setHours(0, 0, 0, 0);
                        end.setHours(0, 0, 0, 0);
                        newItems.push({
                            id: itemId,
                            group: roomId,
                            title: dates.status,
                            start: start,
                            end: end,
                            canMove: false,
                            canResize: false,
                            color: selectedColors[dates.status],
                            bgColor: bgColors[dates.status],
                            selectedBgColor: selectedBgColors[dates.status],
                            info: {
                                status: dates.status,
                                bookingId: dates.bookingId,
                                bookingRoomId: dates.bookingRoomId,
                                bookingRoomsId: dates.bookingRoomsId,
                                datesId: dates.datesId,
                                roomId: roomId,
                                checkInTime: checkInTime,
                                checkOutTime: checkOutTime
                            }
                        })
                        roomItemIds.push(itemId);
                        if (dates.bookingId != null){
                            if (dates.bookingId in newBookingMap){
                                newBookingMap[dates.bookingId].push(itemId);
                            } else {
                                newBookingMap[dates.bookingId] = [itemId];
                            }
                        }
                        itemId += 1;
                    }
                    groupId += 1;
                    room["itemIds"] = roomItemIds;
                }
                var newItemMap = {}
                for (let item of newItems){
                    newItemMap[item.id] = item;
                }
                setRoomMap(data.roomMap);
                setBookingMap(newBookingMap)
                setGroups(newGroups);
                setItems(newItems)
                setItemMap(newItemMap);
            })
            .catch(e => {
                console.error(e)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    function fetchMainGuestInfo(bookingId){
        setFetching(true);
        getWithJwt(`/api/v1/booking-management/hotel/${hotelId}/booking/${bookingId}/active/main-guest`)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setMainGuestInfo(data);
            })
            .catch(e => {
                console.error(e)})
            .finally(setFetching(false));
    }

    useEffect(() => {
        fetchDates()
    }, [])


    const itemRenderer = ({ item, timelineContext, itemContext, getItemProps, getResizeProps }) => {
        const { left: leftResizeProps, right: rightResizeProps } = getResizeProps();
        const backgroundColor = itemContext.selected ? (itemContext.dragging ? "red" : item.selectedBgColor) : item.bgColor;
        const borderColor = itemContext.resizing ? "red" : item.color;
        return (
            <div
                {...getItemProps({
                    style: {
                        // backgroundColor: 'blue',
                        // color: 'white',
                        backgroundColor,
                        color: item.color,
                        borderColor,
                        borderStyle: "solid",
                        borderWidth: 1,
                        borderRadius: 4,
                        borderLeftWidth: itemContext.selected ? 3 : 1,
                        borderRightWidth: itemContext.selected ? 3 : 1
                    },
                    onMouseDown: () => {
                        if (selectedItemIds.length === 0 ||
                            selectedItemIds[0] !== item.id
                        ){
                            setSelectedItemIds([item.id]);
                            setEditItem(item)
                            setNewRoomId(item.info.roomId)
                            setNewDates({
                                start: dateDashFormat(item.start),
                                end: dateDashFormat(item.end),
                                checkInTime: item.info.checkInTime,
                                checkOutTime: item.info.checkOutTime
                            })

                            setOpenAddAvailability(false);
                            setOpenAdd(false);
                            setOpenEdit(false);
                            setOpenRemove(false);

                            if (item.info.status === "RESERVED" || item.info.status === "BOOKED"){
                                fetchMainGuestInfo(item.info.bookingId)
                            }


                        } else {
                            var start = new Date();
                            var end = new Date();
                            end.setDate(end.getDate()+MAX_BOOKING_DAYS);
                            setSelectedItemIds([])
                            setNewDates({start: null, end: null, checkInTime: null, checkOutTime: null})
                        }
                    }
                })}
            >
                {/*{itemContext.useResizeHandle ? <div {...leftResizeProps} /> : null}*/}

                <div
                    style={{
                        height: itemContext.dimensions.height,
                        overflow: "hidden",
                        paddingLeft: 3,
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap"
                    }}
                >
                    {itemContext.title}
                </div>

                {/*{itemContext.useResizeHandle ? <div {...rightResizeProps} /> : null}*/}
            </div>
        );
    };

    function navBookingDetails(bookingId){
        navigate(`/user/hotel/${hotelId}/bookings/active/${bookingId}`, {state: {user_role: "hotel-manager"}})
    }

    const onClickAddAvailability = () => {
        if (selectedItemIds.length === 1) {
            setSelectedItemIds([])
            setOpenAddAvailability(true);
        } else {
            setOpenAddAvailability(!openAddAvailability)
        }
    }

    const onClickAdd = () => {
        setOpenAdd(!openAdd);
        setOpenEdit(false);
        setOpenRemove(false);
    }

    const onClickEdit = () => {
        console.log(newDates)
        setOpenAdd(false);
        setOpenEdit(!openEdit);
        setOpenRemove(false);
    }

    const onClickRemove = () => {
        setOpenAdd(false);
        setOpenEdit(false);
        setOpenRemove(!openRemove)
    }

    const onStartDateChange = (e) => {
        if (newDates.end <= e.target.value) {
            var end = new Date(e.target.value);
            end.setDate(end.getDate() + 2);
            setNewDates({...newDates, ["start"]: e.target.value, ["end"]: dateDashFormat(end)})
        } else {
            setNewDates({...newDates, ["start"]: e.target.value})
        }
    }

    const onEndDateChange = (e) => {
        if (newDates.start >= e.target.value) {
            var start = new Date(e.target.value);
            start.setDate(start.getDate());
            setNewDates({...newDates, ["start"]: dateDashFormat(start), ["end"]: e.target.value})
        } else {
            setNewDates({...newDates, ["end"]: e.target.value})
        }
    }

    const onCheckInTimeChange = (e) => {
        setNewDates({...newDates, ["checkInTime"]: e.target.value})
    }

    const onCheckOutTimeChange = (e) => {
        setNewDates({...newDates, ["checkOutTime"]: e.target.value})
    }

    const checkDatesInput = ()=> {
        console.log(newRoomId, newDates)
        if (newRoomId === "default"){
            // todo: add warning
            return false;
        }
        if (newDates?.start == null){
            // todo: add warning
            return false;
        }
        if (newDates?.end == null){
            // todo: add warning
            return false;
        }
        if (newDates.start >= newDates.end){
            // todo: add warning
            return false;
        }
        return true;
    }

    const checkDatesEmpty = (itemIdToExclude) => {
        for (let itemId of roomMap[newRoomId].itemIds){
            if (itemId === itemIdToExclude){
                continue;
            }
            var item = itemMap[itemId]
            console.log(item.info.roomId, dateDashFormat(item.start), dateDashFormat(item.end))
            if (dateDashFormat(item.start) < newDates.end
                && dateDashFormat(item.end) > newDates.start
            ){
                //todo: add warning
                return false;
            }
        }
        return true;

    }

    const checkDatesAvailability = (start, end) => {
        for (let itemId of roomMap[newRoomId].itemIds){
            var item = itemMap[itemId]
            if (item.info.status === "AVAILABLE"
                && dateDashFormat(item.start) <= start
                && dateDashFormat(item.end) >= end
            ){
                return true;
            }
        }
        //todo: add warning
        return false;
    }

    const onClickAddAvailabilitySubmit = () => {
        if (!checkDatesInput()){
            return
        }
        if (!checkDatesEmpty(null)){
            return
        }
        var payload = {
            roomId: newRoomId,
            startDate: new Date(newDates.start),
            endDate: new Date(newDates.end)
        }
        console.log(payload)
        postWithJwt(`/api/v1/booking/hotel/${hotelId}/dates/available`, payload)
            .catch(e => {console.error(e)})
            .finally(() => {
                fetchDates();
            })
    }
    const onClickEditAvailabilitySubmit = () => {
        if (!checkDatesInput()){
            return
        }
        var item = itemMap[selectedItemIds[0]];
        if (!checkDatesEmpty(selectedItemIds[0])){
            if (dateDashFormat(item.start) > newDates.start || dateDashFormat(item.end) < newDates.end
            ){
                return;
            }
            if (dateDashFormat(item.start) === newDates.start && dateDashFormat(item.end) === newDates.end){
                return;
            }
        }
        var payload = {
            roomId: item.info.roomId,
            datesId: item.info.datesId,
            startDate: new Date(newDates.start),
            endDate: new Date(newDates.end)
        }
        console.log(payload)
        putWithJwt(`/api/v1/booking/hotel/${hotelId}/dates/available`, payload)
            .catch(e => {console.error(e)})
            .finally(() => {
                fetchDates();
            })
    }

    const onClickDeleteAvailabilitySubmit = () => {
        var item = itemMap[selectedItemIds[0]];
        var payload = {
            datesId: item.info.datesId,
        }
        console.log(payload)
        deleteWithJwt(`/api/v1/booking/hotel/${hotelId}/dates/available`, payload)
            .catch(e => {
                console.error(e)})
            .finally(() => {
                fetchDates();
            });
    }

    const onClickAddBookingRoomSubmit = () => {
        if (!checkDatesInput()){
            return;
        }
        console.log("onClickAddBookingRoomSubmit")
        if (!checkDatesAvailability(newDates.start, newDates.end)){
            return;
        }
        var item = itemMap[selectedItemIds[0]];
        var payload = {
            bookingId: item.info.bookingId,
            bookingRoomsId: item.info.bookingRoomsId,
            roomId: newRoomId,
            startDate: new Date(newDates.start),
            endDate: new Date(newDates.end),
            checkInTime: newDates.checkInTime,
            checkOutTime: newDates.checkOutTime,
            payed: payed
        }
        postWithJwt(`/api/v1/booking/hotel/${hotelId}/booking/${item.info.bookingId}/dates`, payload)
            .catch(e=>{
                console.error(e)})
            .finally(() => {
                fetchDates()
            })
    }

    const onClickEditBookingRoomSubmit = () => {
        if (!checkDatesInput()){
            return;
        }
        if (selectedItemIds.length === 0){
            return;
        }
        var item = itemMap[selectedItemIds[0]];
        if (item.info.roomId !== newRoomId
            || newDates.end <= item.start
            || newDates.start >= item.end
        ){
            if (!checkDatesAvailability(newDates.start, newDates.end)){
                return;
            }
        } else {
            if (newDates.start < item.start){
                if (!checkDatesAvailability(newDates.start, item.start)){
                    return;
                }
            }
            if (newDates.end > item.end){
                if (!checkDatesAvailability(item.end, newDates.end)){
                    return;
                }
            }
        }
        var payload = {
            bookingId: item.info.bookingId,
            bookingRoomsId: item.info.bookingRoomsId,
            bookingRoomId: item.info.bookingRoomId,
            roomId: newRoomId,
            startDate: new Date(newDates.start),
            endDate: new Date(newDates.end),
            checkInTime: newDates.checkInTime,
            checkOutTime: newDates.checkOutTime
        }
        putWithJwt(`/api/v1/booking/hotel/${hotelId}/booking/${item.info.bookingId}/dates`, payload)
            .catch(e => {
                console.error(e)})
            .finally(() => {
                fetchDates()
            })
    }

    const onClickCancelBookingRoomSubmit = () => {
        if (selectedItemIds.length === 0){
            return;
        }
        var item = itemMap[selectedItemIds[0]];
        var payload = {
            bookingRoomId: item.info.bookingRoomId
        }
        putWithJwt(`/api/v1/booking/hotel/${hotelId}/booking/${item.info.bookingId}/dates/cancel`, payload)
            .catch(e => {
                console.error(e)})
            .finally(() => {
                fetchDates()
            })
    }


    if (fetching){
        return (
            <div>
                <Navbar/>
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
            </div>
        );
    }

    var defaultStartTime = new Date();
    var defaultEndTime = new Date();


    defaultStartTime.setDate(defaultStartTime.getDate() + 1)
    for (let item of items){
        if (item.title !== 'AVAILABLE'){
            continue;
        }
        if (item.start < defaultStartTime){
            defaultStartTime.setTime(item.start.getTime());
        }
    }
    if (defaultStartTime < defaultEndTime){
        defaultStartTime.setDate(defaultEndTime.getDate())
    }

    defaultStartTime.setHours(0, 0, 0, 0);


    defaultEndTime.setDate(defaultStartTime.getDate()+30);
    defaultEndTime.setHours(0, 0, 0, 0);

    console.log(itemMap[selectedItemIds[0]])
    console.log(groups)
    console.log(items)
    console.log(defaultStartTime)
    console.log(defaultEndTime)

    return (
        <div>
            <Navbar/>
            <div className="hotelDatesContainer">
                <h1>Dates</h1>
                <Timeline
                    groups={groups}
                    items={items}
                    keys={keys}
                    itemTouchSendsClick={false}
                    stackItems
                    itemHeightRatio={0.75}
                    showCursorLine
                    canMove={false}
                    canResize={false}
                    defaultTimeStart={defaultStartTime}
                    defaultTimeEnd={defaultEndTime}
                    itemRenderer={itemRenderer}
                    selected={selectedItemIds}
                />
                <span>※Select schedule to edit </span>
                <button
                    onClick={onClickAddAvailability}
                >Add availability</button>
                <br/>
                {
                    openAddAvailability &&
                    <div className="hotelDatesCard">
                        <h2>Add availability</h2>
                        <select
                            value={newRoomId}
                            onChange={e => {
                                setNewRoomId(e.target.value)
                            }}
                        >
                            <option value="default">Not selected</option>
                            {
                                Object.keys(roomMap).map((roomId, index) => {
                                    return (
                                        <option value={roomId}>{roomMap[roomId].title}</option>
                                    )
                                })
                            }
                        </select>
                        <div className="hotelDatesEdit">
                            <label>Start date</label>
                            <input
                                type="date"
                                value={newDates.start}
                                onChange={onStartDateChange}
                            />
                            <label>End date</label>
                            <input type="date"
                                   value={newDates.end}
                                   onChange={onEndDateChange}
                            />
                            <button
                                onClick={onClickAddAvailabilitySubmit}
                            >Submit</button>
                        </div>
                    </div>
                }
                { (selectedItemIds.length === 1 && itemMap[selectedItemIds[0]] != null && itemMap[selectedItemIds[0]].info.status === "AVAILABLE") &&
                    <div className="hotelDatesCard">
                        <h2>Availability</h2>
                        <span>Room: {roomMap[itemMap[selectedItemIds[0]].info.roomId].title}</span> <br/>
                        <span>Dates: {itemMap[selectedItemIds[0]].start.toISOString().split('T')[0]} ~ {itemMap[selectedItemIds[0]].end.toISOString().split('T')[0]}</span>
                        <br/>
                        <button
                            onClick={() => {onClickAddAvailability()}}
                        >Add</button>
                        <button
                            onClick={() => {onClickEdit()}}
                        >Edit</button>
                        <button
                            onClick={onClickRemove}
                        >Remove</button> <br/>
                        { openEdit &&
                            <div className="hotelDatesEdit">
                                <label>New start date</label>
                                <input
                                    type="date"
                                    value={newDates.start}
                                    onChange={onStartDateChange}
                                />
                                <label>New end date</label>
                                <input
                                    type="date"
                                    value={newDates.end}
                                    onChange={onEndDateChange}
                                />
                                <button
                                    onClick={onClickEditAvailabilitySubmit}
                                >Submit</button>
                            </div>
                        }
                        {
                            openRemove &&
                                <div className="hotelDatesEdit">
                                    <span>Removing availabile dates</span> <br/>
                                    <button
                                        onClick={onClickDeleteAvailabilitySubmit}
                                    >Confirm</button>
                                </div>
                        }

                    </div>
                }
                { selectedItemIds.length === 1 && itemMap[selectedItemIds[0]] != null &&
                    ( itemMap[selectedItemIds[0]].info.status === "RESERVED" ||
                        itemMap[selectedItemIds[0]].info.status === "BOOKED"
                    )
                    &&
                    <div className="hotelDatesReservation">
                        { itemMap[selectedItemIds[0]].info.status === "RESERVED" &&
                        <h2>Reservation</h2> }
                        { itemMap[selectedItemIds[0]].info.status === "BOOKED" &&
                            <h2>Booking</h2> }

                        <div className="hotelDatesCard">
                            <span>Guest</span> <br/>
                            <span>First name: {mainGuestInfo.firstName}</span> <br/>
                            <span>Last name: {mainGuestInfo.lastName}</span> <br/>
                            <span>Email: {mainGuestInfo.email}</span> <br/>
                            <button
                                onClick={()=>{navBookingDetails(itemMap[selectedItemIds[0]].info.bookingId)}}
                            >Booking details</button> <br/>
                        </div>
                        <div className="hotelDatesCard">
                            { itemMap[selectedItemIds[0]].info.status === "RESERVED" &&
                                <span>Reserved Rooms</span> }
                            { itemMap[selectedItemIds[0]].info.status === "BOOKED" &&
                                <span>Booked Rooms</span> }
                            <table>
                                <tr>
                                    <th>Room</th>
                                    <th>Dates</th>
                                    <th></th>
                                </tr>
                                {
                                    bookingMap[itemMap[selectedItemIds[0]].info.bookingId].map((itemId, index) => {
                                        var item = itemMap[itemId];
                                        return (
                                            <tr
                                                onClick={() => {
                                                    setSelectedItemIds([itemId])
                                                }}
                                            >
                                                <td>{roomMap[item.info.roomId].title}</td>
                                                <td>{item.start.toISOString().split('T')[0]} ~ {item.end.toISOString().split('T')[0]}</td>
                                            </tr>
                                        );
                                    })
                                }
                            </table>

                            <button
                                onClick={onClickAdd}
                            >Add Room</button>
                            <button
                                onClick={() => {
                                    onClickEdit()
                                }}
                            >Edit Room</button>
                            <button
                                onClick={onClickRemove}
                            >Cancel Room</button>
                            { openAdd &&
                            <div className="hotelDatesEdit">
                                <h4>Add room</h4>
                                <label>Room</label>
                                <select
                                    value={newRoomId}
                                    onChange={e => {
                                        setNewRoomId(e.target.value)
                                    }}
                                >
                                    <option value="default">Not selected</option>
                                    {groups.map((group, index) => {
                                        return <option value={group.id}>{group.title}</option>
                                    })}
                                </select>
                                <label>Start date</label>
                                <input
                                    type="date"
                                    value={newDates.start}
                                    onChange={onStartDateChange}
                                />
                                <label>Check-in time</label>
                                <input
                                    type="time"
                                    onChange={onCheckInTimeChange}
                                    value={newDates.checkInTime}
                                ></input><br/>
                                <label>End date</label>
                                <input type="date"
                                       value={newDates.end}
                                       onChange={onEndDateChange}
                                />
                                <label>Check-out time</label>
                                <input
                                    type="time"
                                    onChange={onCheckOutTimeChange}
                                    value={newDates.checkOutTime}
                                /><br/>
                                <label>Require Payment</label>
                                <input
                                    type="checkbox"
                                    checked={!payed}
                                    onClick={() => {setPayed(!payed)}}
                                /><br/>
                                <button
                                    onClick={onClickAddBookingRoomSubmit}
                                >Submit</button>
                            </div>
                            }
                            { openEdit && selectedItemIds.length === 1 &&
                                <div className="hotelDatesEdit">
                                    <h4>Edit room</h4>
                                    <label>Room</label> <br/>
                                    <span>
                                        {roomMap[itemMap[selectedItemIds[0]].info.roomId].title} ->
                                    <select
                                        value={newRoomId}
                                        onChange={e => {setNewRoomId(e.target.value)}}
                                    >
                                        <option value="default">Not selected</option>
                                        {groups.map((group, index) => {
                                            return <option value={group.id}>{group.title}</option>
                                        })}
                                    </select>
                                    </span>
                                    <label>Start date</label>
                                    <span>{itemMap[selectedItemIds[0]].start.toISOString().split('T')[0]} ->
                                        <input type="date"
                                               value={newDates.start}
                                               onChange={onStartDateChange}
                                        /></span>
                                    <label>Check-in time</label>
                                    <input
                                        type="time"
                                        onChange={onCheckInTimeChange}
                                        value={newDates.checkInTime}
                                    /><br/>
                                    <label>End date</label>
                                    <span>{itemMap[selectedItemIds[0]].end.toISOString().split('T')[0]} ->
                                        <input type="date"

                                               value={newDates.end}
                                               onChange={onEndDateChange}
                                        /></span>
                                    <label>Check-out time</label>
                                    <input
                                        type="time"
                                        onChange={onCheckOutTimeChange}
                                        value={newDates.checkOutTime}
                                    /><br/>
                                    <button
                                        onClick={onClickEditBookingRoomSubmit}
                                    >Submit</button>
                                </div>
                            }
                            { openRemove &&
                                <div className="hotelDatesEdit">
                                    <span>Removing reserved dates</span> <br/>
                                    <button
                                        onClick={onClickCancelBookingRoomSubmit}
                                    >Confirm</button>
                                </div>
                            }
                        </div>
                    </div>
                }
            </div>

            <MailList/>
            <Footer/>
        </div>
    );
}

export default HotelDates;