const form = document.getElementById ('form');
form.addEventListener('submit', getFormValue);

async function getFormValue(event) {
    event.preventDefault();
    const
        id = form.querySelector('[name="id"]'),
        abbreviation = form.querySelector('[name="abbreviation"]'),
        teacher = form.querySelector('[name="teacher"]');


    let dataSubject = {
        id: id.value,
        abbreviation: abbreviation.value,
        teacher: teacher.value,
    };

    if (dataSubject.id === "") {
        dataSubject.id = null
    }

    if (dataSubject.abbreviation === "") {
        dataSubject.abbreviation = null
    }

    let promise = await fetch('http://localhost:8082/subject', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(dataSubject)
    });

    let status = promise.status;

    switch (status) {
        case 200 : alert("Success: " + status);
            break;
        case 400 : alert("Wrong input: " + status);
            break;
        case 404 : alert("Not found: " + status);
            break;
        default : alert("Server error: " + status);
    }
}