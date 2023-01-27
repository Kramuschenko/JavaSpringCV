package com.cv.demo.frontend;

import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectDataException;
import com.cv.demo.service.ProjectService;
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

    @GetMapping("/project")
    private List<ProjectDto> getAllProject() {
        return projectService.getAllProjects();
    }

    @GetMapping("/project/{id}")
    private ProjectDto getProject(@PathVariable("id") int id) {
        return projectService.getProjectById(id).orElse(null);
    }

    @DeleteMapping("/project/{id}")
    private @ResponseBody ResponseEntity<String> deleteProject(@PathVariable("id") int id) {
        log.info("Subject {} will be deleted", id);
        projectService.delete(id);
        return new ResponseEntity<>("Subject deleted", HttpStatus.OK);
    }

    @PostMapping("/project")
    private @ResponseBody ResponseEntity<String> saveProject(@RequestBody ProjectDto projectDto) throws MissingProjectDataException {

        projectService.saveOrUpdate(projectDto);
        String answer = "Project: " + (projectDto.getId() == 0 ?
                projectService.getAllProjects().size() + " added" : projectDto.getId() + " updated");
        log.debug("Success: " + (projectDto.getId() == 0 ? "Project created" : "Project updated (" + projectDto.getId() + ")"));
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
