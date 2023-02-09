package com.cv.demo.frontend;

import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectNameException;
import com.cv.demo.exception.MissingProjectSubjectIdException;
import com.cv.demo.exception.ProjectNotFoundException;
import com.cv.demo.exception.SubjectNotFoundException;
import com.cv.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/project")
    private List<ProjectDto> getAllProject() {
        return projectService.getAllProjects();
    }


    @GetMapping("/project/by-subject-id/{id}")
    private List<ProjectDto> getAllProjectBySubjectId(@PathVariable("id") Integer id) throws SubjectNotFoundException {
        return projectService.getProjectBySubjectId(id);
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
    private ResponseEntity<String> saveProject(@RequestBody ProjectDto projectDto) throws MissingProjectNameException, MissingProjectSubjectIdException {

        projectService.saveOrUpdate(projectDto);
        Integer id = projectDto.getId();

        String answer = "Project: " + (id == null ?
                projectService.getAllProjects().size() + " added" : id + " updated");
        log.debug("Success: " + (id == null ? "Project created" : "Project updated (" + id + ")"));
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PostMapping("/projects")
    @ResponseBody
    private ResponseEntity<String> saveProjects(@RequestBody List<ProjectDto> projectsDto) throws MissingProjectNameException, MissingProjectSubjectIdException {

        int size = projectsDto.size();
        for (int i = 0; i < size; i++) {

            try {
                saveProject(projectsDto.get(i));
                log.info("Project {} of {} saved successfully", (i + 1), size);
            } catch (MissingProjectNameException | MissingProjectSubjectIdException e) {
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
