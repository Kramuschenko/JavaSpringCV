package com.cv.demo.controllers;

import com.cv.demo.Services.ProjectService;
import com.cv.demo.db.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/project")
    private List<Project> getAllProject() {
        return projectService.getAllProjects();
    }

    @GetMapping("/project/{id}")
    private Project getProject(@PathVariable("id") int id) {
        return projectService.getProjectsById(id);
    }

    @DeleteMapping("/project/{id}")
    private void deleteProject(@PathVariable("id") int id) {
        projectService.delete(id);
    }

    @PostMapping("/project")
    private int saveProject(@RequestBody Project project) {

        projectService.saveOrUpdate(project);
        return project.getId();
    }
}
