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
    private List<Subject> getAllProject() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/subject/{id}")
    private Subject getProject(@PathVariable("id") int id) {
        return subjectService.getSubjectsById(id);
    }

    @DeleteMapping("/subject/{id}")
    private void deleteProject(@PathVariable("id") int id) {
        subjectService.delete(id);
    }

    @PostMapping("/subject")
    private int saveProject(@RequestBody Subject subject) {
        subjectService.saveOrUpdate(subject);
        return subject.getId();
    }
}
