import './recommendedTable.css'
import {useNavigate} from "react-router-dom";

function RecommendedTable(props) {
    const navigate = useNavigate();

    const hotelInfo = props.hotelInfo;
    const date = props.date;
    const options = props.options;
    const hotelDetails = props.hotelDetails;
    const roomSelection = props.roomSelection;
    const setRoomSelection = props.setRoomSelection;

    if (hotelInfo == null || options == null || date == null || hotelDetails == null){
        return;
    }

    const bedNameMap = {
        "KING": "king bed",
        "QUEEN": "queen bed",
        "SOFA_BED": "sofa bed",
        "FULL": "full bed",
        "TWIN": "twin bed",
        "SINGLE": "single bed"

    }
    // console.log(item)


    var startDate = (date[0].startDate.getMonth()+1) + "/" + date[0].startDate.getDate() + "/" + date[0].startDate.getFullYear()
    var cancelDate = new Date();
    var paymentDate = new Date();
    cancelDate.setDate(date[0].endDate.getDate() - hotelInfo.maxFreeCancellationDays)
    paymentDate.setDate(date[0].endDate.getDate() - hotelInfo.noPrepaymentDays)
    cancelDate = cancelDate > new Date() ?
        (cancelDate.getMonth()+1) + "/" + cancelDate.getDate() + "/" + cancelDate.getFullYear()
        : ""
    paymentDate = paymentDate > new Date() ?
        (paymentDate.getMonth()+1) + "/" + paymentDate.getDate() + "/" + paymentDate.getFullYear()
        : ""
    // console.log("cancelDate", cancelDate)
    // console.log("paymentDate", paymentDate)
    // console.log(options)
    // console.log("recommended", recommended)
    var dates = Math.round((date[0].endDate.getTime() - date[0].startDate.getTime()) / 86400000);

    const onReserveSelection = () => {
        console.log("onReserveSelection")
        console.log(roomSelection)
        var recommended = hotelInfo.roomsList
        var newRoomSelection = {}

        for (let i=0; i<recommended.length; i++){
            console.log(recommended[i])
            newRoomSelection[recommended[i].roomsId] = recommended[i].recommended.toString()
        }
        setRoomSelection(newRoomSelection)
        document.getElementById("availabilityTable").scrollIntoView({behavior: "smooth"})
    }

    var optionString = `${options.room} room` + (options.room > 1 ? "s": "");
    optionString += `, ${dates} night` + (dates > 1 ? "s": "");
    // optionString += `, ${options.adult} adult` + (options.adult > 1 ? "s" : "");
    // if (options.children > 0){
    //     optionString += `, ${options.children} child` + (options.children > 1 ? "ren": "")
    // }


    return (
        <div className="recommendationBox">
            <table className="recommendedTable">
                <tr className="recommendedTableRow">
                    <td
                        className="recommendedTableCol1"
                        colSpan={3}>Recommended for {options.adult} adult{options.adult > 1 && "s"}{ options.children > 0 && " and " + (options.children + " child" + (options.children > 1 && "ren"))}</td>
                </tr>

                <tr className="recommendedTableRow">
                    <td className={"recommendedTableCol2"}>
                {
                    hotelInfo.roomsList.map((room, index) => {
                        if (room.recommended < 1){
                            return;
                        }
                        return (
                            <div className={"recommendedTableCol"}>
                                        {room.recommended} x {room.displayName}: {room.bedInfoList.map((bedInfo, i) => {
                                                return [bedInfo.quantity + " " + bedNameMap[bedInfo.size]]
                                            }).join(', ')}

                                        {   paymentDate.length > 0 &&  (
                                                <td>
                                                No prepayment required until {startDate} {hotelInfo.noPrepaymentDays === 0 && "11:59 PM"}
                                                </td>
                                        )
                                        }
                                        { ( cancelDate.length > 0 && hotelInfo.maxFreeCancellationDays > 0 &&
                                            <tr><td>Free cancellation before {cancelDate}</td></tr>
                                        )
                                        }
                            </div>
                        );
                    }
                )}
                    </td>
                {

                        <td
                            className="recommendedTableCol"
                            rowSpan={(hotelInfo.roomsList.length )}>
                            <span>{optionString}</span> <br/>
                            <span>${hotelInfo.totalPrice / 100}</span><br/>
                            <button onClick={onReserveSelection}>Reserve your selections</button> <br/>
                            <span className="recommendedNote">Don't worry - clicking this button won't charge you anything!</span>
                        </td>
                }
                </tr>

            </table>
        </div>
    );
}

export default RecommendedTable;