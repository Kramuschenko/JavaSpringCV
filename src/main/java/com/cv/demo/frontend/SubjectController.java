package com.cv.demo.frontend;

import com.cv.demo.backend.Subject;
import com.cv.demo.exception.MissingSubjectDataException;
import com.cv.demo.responcestatus.ServerResponse;
import com.cv.demo.service.ProjectService;
import com.cv.demo.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/subject")
    private List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/subject/id/{id}")
    private Subject getSubject(@PathVariable("id") int id) {
        return subjectService.getSubjectById(id).orElse(null);
    }

    @DeleteMapping("/subject/id/{id}")
    private void deleteSubject(@PathVariable("id") int id) {
        subjectService.delete(id);
    }

    @PostMapping("/subject")
    private @ResponseBody ResponseEntity<ServerResponse> saveSubject(@RequestBody Subject subject) throws MissingSubjectDataException {

        subjectService.saveOrUpdate(subject);
        String answer = "Subject: " + (subject.getId() == 0 ? subjectService.getAllSubjects().size() + projectService.getAllProjects().size() : subject.getId()) + " added or updated";
        return new ServerResponse().successNotification(answer);
    }

    @GetMapping("/subject/teacher/{teacherName}")
    private Subject firstByTeacher(@PathVariable("teacherName") String teacher) {
        return subjectService.firstByTeacher(teacher);
    }

    @GetMapping("/subject/teacher/all/{teacherName}")
    private List<Subject> subjectsByTeacher(@PathVariable("teacherName") String teacher) {
        return subjectService.subjectsByTeacher(teacher);
    }
}
