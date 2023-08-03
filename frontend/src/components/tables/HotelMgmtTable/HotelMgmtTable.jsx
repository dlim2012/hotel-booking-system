import React, {useMemo} from 'react';
import {useTable} from "react-table";
import {useNavigate} from "react-router-dom";
import './hotelMgmtTable.css'

function HotelMgmtTable(props) {
    const navigate = useNavigate();
    console.log(props)


    const navManagement = (v) => {
        console.log(v)
        navigate(`/user/hotel/${v.id}`, {state: { hotel: v }})
    }

    let rowIndex = 0;

    console.log(1)
    const newData = props.data
        .map(v => {
            rowIndex += 1;
            // console.log(v)
            // var buttons = (
            //     <div>
            //         <button
            //             onClick={() => {navigate(`/user/hotel/${v.id}`, {state: { hotel: v }})}}
            //         >Main</button>
            //         <button
            //             onClick={() => {navigate(`/user/hotel/${v.id}/dates`)}}
            //         >
            //             Dates
            //         </button>
            //         <button>
            //             Info
            //         </button>
            //         <button
            //             onClick={() => {navigate(`/hotels/${v.id}`)}}
            //         >View page</button>
            //     </div>
            // )
            return {...v, rowIndex: rowIndex}
        });


    const columns = useMemo(() => props.columns, [])
    const data = useMemo(() => newData, [])

    const tableInstance = useTable({
        columns: columns,
        data: data
    })

    const {getTableProps, getTableBodyProps, headerGroups, rows, prepareRow} = tableInstance;

    return (
        <table
            className="hotelMgmtTable"
            {...getTableProps()}
        >
            <thead

            >
            {
                headerGroups.map(headerGroup => (
                    <tr
                        {...headerGroup.getHeaderGroupProps()}
                    >
                        {
                            headerGroup.headers.map(column => (
                                <th
                                    {...column.getHeaderProps()}
                                    className="hotelMgmtTableHeader"
                                >
                                    {column.render('Header')}
                                </th>
                            ))
                        }
                        <th
                            className="hotelMgmtTableHeader"
                        ></th>
                    </tr>
                ))
            }
            </thead>
            <tbody {...getTableBodyProps()}>
            {
                rows.map((row) => {
                    prepareRow(row)
                    return (
                        <tr
                            {...row.getRowProps()}
                            // onClick={() => {navManagement(row.original)}}
                        >
                            {
                                row.cells.map((cell) => (
                                    <td
                                        {...cell.getCellProps()}
                                        className="hotelMgmtTableRow"
                                    >{cell.render('Cell')}</td>
                                ))
                            }
                            <td
                                className="hotelMgmtTableRow"
                            ><button

                                onClick={() => {navManagement(row.original)}}
                            >Main Page</button></td>
                        </tr>


                    )
                })
            }
            </tbody>
        </table>
    );
}

export default HotelMgmtTable;