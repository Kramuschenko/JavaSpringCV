package com.cv.demo.frontend;

import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectDataException;
import com.cv.demo.exception.ProjectNotFoundException;
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
    private @ResponseBody ResponseEntity<String> deleteProject(@PathVariable("id") int id) throws ProjectNotFoundException {
        log.info("Subject {} will be deleted", id);
        projectService.delete(id);
        return new ResponseEntity<>("Subject " + id + " was deleted", HttpStatus.OK);
    }

    @PostMapping("/project")
    @ResponseBody
    private ResponseEntity<String> saveProject(@RequestBody ProjectDto projectDto) throws MissingProjectDataException {

        projectService.saveOrUpdate(projectDto);
        String answer = "Project: " + (projectDto.getId() == 0 ?
                projectService.getAllProjects().size() + " added" : projectDto.getId() + " updated");
        log.debug("Success: " + (projectDto.getId() == 0 ? "Project created" : "Project updated (" + projectDto.getId() + ")"));
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PostMapping("/projects")
    @ResponseBody
    private ResponseEntity<String> saveProjects(@RequestBody List<ProjectDto> projectsDto) throws MissingProjectDataException {

        int size = projectsDto.size();
        for (int i = 0; i < size; i++) {

            try {
                saveProject(projectsDto.get(i));
                log.info("Project {} of {} saved successfully", (i + 1), size);
            } catch (MissingProjectDataException e) {
                errorsLog(projectsDto, i, e);
                throw e;
            }
        }

        String answer = "Projects added or updated";
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    private static void errorsLog(List<ProjectDto> projectsDto, int i, Exception e) {
        if (i > 0) {
            log.error(e + "were saved only {} elements ", (i));
            log.error("Were saved only that items: " + projectsDto.subList(0, i));
        } else {
            log.error("Subjects were not saved");
        }
    }
}
