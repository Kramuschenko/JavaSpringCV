package com.cv.demo.frontend;

import com.cv.demo.backend.Project;
import com.cv.demo.exception.MissingProjectDataException;
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
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/project")
    private List<Project> getAllProject() {
        return projectService.getAllProjects();
    }

    @GetMapping("/project/{id}")
    private Project getProject(@PathVariable("id") int id) {
        return projectService.getProjectById(id).orElse(null);
    }

    @DeleteMapping("/project/{id}")
    private void deleteProject(@PathVariable("id") int id) {
        projectService.delete(id);
    }

    @PostMapping("/project")
    private @ResponseBody ResponseEntity<String> saveProject(@RequestBody Project project) throws MissingProjectDataException {

        projectService.saveOrUpdate(project);
        String answer = "Project: " + (project.getId() == 0 ? subjectService.getAllSubjects().size() + projectService.getAllProjects().size() : project.getId()) + " added or updated";
        log.debug("Success: " + (project.getId() == 0 ? "Project created" : "Project updated (" + project.getId() + ")"));
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
