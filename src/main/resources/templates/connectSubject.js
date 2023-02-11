const form = document.getElementById ('form');
form.addEventListener('submit', getFormValue);

async function getFormValue(event) {
    event.preventDefault();
    const abbreviation = form.querySelector('[name="abbreviation"]'),
        teacher = form.querySelector('[name="teacher"]');


    let dataSubject = {
        id: null,
        abbreviation: abbreviation.value,
        teacher: teacher.value,
    };

    if (dataSubject.abbreviation == "") {
        dataSubject.abbreviation = null
    }

    let promise = await fetch('http://localhost:8082/subject', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(dataSubject)
    });
    if (promise.status == 400) {
        alert("error")
    }
    if (promise.status == 200) {
        alert("top")
    }
}