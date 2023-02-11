const form = document.getElementById ('form');
form.addEventListener('submit', getFormValue);

async function getFormValue(event) {
    event.preventDefault();
    const comment = form.querySelector('[name="comment"]'),
        subjectId = form.querySelector('[name="subjectId"]'),
        name = form.querySelector('[name="name"]');

    let dataProject = {
        id: null,
        name: name.value,
        comment: comment.value,
        subjectId: subjectId.value
    };

    if (dataProject.name == "") {
        dataProject.name = null
    }
    if (dataProject.subjectId == "") {
        dataProject.subjectId = null
    }

    let promise = await fetch('http://localhost:8082/project', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(dataProject)
    });
    if (promise.status == 400) {
        alert("error")
    }
    if (promise.status == 200) {
        alert("top")
    }
}