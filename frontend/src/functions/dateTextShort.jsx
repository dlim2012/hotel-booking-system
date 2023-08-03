
const dayOfWeekNameShort = [
    "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
];

const monthNameShort = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
]

const getDateTextShort = (date) => {
    return `${dayOfWeekNameShort[date.getDay()]}, ${monthNameShort[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`
}

export const getDateTextShort2 = (date) => {
    return `${monthNameShort[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()} (${dayOfWeekNameShort[date.getDay()]})`
}

export default getDateTextShort;
// export  getDateTextShort2;