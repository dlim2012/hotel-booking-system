
const dayOfWeekName = [
    "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
];

const monthName = [
    "January", "February", "March", "April", "May", "June", "July", "August", "September",
    "October", "November", "December"
]

const getDateText = (date) => {
    return `${dayOfWeekName[date.getDay()]}, ${monthName[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`
}

export default getDateText;