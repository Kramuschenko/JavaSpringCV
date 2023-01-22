package com.cv.demo.controllers;

import com.cv.demo.Services.ProjectService;
import com.cv.demo.db.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cv.demo.Services.ProjectService.setCreationInformation;
import static com.cv.demo.Services.ProjectService.setModificationInformation;

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
