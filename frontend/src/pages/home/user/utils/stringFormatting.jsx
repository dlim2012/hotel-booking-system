
const DAYS_OF_WEEKS = [
    "SUNDAY",
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY"
]
const DAYS_OF_WEEKS_SHORT = [
    "SUN",
    "MON",
    "TUE",
    "WED",
    "THU",
    "FRI",
    "SAT"
]

export function getTime(dateTimeString){
    if (dateTimeString == null){
        return;
    }
    var hours = parseInt(dateTimeString.substring(11, 13));
    var minutes = parseInt(dateTimeString.substring(14, 16));
    if (hours >= 12){
        return String(hours - 12).padStart(2, '0') + ":" + String(minutes).padStart(2, '0') + " PM"
    } else {

        return String(hours).padStart(2, '0') + ":" + String(minutes).padStart(2, '0') + " AM"
    }
}

export function getDateTime(dateTimeString){
    return `${dateTimeString?.substring(0, 10)} (${DAYS_OF_WEEKS_SHORT[(new Date(dateTimeString)).getDay()]}) ${getTime(dateTimeString)}`
}

export function getDate(dateTimeString){
    return `${dateTimeString?.substring(0, 10)} (${DAYS_OF_WEEKS_SHORT[(new Date(dateTimeString)).getDay()]})`;
}