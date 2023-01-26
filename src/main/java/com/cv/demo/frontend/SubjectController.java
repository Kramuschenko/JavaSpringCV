package com.cv.demo.frontend;

import com.cv.demo.backend.Subject;
import com.cv.demo.exception.MissingSubjectDataException;
import com.cv.demo.service.ProjectService;
import com.cv.demo.service.SubjectService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
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
    private @ResponseBody ResponseEntity<String> saveSubject(@RequestBody Subject subject) throws MissingSubjectDataException {

        subjectService.saveOrUpdate(subject);
        String answer = "Subject: " + (subject.getId() == 0 ? subjectService.getAllSubjects().size() + projectService.getAllProjects().size() : subject.getId()) + " added or updated";
        log.debug("Success: " + (subject.getId() == 0 ? "Subject created" : "Subject updated (" + subject.getId() + ")"));
        return new ResponseEntity<>(answer, HttpStatus.OK);
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
