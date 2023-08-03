// import {useRef, useState} from "react";
// import {de} from "date-fns/locale";
//
//
// export const getDefaultHeaderAttributes = () => {
//
//     const [destination, setDestination] = useState(
//         location.state == null? "Where are you going?" :
//             location.state?.destination);
//     const [address, setAddress] = useState(location.state?.address);
//     const [coordinates, setCoordinates] = useState(location.state?.coordinates);
//     const [openDate, setOpenDate] = useState(false);
//     const [date, setDate] = useState(
//         (location.state == null ?
//             [
//                 {
//                     startDate: new Date(),
//                     endDate: new Date(),
//                     key: "selection",
//                 },]
//
//             : location.state.date)
//     );
//     const [openOptions, setOpenOptions] = useState(false);
//     const [options, setOptions] = useState(
//         location.state == null ?
//             {
//                 adult: 1,
//                 children: 0,
//                 room: 1,
//             }: location.state.options
//
//     );
//
//     return {
//         "destination": destination,
//         "setDestination": setDestination,
//         "address": address,
//         "setAddress": setAddress,
//         "coordinates": coordinates,
//         "setCoordinates": setCoordinates,
//         "openDate": openDate,
//         "setOpenDate": setOpenDate,
//         "date": date,
//         "setDate": setDate,
//         "openOptions": openOptions,
//         "setOpenOptions": setOpenOptions,
//         "options": options,
//         "setOptions": setOptions
//     }
// }