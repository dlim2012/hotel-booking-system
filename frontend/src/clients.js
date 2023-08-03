

const checkStatus = response => {
    // console.log(response)
    if (response.ok){
        return response;
    }
    const error = new Error(response.statusText);
    error.response = response;
    return Promise.reject(error);
}

export const post = (path, payload) => {
    const requestOptions = {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
    };
    return fetch(path, requestOptions)
        .then(response => checkStatus(response));
}

export const getWithJwt = (path) => {
    const requestOptions = {
        method: 'GET',
        mode: 'cors',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        }
    };
    return fetch(path, requestOptions)
        .then(response => checkStatus(response))
}

export const postWithJwt = (path, payload) => {
    const requestOptions = {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        body: JSON.stringify(payload)
    };
    return fetch(path, requestOptions)
        .then(response => checkStatus(response))
}

export const putWithJwt = (path, payload) => {
    const requestOptions = {
        method: 'PUT',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        body: JSON.stringify(payload)
    };
    return fetch(path, requestOptions)
        .then(response => checkStatus(response))
}

export const deleteWithJwt = (path, payload) => {
    const requestOptions = {
        method: 'DELETE',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        body: JSON.stringify(payload)
    };
    return fetch(path, requestOptions)
        .then(response => checkStatus(response))
}

export const postImageWithJwt = (path, imageData) => {
    const formData = new FormData();
    formData.append("image", imageData);
    const requestOptions = {
        method: 'POST',
        mode: 'cors',
        headers: {
            // 'Content-Type': 'multipart/form-data',
            'Authorization': 'Bearer ' + localStorage.getItem("jwt")
        },
        body: formData
    };
    console.log(requestOptions)
    return fetch(path, requestOptions)
        .then(response=> checkStatus(response))
}