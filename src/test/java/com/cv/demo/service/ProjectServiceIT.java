package com.cv.demo.service;

import com.cv.demo.assembler.ProjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectDataException;
import com.cv.demo.exception.ProjectNotFoundException;
import com.cv.demo.tools.ProjectITTool;
import com.cv.demo.tools.SubjectITTool;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * You need to comment all project records in data.sql
 */

@Log4j2
@SpringBootTest
public class ProjectServiceIT {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectITTool projectITTool;

    @Autowired
    private SubjectITTool subjectITTool;
    private final ProjectAssembler projectAssembler = Mappers.getMapper(ProjectAssembler.class);

    @Test
    @Transactional
    @Rollback
    public void creatingProjectWithSpecificNum() throws MissingProjectDataException {
        //given
        Integer subjectId = 1;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = 10;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        Project projectNew = projectITTool.createProject(projectId, name, subjectId, comment);
        ProjectDto projectNewDto = projectAssembler.toDto(projectNew);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);

        //when
        projectService.saveOrUpdate(projectNewDto);

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);

        List<Project> projects = projectRepository.findAll();
        assertEquals(1, projects.size());

        Project project = projects.get(0);

        assertEquals(projectId, project.getId());
        assertEquals(name, project.getName());
        assertEquals(comment, project.getComment());
        assertEquals(subjectId, project.getSubjectId());
        assertTrue(before.isBefore(project.getModifiedAt()));
        assertTrue(after.isAfter(project.getModifiedAt()));
    }

    @Test
    @Transactional
    @Rollback
    public void creatingProjectWithNullName() {
        //given
        Integer subjectId = 1;
        String name = null;
        String comment = "COMMENT";
        Integer projectId = 10;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        Project projectNew = projectITTool.createProject(projectId, name, subjectId, comment);
        ProjectDto projectNewDto = projectAssembler.toDto(projectNew);


        try {
            //when
            projectService.saveOrUpdate(projectNewDto);
            fail("Expected exception was not thrown");
        } catch (MissingProjectDataException e) {
            //then
            assertTrue(true, "Thrown: " + e);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void creatingProjectWithNullSubjectID() {
        //given
        Integer subjectId = null;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = 10;

        Project projectNew = projectITTool.createProject(projectId, name, subjectId, comment);
        ProjectDto projectNewDto = projectAssembler.toDto(projectNew);

        try {
            //when
            projectService.saveOrUpdate(projectNewDto);
            fail("Expected exception was not thrown");
        } catch (MissingProjectDataException e) {
            //then
            assertTrue(true, "Thrown: " + e);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void creatingProjectWithNullIdAndNoProjectsInRepository() throws MissingProjectDataException {
        //given
        int subjectId = 1;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = null;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");

        ProjectDto projectNewDto = new ProjectDto();
        projectNewDto.setId(projectId);
        projectNewDto.setName(name);
        projectNewDto.setComment(comment);
        projectNewDto.setSubjectId(subjectId);


        //when
        projectService.saveOrUpdate(projectNewDto);

        //then


        List<Project> projects = projectRepository.findAll();
        assertEquals(1, projects.size());

        Project project = projects.get(0);

        assertEquals(1, project.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void creatingProjectWithNullIdAndProjectsInRepository() throws MissingProjectDataException {
        //given
        Integer subjectId = 1;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = 1;
        Integer subjectIdNew = 1;
        String nameNew = "NAME OF NEW PROJECT";
        String commentNew = "COMMENT NEW";
        Integer projectIdNew = null;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");

        ProjectDto projectNewDto = new ProjectDto();
        projectNewDto.setId(projectId);
        projectNewDto.setName(name);
        projectNewDto.setComment(comment);
        projectNewDto.setSubjectId(subjectId);
        projectService.saveOrUpdate(projectNewDto);

        ProjectDto projectDtoForTesting = new ProjectDto();
        projectDtoForTesting.setId(projectIdNew);
        projectDtoForTesting.setName(nameNew);
        projectDtoForTesting.setComment(commentNew);
        projectDtoForTesting.setSubjectId(subjectIdNew);

        //when
        projectService.saveOrUpdate(projectDtoForTesting);

        //then
        List<Project> projects = projectRepository.findAll();
        assertEquals(2, projects.size());

        Project project = projects.get(1);
        assertEquals(2, project.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void updatingProject() throws MissingProjectDataException {
        //given
        int subjectId = 1;
        LocalDateTime createdAt = LocalDateTime.of(2022, 1, 1, 0, 0, 1);

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        Project projectNew = projectITTool.createProject(10, "NAME", subjectId, "COMMENT", createdAt, LocalDateTime.of(2023, 1, 1, 0, 0, 1));
        ProjectDto projectNewDto = projectAssembler.toDto(projectNew);

        //when
        projectNew.setName("UPDATING");
        projectService.saveOrUpdate(projectNewDto);

        //then
        Optional<Project> project = projectRepository.findById(10);

        assertTrue(project.isPresent());
        assertEquals(createdAt, project.orElse(null).getCreatedAt());
        assertEquals(projectNewDto.getName(), project.get().getName());
    }

    @Test
    @Transactional
    @Rollback
    public void deletingProject() throws ProjectNotFoundException {
        //given
        int subjectID = 1;
        int projectId = 10;

        subjectITTool.createSubject(subjectID, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(projectId, "test", subjectID);

        //when
        projectService.delete(projectId);

        //then
        List<Project> projects = projectRepository.findAll();

        assertEquals(0, projects.size());
    }

    @Test
    @Transactional
    @Rollback
    public void deletingProjectNotFoundException() throws ProjectNotFoundException {
        //given
        int subjectId = 1;
        int projectId = 10;
        int notExistingProjectId = 2;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(projectId, "test", subjectId);

        try {
            //when
            projectService.delete(notExistingProjectId);
            fail("Expected exception was not thrown");
        } catch (ProjectNotFoundException e) {
            //then
            assertTrue(true, "Thrown: " + e);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void getProjectById() {
        //given
        int subjectID = 1;
        int projectId = 10;

        subjectITTool.createSubject(subjectID, "ABBREVIATION", "TEACHER");
        Project projectNew = projectITTool.createProject(projectId, "NAME", subjectID);
        Optional<ProjectDto> optionalProjectDto = Optional.of(projectAssembler.toDto(projectNew));

        //when
        Optional<ProjectDto> project = projectService.getProjectById(projectId);

        //then
        assertEquals(optionalProjectDto, project);
    }

    @Test
    @Transactional
    @Rollback
    public void getAllProjects() {
        //given
        int subjectID = 1;
        subjectITTool.createSubject(subjectID, "ABBREVIATION");
        Project project1 = projectITTool.createProject(10, "test", subjectID);
        Project project2 = projectITTool.createProject(11, "test", subjectID);
        Project project3 = projectITTool.createProject(13, "test", subjectID);
        List<ProjectDto> listOfProjects = new ArrayList<>();
        ProjectDto projectDto1 = projectAssembler.toDto(project1);
        ProjectDto projectDto2 = projectAssembler.toDto(project2);
        ProjectDto projectDto3 = projectAssembler.toDto(project3);
        listOfProjects.add(projectDto1);
        listOfProjects.add(projectDto2);
        listOfProjects.add(projectDto3);

        //when
        List<ProjectDto> projectDtoList = projectService.getAllProjects();

        //then
        assertEquals(3, projectDtoList.size());
        assertEquals(projectDto1, projectDtoList.get(0));
        assertEquals(projectDto2, projectDtoList.get(1));
        assertEquals(projectDto3, projectDtoList.get(2));
        assertEquals(listOfProjects, projectDtoList);
    }

}

