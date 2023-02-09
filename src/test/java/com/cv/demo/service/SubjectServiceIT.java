package com.cv.demo.service;

import com.cv.demo.assembler.SubjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.ArchiveSubjectNotFoundException;
import com.cv.demo.exception.DeletingArchiveSubjectException;
import com.cv.demo.exception.MissingSubjectAbbreviationException;
import com.cv.demo.exception.SubjectNotFoundException;
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

@SpringBootTest
@RunWith(SpringRunner.class)
@Log4j2
public class SubjectServiceIT {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectITTool subjectITTool;

    @Autowired
    private ProjectITTool projectITTool;

    @Autowired
    private SubjectService subjectService;

    private final SubjectAssembler subjectAssembler = Mappers.getMapper(SubjectAssembler.class);

    @Test
    @Transactional
    @Rollback
    public void getAllSubjects() {
        //given
        Integer firstId = 0;
        Integer secondId = 4;
        subjectITTool.createSubject(firstId, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);
        subjectITTool.createSubject(secondId, "Abbreviation", "Teacher");

        //when
        List<SubjectDto> subjectsDto = subjectService.getAllSubjects();

        //then
        Assert.assertEquals(2, subjectsDto.size());

        List<Integer> id = new ArrayList<>();
        id.add(subjectsDto.get(0).getId());
        id.add(subjectsDto.get(1).getId());

        Assert.assertTrue(id.contains(firstId));
        Assert.assertTrue(id.contains(secondId));
    }

    @Test
    @Transactional
    @Rollback
    public void getSubjectById() throws SubjectNotFoundException {
        //given
        Integer subjectID = 4;
        String abbreviation = "Abbreviation";
        String teacher = "Teacher";

        subjectITTool.createSubject(subjectID, abbreviation, teacher);

        //when
        SubjectDto subjectDto = subjectService.getSubjectById(subjectID);

        //then
        Assert.assertEquals(subjectID, subjectDto.getId());
        Assert.assertEquals(abbreviation, subjectDto.getAbbreviation());
        Assert.assertEquals(teacher, subjectDto.getTeacher());
    }

    @Test
    @Transactional
    @Rollback
    public void creatingSubject() throws MissingSubjectAbbreviationException {
        //given
        Integer subjectID = 4;
        String abbreviation = "Abbreviation";
        String teacher = "Teacher";
        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);

        Subject subjectNew = subjectITTool.createSubject(subjectID, abbreviation, teacher, null);

        //when
        subjectService.saveOrUpdate(subjectAssembler.toDto(subjectNew));

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);
        List<Subject> subjects = subjectRepository.findAll();

        Assert.assertEquals(1, subjects.size());

        Subject subject = subjects.get(0);

        Assert.assertEquals(subjectID, subject.getId());
        Assert.assertEquals(abbreviation, subject.getAbbreviation());
        Assert.assertEquals(teacher, subject.getTeacher());
        Assert.assertTrue(before.isBefore(subject.getCreatedAt()));
        Assert.assertTrue(after.isAfter(subject.getCreatedAt()));
        Assert.assertTrue(before.isBefore(subject.getModifiedAt()));
        Assert.assertTrue(after.isAfter(subject.getModifiedAt()));
        Assert.assertTrue(subject.getProjects().isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void creatingSubjectWithNullIdAndNoSubjectsInRepository() throws MissingSubjectAbbreviationException {
        //given
        Integer subjectID = null;
        String abbreviation = "Abbreviation";
        Integer expectedId = 1;

        SubjectDto subjectDtoNew = new SubjectDto();
        subjectDtoNew.setId(subjectID);
        subjectDtoNew.setAbbreviation(abbreviation);

        //when
        subjectService.saveOrUpdate(subjectDtoNew);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        Assert.assertEquals(1, subjects.size());
        Assert.assertEquals(expectedId, subjects.get(0).getId());
    }

    @Test
    @Transactional
    @Rollback
    public void creatingSubjectWithNullIdAndWithSubjectsInRepository() throws MissingSubjectAbbreviationException {
        //given
        Integer subjectID = 1;
        String abbreviation = "Abbreviation";

        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(subjectID);
        subjectDto.setAbbreviation(abbreviation);

        subjectService.saveOrUpdate(subjectDto);

        Integer subjectIDNew = null;
        String abbreviationNew = "Abbreviation2";
        Integer expectedId = 2;

        SubjectDto subjectDtoNew = new SubjectDto();
        subjectDtoNew.setId(subjectIDNew);
        subjectDtoNew.setAbbreviation(abbreviationNew);

        //when
        Subject subject = subjectService.saveOrUpdate(subjectDtoNew);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        Assert.assertEquals(2, subjects.size());
        Assert.assertEquals(expectedId, subject.getId());
    }

    @Test(expected = MissingSubjectAbbreviationException.class)
    @Transactional
    @Rollback
    public void creatingSubjectWithNullAbbreviation() throws MissingSubjectAbbreviationException {
        //given
        Integer subjectID = 1;
        String abbreviation = null;

        SubjectDto subjectDtoNew = new SubjectDto();
        subjectDtoNew.setId(subjectID);
        subjectDtoNew.setAbbreviation(abbreviation);

        //when
        subjectService.saveOrUpdate(subjectDtoNew);

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void updatingSubjectTest() throws MissingSubjectAbbreviationException {
        //given
        Integer subjectId = 4;
        Integer projectId = 1;
        String abbreviationNew = "Abbreviation UPDATED";
        Project projectNew = projectITTool.createProject(projectId, "name");
        List<Project> projectsNew = new ArrayList<>();
        projectsNew.add(projectNew);
        LocalDateTime createdAt = LocalDateTime.of(2020, 6, 7, 12, 30, 25);
        LocalDateTime modifiedAt = LocalDateTime.of(2022, 1, 1, 0, 0, 1);
        Subject subjectNew = subjectITTool.createSubject(subjectId, "Abbreviation", createdAt, modifiedAt, "Teacher", projectsNew);

        subjectNew.setAbbreviation(abbreviationNew);

        //when
        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);
        subjectService.saveOrUpdate(subjectAssembler.toDto(subjectNew));

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);

        List<Subject> subjects = subjectRepository.findAll();
        Assert.assertEquals(1, subjects.size());

        Subject subject = subjects.get(0);

        Assert.assertEquals(subjectId, subject.getId());
        Assert.assertEquals(subjectNew.getAbbreviation(), subject.getAbbreviation());
        Assert.assertEquals(subjectNew.getCreatedAt(), subject.getCreatedAt());
        Assert.assertTrue(before.isBefore(subject.getModifiedAt()));
        Assert.assertTrue(after.isAfter(subject.getModifiedAt()));

        List<Project> projects = subject.getProjects();
        Assert.assertEquals(1, projects.size());
        Assert.assertEquals(projectId, projects.get(0).getId());
    }


    @Test(expected = SubjectNotFoundException.class)
    @Transactional
    @Rollback
    public void deletingNotExistingSubject() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {
        //when
        subjectService.delete(6);

        //then
        //exception expected
    }

    @Test(expected = DeletingArchiveSubjectException.class)
    @Transactional
    @Rollback
    public void deletingArchiveSubject() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {
        //given
        subjectITTool.createSubject(0, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);

        subjectService.delete(0);

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void deletingSubjectWithoutProjectsTest() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {
        //given
        subjectITTool.createSubject(4, "TEST", "TEST TEST", new ArrayList<>());

        //when
        subjectService.delete(4);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        Assert.assertEquals(0, subjects.size());
    }

    @Test
    @Transactional
    @Rollback
    public void deletingSubjectWithProjects() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {

        //given
        Integer subjectDeletedId = 4;
        Integer subjectArchiveId = 0;
        subjectITTool.createSubject(subjectArchiveId, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);
        List<Project> projectsNew = new ArrayList<>();
        Project projectNew = projectITTool.createProject(1, "Tmp");
        projectsNew.add(projectNew);
        subjectITTool.createSubject(subjectDeletedId, "Abbreviation", "Teacher", projectsNew);

        //when
        subjectService.delete(subjectDeletedId);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        Assert.assertEquals(1, subjects.size());

        Subject subject = subjects.get(0);
        Assert.assertEquals(subjectArchiveId, subject.getId());

        List<Project> projects = subject.getProjects();
        Assert.assertEquals(1, projects.size());
        Assert.assertEquals(projectsNew, projects);

    }

    @Test(expected = ArchiveSubjectNotFoundException.class)
    @Transactional
    @Rollback
    public void deletingSubjectWithProjectsAndNoArchive() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {

        //given
        Integer subjectDeletedId = 4;
        List<Project> projectsNew = new ArrayList<>();
        Project projectNew = projectITTool.createProject(1, "Tmp");
        projectsNew.add(projectNew);
        subjectITTool.createSubject(subjectDeletedId, "Abbreviation", "Teacher", projectsNew);

        //when
        subjectService.delete(subjectDeletedId);

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void getSubjectsByTeacher() {

        //given
        String teacher = "Teacher1";
        subjectITTool.createSubject(1, "Abbreviation", "Teacher2");
        subjectITTool.createSubject(3, "Abbreviation2", "Teacher2");
        Subject subject2 = subjectITTool.createSubject(2, "Abbreviation1", teacher);
        Subject subject4 = subjectITTool.createSubject(4, "Abbreviation3", teacher);

        List<SubjectDto> subjectsWithTeacher1New = new ArrayList<>();
        SubjectDto subject2Dto = subjectAssembler.toDto(subject2);
        SubjectDto subject4Dto = subjectAssembler.toDto(subject4);
        subjectsWithTeacher1New.add(subject2Dto);
        subjectsWithTeacher1New.add(subject4Dto);

        //when
        List<SubjectDto> subjectsWithTeacher1 = subjectService.subjectsByTeacher(teacher);

        //then
        Assert.assertEquals(2, subjectsWithTeacher1.size());
        Assert.assertEquals(teacher, subjectsWithTeacher1.get(0).getTeacher());
        Assert.assertEquals(teacher, subjectsWithTeacher1.get(1).getTeacher());

        Assert.assertEquals(subjectsWithTeacher1New.get(0), subjectsWithTeacher1.get(0));
        Assert.assertEquals(subjectsWithTeacher1New.get(1), subjectsWithTeacher1.get(1));
    }

    @Test
    @Transactional
    @Rollback
    public void getAllSubjectsAndProjects() {

        //given
        Integer subjectId = 1;
        Integer projectId = 1;
        String abbreviation = "Abbreviation";
        String teacher = "Teacher2";
        String name = "Name";
        String comment = "Comment";
        subjectITTool.createSubject(subjectId, abbreviation, teacher);
        projectITTool.createProject(projectId, name, subjectId, comment);

        //when
        List<String> groupedList = subjectService.getAllSubjectsAndProjects();

        //then

        Assert.assertEquals(1, groupedList.size());

        String[] information = groupedList.get(0).split(",");
        Assert.assertEquals(subjectId, Integer.valueOf(information[0]));
        Assert.assertEquals(projectId, Integer.valueOf(information[1]));
        Assert.assertEquals(abbreviation, information[2]);
        Assert.assertEquals(name + "", information[3]);
        Assert.assertEquals(comment + "", information[4]);
        Assert.assertEquals(teacher + "", information[5]);
    }

    @Test(expected = SubjectNotFoundException.class)
    @Transactional
    @Rollback
    public void findNotExistingSubject() throws SubjectNotFoundException {
        //given
        Integer notExistingSubjectID = 2;

        //when
        subjectService.getSubjectById(notExistingSubjectID);

        //then
        //exception expected
    }
}