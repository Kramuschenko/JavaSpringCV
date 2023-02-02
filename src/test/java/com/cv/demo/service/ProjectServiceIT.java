package com.cv.demo.service;

import com.cv.demo.assembler.ProjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.tools.ProjectITTool;
import com.cv.demo.tools.SubjectITTool;
import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectDataException;
import com.cv.demo.exception.ProjectNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * You need to comment all project records in data.sql
 */
@SpringBootTest
@Log4j2
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
    public void creatingProjectTest() throws MissingProjectDataException {
        //given
        subjectITTool.createSubject(1, "TEST", "TEST TEST");
        Project projectNew = projectITTool.createProject(10, "test", 1);
        ProjectDto projectNewDto = projectAssembler.toDto(projectNew);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);

        //when
        projectService.saveOrUpdate(projectNewDto);

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);

        List<Project> projects = projectRepository.findAll();
        assertEquals(1, projects.size());

        Project project = projects.get(0);

        assertEquals(10, project.getId());
        assertEquals("test", project.getName());
        assertEquals(1, project.getSubjectId());
        assertTrue(before.isBefore(project.getModifiedAt()));
        assertTrue(after.isAfter(project.getModifiedAt()));
    }

    @Test
    @Transactional
    @Rollback
    public void updatingProjectTest() throws MissingProjectDataException {
        //given
        subjectITTool.createSubject(1, "TEST", "TEST TEST");
        Project projectNew = projectITTool.createProject(10, "test", 1, "smth", LocalDateTime.of(2022, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1));
        ProjectDto projectNewDto = projectAssembler.toDto(projectNew);

        //when
        projectNew.setName("UPDATING");
        projectService.saveOrUpdate(projectNewDto);

        //then
        Optional<Project> project = projectRepository.findById(10);

        assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0, 1), project.get().getCreatedAt());
        assertEquals(projectNewDto.getName(), project.get().getName());
    }

    @Test
    @Transactional
    @Rollback
    public void deletingProjectTest() throws ProjectNotFoundException {
        //given
        subjectITTool.createSubject(1, "TEST", "TEST TEST");
        projectITTool.createProject(10, "test", 1);

        //when
        projectService.delete(10);

        //then
        List<Project> projects = projectRepository.findAll();

        assertEquals(0, projects.size());
    }

    @Test
    @Transactional
    @Rollback
    public void getProjectByIdTest() {
        //given
        subjectITTool.createSubject(1, "TEST", "TEST TEST");
        Project projectNew = projectITTool.createProject(10, "test", 1);
        Optional<ProjectDto> optionalProjectDto = Optional.of(projectAssembler.toDto(projectNew));

        //when
        Optional<ProjectDto> project = projectService.getProjectById(10);

        //then
        assertEquals(optionalProjectDto, project);
    }

    @Test
    @Transactional
    @Rollback
    public void getAllProjectsTest() {
        //given
        subjectITTool.createSubject(1, "test");
        Project project1 = projectITTool.createProject(10, "test", 1);
        Project project2 = projectITTool.createProject(11, "test", 1);
        Project project3 = projectITTool.createProject(13, "test", 1);
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
        assertEquals(listOfProjects, projectDtoList);
    }

}
