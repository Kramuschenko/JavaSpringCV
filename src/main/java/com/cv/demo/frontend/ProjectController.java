package com.cv.demo.frontend;

import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.*;
import com.cv.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@CrossOrigin(maxAge = 3600)
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/project")
    private List<ProjectDto> getAllProject() {
        return projectService.getAllProjects();
    }


    @GetMapping("/project/by-subject-id/{id}")
    private List<ProjectDto> getAllProjectBySubjectId(@PathVariable("id") Integer id) throws SubjectNotFoundException {
        return projectService.getProjectsBySubjectId(id);
    }

    @GetMapping("/project/{id}")
    private ProjectDto getProject(@PathVariable("id") int id) throws ProjectNotFoundException {
        return projectService.getProjectById(id);
    }

    @DeleteMapping("/project/{id}")
    private @ResponseBody ResponseEntity<String> deleteProject(@PathVariable("id") Integer id) throws ProjectNotFoundException {
        log.info("Subject {} will be deleted", id);
        projectService.delete(id);
        return new ResponseEntity<>("Subject " + id + " was deleted", HttpStatus.OK);
    }

    @PostMapping("/project")
    @ResponseBody
    private ResponseEntity<String> saveProject(@RequestBody ProjectDto projectDto) throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

        projectService.saveOrUpdate(projectDto);
        Integer id = projectDto.getId();

        String answer = "Project: " + (id == null ? " added" : id + " updated");
        log.info("Success: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PostMapping("/projects")
    @ResponseBody
    private ResponseEntity<String> saveProjects(@RequestBody List<ProjectDto> projectsDto) throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

        projectService.saveOrUpdateProjects(projectsDto);

        String answer = "Projects added or updated";
        log.info("Success: " + answer);
        log.debug("Saved or updated: " + projectsDto);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }


}
