import React, {useMemo} from 'react';
import {useTable} from "react-table";

function Table(props) {

    // let rowIndex = 0;
    //
    // const newData = props.data
    //     .map(v => {
    //         rowIndex += 1;
    //         return {...v, rowIndex: rowIndex}
    //     });


    const columns = useMemo(() => props.columns, [])
    const data = useMemo(() => props.data, [])

    const tableInstance = useTable({
        columns: columns,
        data: data
    })

    const {getTableProps, getTableBodyProps, headerGroups, rows, prepareRow} = tableInstance;


    return (
        <table {...getTableProps()}>
            <thead >
            {
                headerGroups.map(headerGroup => (
                    <tr {...headerGroup.getHeaderGroupProps()}>
                        {
                            headerGroup.headers.map(column => (
                                <th {...column.getHeaderProps()}>
                                    {column.render('Header')}
                                </th>
                            ))
                        }
                    </tr>
                ))
            }
            </thead>
            <tbody {...getTableBodyProps()}>
            {
                rows.map((row) => {
                    prepareRow(row)
                    return (
                        <tr {...row.getRowProps()}>
                            {
                                row.cells.map((cell) => (
                                    <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
                                ))
                            }
                        </tr>
                    )
                })
            }
            </tbody>
        </table>
    );
}

export default Table;