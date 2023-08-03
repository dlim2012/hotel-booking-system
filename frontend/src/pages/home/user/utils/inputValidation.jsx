
export const validEmail = (email) => {
    var email_at_index = email.indexOf('@');
    var email_dot_index = email.lastIndexOf('.');
    var res = email_at_index >= 1
        && 0 < email_dot_index
        && email_dot_index < email.length - 1
        && email_at_index + 1 < email_dot_index;
    return res;
}


export function validateEmail(email) {
    if (email.length === 0){
        return false;
    }

    var validRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

    return email.match(validRegex)
}


export const validPhoneNumber = (number) => {
    var phoneno1 = /^\d{10}$/;
    var phoneno2 = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/;
    return number.value.match(phoneno1) || number.value.match(phoneno2);
}
