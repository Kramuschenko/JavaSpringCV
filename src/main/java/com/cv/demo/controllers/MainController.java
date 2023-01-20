package com.cv.demo.controllers;

import com.cv.demo.Services.ProjectService;
import com.cv.demo.db.Projects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MainController {
    //autowired the projectService class
    @Autowired
    private ProjectService projectService;
    //creating a get mapping that retrieves all the Projects detail from the database
    @GetMapping("/projects")
    private List<Projects> getAllProjects()
    {
        return projectService.getAllProjects();
    }
    //creating a get mapping that retrieves the detail of a specific Projects
    @GetMapping("/projects/{id}")
    private Projects getProjects(@PathVariable("id") int id)
    {
        return projectService.getProjectsById(id);
    }
    //creating a deleted mapping that deletes a specific Projects
    @DeleteMapping("/projects/{id}")
    private void deleteProjects(@PathVariable("id") int id)
    {
        projectService.delete(id);
    }
    //creating post mapping that post the Projects detail in the database
    @PostMapping("/projects/save")
    private int saveProjects(@RequestBody Projects projects)
    {
        projectService.saveOrUpdate(projects);
        return projects.getId();
    }

}
