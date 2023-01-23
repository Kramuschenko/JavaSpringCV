package com.cv.demo.controllers;

import com.cv.demo.Services.SubjectService;
import com.cv.demo.db.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/subject")
    private List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/subject/id/{id}")
    private Subject getSubject(@PathVariable("id") int id) {
        return subjectService.getSubjectById(id);
    }

    @DeleteMapping("/subject/id/{id}")
    private void deleteSubject(@PathVariable("id") int id) {
        subjectService.delete(id);
    }

    @PostMapping("/subject")
    private int saveSubject(@RequestBody Subject subject) {
        subjectService.saveOrUpdate(subject);
        return subject.getId();
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
