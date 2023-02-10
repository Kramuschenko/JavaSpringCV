package com.cv.demo.service;

import com.cv.demo.assembler.ProjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectNameException;
import com.cv.demo.exception.MissingProjectSubjectIdException;
import com.cv.demo.exception.ProjectNotFoundException;
import com.cv.demo.exception.SubjectNotFoundException;
import com.cv.demo.tools.ProjectITTool;
import com.cv.demo.tools.SubjectITTool;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private ProjectDto projectDto(Integer projectId , String name , String comment , Integer subjectId) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(projectId);
        projectDto.setName(name);
        projectDto.setComment(comment);
        projectDto.setSubjectId(subjectId);
        return projectDto;
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindProjectById() throws ProjectNotFoundException {

        //given
        Integer subjectID = 1;
        Integer projectId = 10;
        String name = "NAME";
        String comment = "comment";

        subjectITTool.createSubject(subjectID, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(projectId, name, subjectID, comment);

        //when
        ProjectDto project = projectService.getProjectById(projectId);

        //then
        assertEquals(projectId, project.getId());
        assertEquals(subjectID, project.getSubjectId());
        assertEquals(name, project.getName());
        assertEquals(comment, project.getComment());

    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotFindProjectByNotExistingProjectId() {
        //given
        int notExistingProjectId = 1;

        //when
        assertThrows(ProjectNotFoundException.class, () -> {
            projectService.getProjectById(notExistingProjectId);
        });

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllProjects() {

        //given
        Integer subjectID = 1;
        subjectITTool.createSubject(subjectID, "ABBREVIATION");
        projectITTool.createProject(10, "Name of first", subjectID);
        projectITTool.createProject(11, "Name of second", subjectID);
        projectITTool.createProject(12, "Name of third", subjectID);

        //when
        List<ProjectDto> projectDtoList = projectService.getAllProjects();

        //then
        assertEquals(3, projectDtoList.size());

        List<Integer> id = new ArrayList<>();
        id.add(projectDtoList.get(0).getId());
        id.add(projectDtoList.get(1).getId());
        id.add(projectDtoList.get(2).getId());

        assertTrue(id.contains(10));
        assertTrue(id.contains(11));
        assertTrue(id.contains(12));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldReturnEmptyListWhenFindAllProjects() {
        //given

        //when
        List<ProjectDto> projectDtoList = projectService.getAllProjects();

        //then
        assertTrue(projectDtoList.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllProjectsBySubjectId() throws SubjectNotFoundException {

        //given
        int subjectId1 = 1;
        int subjectId2 = 2;
        int projectId1 = 10;
        int projectId2 = 11;
        subjectITTool.createSubject(subjectId1, "ABBREVIATION of first");
        subjectITTool.createSubject(subjectId2, "ABBREVIATION of second");
        projectITTool.createProject(projectId1, "Name of first", subjectId1);
        projectITTool.createProject(projectId2, "Name of second", subjectId1);
        projectITTool.createProject(12, "Name of third", subjectId2);

        //when
        List<ProjectDto> projectDtoList = projectService.getProjectsBySubjectId(subjectId1);

        //then
        assertEquals(2, projectDtoList.size());

        List<Integer> id = new ArrayList<>();
        id.add(projectDtoList.get(0).getId());
        id.add(projectDtoList.get(1).getId());

        assertTrue(id.contains(projectId1));
        assertTrue(id.contains(projectId2));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldReturnEmptyListWhenFindAllProjectsBySubjectId() throws SubjectNotFoundException {

        //given
        int subjectId1 = 1;
        int subjectId2 = 2;
        int projectId1 = 10;
        int projectId2 = 11;
        int projectId3 = 12;
        subjectITTool.createSubject(subjectId1, "ABBREVIATION of first");
        subjectITTool.createSubject(subjectId2, "ABBREVIATION of second");
        projectITTool.createProject(projectId1, "Name of first", subjectId2);
        projectITTool.createProject(projectId2, "Name of second", subjectId2);
        projectITTool.createProject(projectId3, "Name of third", subjectId2);

        //when
        List<ProjectDto> projectDtoList = projectService.getProjectsBySubjectId(subjectId1);

        //then
        assertTrue(projectDtoList.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotFindProjectByNotExistingSubjectId() {

        //given
        int subjectId = 1;
        int projectId = 10;
        int notExistingSubjectId = 2;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(projectId, "test", subjectId);

        //when
        assertThrows(SubjectNotFoundException.class, () -> {
            projectService.getProjectsBySubjectId(notExistingSubjectId);
        });

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateProjectWithSpecificSubjectId() throws MissingProjectNameException, MissingProjectSubjectIdException {

        //given
        Integer subjectId = 1;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = 10;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        ProjectDto projectNewDto = projectDto(projectId , name , comment, subjectId);

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
        assertTrue(before.isBefore(project.getCreatedAt()));
        assertTrue(after.isAfter(project.getCreatedAt()));
        assertTrue(before.isBefore(project.getModifiedAt()));
        assertTrue(after.isAfter(project.getModifiedAt()));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateProjectWithGeneratedFirstId() throws MissingProjectNameException, MissingProjectSubjectIdException {

        //given
        Integer subjectId = 1;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = null;
        Integer expectedId = 1;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        ProjectDto projectNewDto = projectDto(projectId, name, comment, subjectId);

        //when
        projectService.saveOrUpdate(projectNewDto);

        //then
        List<Project> projects = projectRepository.findAll();
        assertEquals(1, projects.size());

        Project project = projects.get(0);

        assertEquals(expectedId, project.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateProjectWithConsecutiveIdGenerated() throws MissingProjectNameException, MissingProjectSubjectIdException {

        //given
        Integer subjectId = 1;

        String name = "NAME OF NEW PROJECT";
        String comment = "COMMENT NEW";
        Integer projectId = null;

        Integer expectedId = 2;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(1, "name", subjectId, "comment");

        ProjectDto projectDtoNew = projectDto(projectId, name, comment, subjectId);

        //when
        projectService.saveOrUpdate(projectDtoNew);

        //then
        List<Project> projects = projectRepository.findAll();
        assertEquals(2, projects.size());

        Project project = projects.get(1);
        assertEquals(expectedId, project.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotCreateProjectWithNullSubjectID() {

        //given
        Integer subjectId = null;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = 10;

        ProjectDto projectNewDto = projectDto(projectId, name, comment, subjectId);

        //when
        assertThrows(MissingProjectSubjectIdException.class, () -> {
            projectService.saveOrUpdate(projectNewDto);
        });


        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotCreateProjectWithNullName() {

        //given
        Integer subjectId = 1;
        String name = null;
        String comment = "COMMENT";
        Integer projectId = 10;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        ProjectDto projectNewDto = projectDto(projectId, name, comment, subjectId);

        //when
        assertThrows(MissingProjectNameException.class, () -> {
            projectService.saveOrUpdate(projectNewDto);
        });

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdateProject() throws MissingProjectNameException, MissingProjectSubjectIdException {

        //given
        Integer subjectId = 1;
        Integer projectId = 10;
        String commentNew = "UPDATING";
        LocalDateTime createdAt = LocalDateTime.of(2022, 1, 1, 0, 0, 1);
        LocalDateTime modifiedAt = LocalDateTime.of(2023, 1, 24, 0, 0, 1);

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        Project projectNew = projectITTool.createProject(projectId, "NAME", subjectId, "COMMENT", createdAt, modifiedAt);

        projectNew.setComment(commentNew);
        ProjectDto projectDto = projectAssembler.toDto(projectNew);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);

        //when
        projectService.saveOrUpdate(projectDto);

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);

        List<Project> projects = projectRepository.findAll();
        assertEquals(1, projects.size());

        Project project = projects.get(0);

        assertEquals(projectId, project.getId());
        assertEquals(commentNew, project.getComment());
        assertEquals(projectNew.getCreatedAt(), project.getCreatedAt());
        assertTrue(before.isBefore(project.getModifiedAt()));
        assertTrue(after.isAfter(project.getModifiedAt()));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldDeleteProject() throws ProjectNotFoundException {

        //given
        int subjectID = 1;
        int projectId = 10;

        subjectITTool.createSubject(subjectID, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(projectId, "test", subjectID);

        //when
        projectService.delete(projectId);

        //then
        long count = projectRepository.count();
        assertEquals(0, count);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotDeleteNotExistingProject()  {

        //given
        int subjectId = 1;
        int projectId = 10;
        int notExistingProjectId = 2;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(projectId, "test", subjectId);

        //when
        assertThrows(ProjectNotFoundException.class, () -> {
            projectService.delete(notExistingProjectId);
        });

        //then
        //exception expected
    }


}