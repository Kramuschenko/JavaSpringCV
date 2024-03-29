package com.cv.demo.service;

import com.cv.demo.assembler.ProjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.project.MissingProjectNameException;
import com.cv.demo.exception.project.MissingProjectSubjectIdException;
import com.cv.demo.exception.project.NegativeProjectIdException;
import com.cv.demo.exception.project.ProjectNotFoundException;
import com.cv.demo.exception.subject.SubjectNotFoundException;
import com.cv.demo.tools.ProjectITTool;
import com.cv.demo.tools.SubjectITTool;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RunWith(SpringRunner.class)
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

    private ProjectDto projectDto(Integer projectId, String name, String comment, Integer subjectId) {
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
        Assert.assertEquals(projectId, project.getId());
        Assert.assertEquals(subjectID, project.getSubjectId());
        Assert.assertEquals(name, project.getName());
        Assert.assertEquals(comment, project.getComment());

    }

    @Test(expected = ProjectNotFoundException.class)
    @Transactional
    @Rollback
    public void shouldNotFindProjectByNotExistingProjectId() throws ProjectNotFoundException {
        //given
        int notExistingProjectId = 1;

        //when
        projectService.getProjectById(notExistingProjectId);

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
        Assert.assertEquals(3, projectDtoList.size());

        List<Integer> id = new ArrayList<>();
        id.add(projectDtoList.get(0).getId());
        id.add(projectDtoList.get(1).getId());
        id.add(projectDtoList.get(2).getId());

        Assert.assertTrue(id.contains(10));
        Assert.assertTrue(id.contains(11));
        Assert.assertTrue(id.contains(12));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldReturnEmptyListWhenFindAllProjects() {
        //given

        //when
        List<ProjectDto> projectDtoList = projectService.getAllProjects();

        //then
        Assert.assertTrue(projectDtoList.isEmpty());
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
        Assert.assertEquals(2, projectDtoList.size());

        List<Integer> id = new ArrayList<>();
        id.add(projectDtoList.get(0).getId());
        id.add(projectDtoList.get(1).getId());

        Assert.assertTrue(id.contains(projectId1));
        Assert.assertTrue(id.contains(projectId2));
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
        Assert.assertTrue(projectDtoList.isEmpty());
    }

    @Test(expected = SubjectNotFoundException.class)
    @Transactional
    @Rollback
    public void shouldNotFindProjectByNotExistingSubjectId() throws SubjectNotFoundException {

        //given
        int subjectId = 1;
        int projectId = 10;
        int notExistingSubjectId = 2;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(projectId, "test", subjectId);

        //when
        projectService.getProjectsBySubjectId(notExistingSubjectId);

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateProjectWithSpecificSubjectId() throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

        //given
        Integer subjectId = 1;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = 10;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        ProjectDto projectNewDto = projectDto(projectId, name, comment, subjectId);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);

        //when
        projectService.saveOrUpdate(projectNewDto);

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);

        List<Project> projects = projectRepository.findAll();
        Assert.assertEquals(1, projects.size());

        Project project = projects.get(0);

        Assert.assertEquals(projectId, project.getId());
        Assert.assertEquals(name, project.getName());
        Assert.assertEquals(comment, project.getComment());
        Assert.assertEquals(subjectId, project.getSubjectId());
        Assert.assertTrue(before.isBefore(project.getCreatedAt()));
        Assert.assertTrue(after.isAfter(project.getCreatedAt()));
        Assert.assertTrue(before.isBefore(project.getModifiedAt()));
        Assert.assertTrue(after.isAfter(project.getModifiedAt()));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateProjectWithGeneratedFirstId() throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

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
        Assert.assertEquals(1, projects.size());

        Project project = projects.get(0);

        Assert.assertEquals(expectedId, project.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateProjectWithConsecutiveIdGenerated() throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

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
        Assert.assertEquals(2, projects.size());

        Project project = projects.get(1);
        Assert.assertEquals(expectedId, project.getId());
    }

    @Test(expected = MissingProjectSubjectIdException.class)
    @Transactional
    @Rollback
    public void shouldNotCreateProjectWithNullSubjectID() throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

        //given
        Integer subjectId = null;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = 10;

        ProjectDto projectNewDto = projectDto(projectId, name, comment, subjectId);

        //when
        projectService.saveOrUpdate(projectNewDto);

        //then
        //exception expected
    }

    @Test(expected = SubjectNotFoundException.class)
    @Transactional
    @Rollback
    public void shouldNotCreateProjectWithNotFoundSubjectID() throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

        //given
        Integer subjectId = 10;
        String name = "NAME";
        String comment = "COMMENT";
        Integer projectId = 10;

        ProjectDto projectNewDto = projectDto(projectId, name, comment, subjectId);

        //when
        projectService.saveOrUpdate(projectNewDto);

        //then
        //exception expected
    }

    @Test(expected = MissingProjectNameException.class)
    @Transactional
    @Rollback
    public void shouldNotCreateProjectWithNullName() throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

        //given
        Integer subjectId = 1;
        String name = null;
        String comment = "COMMENT";
        Integer projectId = 10;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        ProjectDto projectNewDto = projectDto(projectId, name, comment, subjectId);

        //when
        projectService.saveOrUpdate(projectNewDto);

        //then
        //exception expected
    }

    @Test(expected = NegativeProjectIdException.class)
    @Transactional
    @Rollback
    public void shouldNotCreateProjectWithNegativeId() throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

        //given
        Integer subjectId = 1;
        String name = "Name";
        String comment = "COMMENT";
        Integer projectId = -10;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        ProjectDto projectNewDto = projectDto(projectId, name, comment, subjectId);

        //when
        projectService.saveOrUpdate(projectNewDto);

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdateProject() throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException, NegativeProjectIdException {

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
        Assert.assertEquals(1, projects.size());

        Project project = projects.get(0);

        Assert.assertEquals(projectId, project.getId());
        Assert.assertEquals(commentNew, project.getComment());
        Assert.assertEquals(projectNew.getCreatedAt(), project.getCreatedAt());
        Assert.assertTrue(before.isBefore(project.getModifiedAt()));
        Assert.assertTrue(after.isAfter(project.getModifiedAt()));
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
        Assert.assertEquals(0, count);
    }

    @Test(expected = ProjectNotFoundException.class)
    @Transactional
    @Rollback
    public void shouldNotDeleteNotExistingProject() throws ProjectNotFoundException {

        //given
        int subjectId = 1;
        int projectId = 10;
        int notExistingProjectId = 2;

        subjectITTool.createSubject(subjectId, "ABBREVIATION", "TEACHER");
        projectITTool.createProject(projectId, "test", subjectId);

        //when
        projectService.delete(notExistingProjectId);

        //then
        //exception expected
    }


}