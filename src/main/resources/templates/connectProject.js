const form = document.getElementById ('form');
form.addEventListener('submit', getFormValue);

async function getFormValue(event) {
    event.preventDefault();
    const
        id = form.querySelector('[name="id"]'),
        comment = form.querySelector('[name="comment"]'),
        subjectId = form.querySelector('[name="subjectId"]'),
        name = form.querySelector('[name="name"]');

    let dataProject = {
        id: id.value,
        name: name.value,
        comment: comment.value,
        subjectId: subjectId.value
    };

    if (dataProject.id === "") {
        dataProject.id = null
    }

    if (dataProject.name === "") {
        dataProject.name = null
    }
    if (dataProject.subjectId === "") {
        dataProject.subjectId = null
    }

    let promise = await fetch('http://localhost:8082/project', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(dataProject)
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