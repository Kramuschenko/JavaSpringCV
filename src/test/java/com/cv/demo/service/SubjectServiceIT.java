package com.cv.demo.service;

import com.cv.demo.assembler.SubjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.Information;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.ArchiveSubjectNotFoundException;
import com.cv.demo.exception.DeletingArchiveSubjectException;
import com.cv.demo.exception.MissingSubjectDataException;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * You need to comment all records in data.sql
 */
@SpringBootTest
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
        Subject subjectArchive = subjectITTool.createSubject(0, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);
        Subject subject = subjectITTool.createSubject(4, "Abbreviation", "Teacher");
        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(subjectArchive);
        subjectList.add(subject);

        //when
        List<Subject> subjects = subjectRepository.findAll();

        //then
        assertEquals(2, subjects.size());
        assertEquals(subjectArchive, subjects.get(0));
        assertEquals(subject, subjects.get(1));
        assertEquals(subjectList, subjects);
    }

    @Test
    @Transactional
    @Rollback
    public void getSubjectById() {
        //given
        int subjectID = 4;
        String abbreviation = "Abbreviation";
        String teacher = "Teacher";

        Subject subjectNew = subjectITTool.createSubject(subjectID, abbreviation, teacher);
        SubjectDto subjectDtoNew = subjectAssembler.toDto(subjectNew);
        Optional<SubjectDto> optionalSubjectDto = Optional.of(subjectDtoNew);

        //when
        Optional<SubjectDto> subjectDtoOptional = subjectService.getSubjectById(subjectID);

        //then
        assertTrue(subjectDtoOptional.isPresent());

        SubjectDto subjectDto = subjectDtoOptional.get();

        assertEquals(subjectID, subjectDto.getId());
        assertEquals(abbreviation, subjectDto.getAbbreviation());
        assertEquals(teacher, subjectDto.getTeacher());
        assertEquals(optionalSubjectDto, subjectDtoOptional);
    }

    @Test
    @Transactional
    @Rollback
    public void creatingSubject() throws MissingSubjectDataException {
        //given
        int subjectID = 4;
        String abbreviation = "Abbreviation";
        String teacher = "Teacher";
        LocalDateTime modifiedAt = LocalDateTime.of(2022, 1, 1, 0, 0, 1);
        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);

        Subject subjectNew = subjectITTool.createSubject(subjectID, abbreviation, LocalDateTime.of(2022, 1, 1, 0, 0, 1), modifiedAt, teacher, new ArrayList<>());

        //when
        subjectService.saveOrUpdate(subjectAssembler.toDto(subjectNew));

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);
        List<Subject> subjects = subjectRepository.findAll();

        assertEquals(1, subjects.size());

        Subject subject = subjects.get(0);

        assertEquals(subjectID, subject.getId());
        assertEquals(abbreviation, subject.getAbbreviation());
        assertEquals(teacher, subject.getTeacher());
        assertTrue(before.isBefore(subject.getModifiedAt()));
        assertTrue(after.isAfter(subject.getModifiedAt()));
        assertEquals(subjectNew, subject);
    }

    @Test
    @Transactional
    @Rollback
    public void creatingSubjectWithNullIdAndNoSubjectsInRepository() throws MissingSubjectDataException {
        //given
        Integer subjectID = null;
        String abbreviation = "Abbreviation";

        SubjectDto subjectDtoNew = new SubjectDto();
        subjectDtoNew.setId(subjectID);
        subjectDtoNew.setAbbreviation(abbreviation);

        //when
        Subject subject = subjectService.saveOrUpdate(subjectDtoNew);

        //then
        assertEquals(1, subject.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void creatingSubjectWithNullIdAndWithSubjectsInRepository() throws MissingSubjectDataException {
        //given
        Integer subjectID = 1;
        String abbreviation = "Abbreviation";

        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(subjectID);
        subjectDto.setAbbreviation(abbreviation);

        Integer subjectIDNew = null;
        String abbreviationNew = "Abbreviation2";

        SubjectDto subjectDtoNew = new SubjectDto();
        subjectDtoNew.setId(subjectIDNew);
        subjectDtoNew.setAbbreviation(abbreviationNew);

        //when
        subjectService.saveOrUpdate(subjectDto);
        Subject subject = subjectService.saveOrUpdate(subjectDtoNew);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        assertEquals(2, subjects.size());
        assertEquals(2, subject.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void creatingSubjectWithoutProjectList() throws MissingSubjectDataException {
        //given
        Integer subjectID = 1;
        String abbreviation = "Abbreviation";

        SubjectDto subjectDtoNew = new SubjectDto();
        subjectDtoNew.setId(subjectID);
        subjectDtoNew.setAbbreviation(abbreviation);
        List<Project> emptyList = new ArrayList<>();

        //when
        Subject subject = subjectService.saveOrUpdate(subjectDtoNew);

        //then
        assertEquals(emptyList, subject.getProjects());
    }

    @Test
    @Transactional
    @Rollback
    public void creatingSubjectWithNullAbbreviation() {
        //given
        Integer subjectID = 1;
        String abbreviation = null;

        SubjectDto subjectDtoNew = new SubjectDto();
        subjectDtoNew.setId(subjectID);
        subjectDtoNew.setAbbreviation(abbreviation);

        try {
            //when
            subjectService.saveOrUpdate(subjectDtoNew);
            fail("Expected exception was not thrown");
        } catch (MissingSubjectDataException e) {
            //then
            assertTrue(true, "Thrown: " + e);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void updatingSubjectTest() throws MissingSubjectDataException {
        //given
        LocalDateTime createdAt = LocalDateTime.of(2020, 6, 7, 12, 30, 25);
        LocalDateTime modifiedAt = LocalDateTime.of(2022, 1, 1, 0, 0, 1);
        Subject subjectNew = subjectITTool.createSubject(4, "Abbreviation", createdAt, modifiedAt, "Teacher", new ArrayList<>());
        String abbreviationNew = "Abbreviation UPDATED";

        //when
        subjectNew.setAbbreviation(abbreviationNew);
        subjectService.saveOrUpdate(subjectAssembler.toDto(subjectNew));
        List<Subject> subjects = subjectRepository.findAll();

        //then
        assertEquals(1, subjects.size());
        Subject subject = subjects.get(0);
        assertEquals(subjectNew.getAbbreviation(), subject.getAbbreviation());
        assertEquals(subjectNew.getCreatedAt(), subject.getCreatedAt());
        assertEquals(subjectNew, subject);
    }


    @Test
    @Transactional
    @Rollback
    public void deletingNotExistingSubject() {
        //given

        try {
            //when
            subjectService.delete(6);
            fail("Expected exception was not thrown");
        } catch (SubjectNotFoundException e) {
            //then
            assertTrue(true, "Thrown: " + e);
        } catch (DeletingArchiveSubjectException | ArchiveSubjectNotFoundException e) {
            fail("Thrown: " + e);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void deletingArchiveSubject() {
        //given
        subjectITTool.createSubject(0, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);

        try {
            //when
            subjectService.delete(0);
            fail("Expected exception was not thrown");
        } catch (SubjectNotFoundException | ArchiveSubjectNotFoundException e) {
            //then
            fail("Thrown: " + e);
        } catch (DeletingArchiveSubjectException e) {
            assertTrue(true, "Thrown: " + e);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void deletingSubjectWithoutProjectsTest() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {
        //given
        subjectITTool.createSubject(4, "TEST", "TEST TEST", new ArrayList<>());

        //when
        subjectService.delete(4);
        List<Subject> subjects = subjectRepository.findAll();

        //then
        assertEquals(0, subjects.size());
    }

    @Test
    @Transactional
    @Rollback
    public void deletingSubjectWithProjects() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {

        //given
        int subjectDeletedId = 4;
        int subjectArchiveId = 0;
        subjectITTool.createSubject(subjectArchiveId, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);
        List<Project> projectsNew = new ArrayList<>();
        Project projectNew = projectITTool.createProject(1, "Tmp");
        projectsNew.add(projectNew);
        subjectITTool.createSubject(subjectDeletedId, "Abbreviation", "Teacher", projectsNew);

        //when
        subjectService.delete(subjectDeletedId);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        assertEquals(1, subjects.size());

        Subject subject = subjects.get(0);
        assertEquals(subjectArchiveId, subject.getId());

        List<Project> projects = subject.getProjects();
        assertEquals(1, projects.size());
        assertEquals(projectsNew, projects);

    }

    @Test
    @Transactional
    @Rollback
    public void deletingSubjectWithProjectsAndNoArchive() {

        //given
        int subjectDeletedId = 4;
        List<Project> projectsNew = new ArrayList<>();
        Project projectNew = projectITTool.createProject(1, "Tmp");
        projectsNew.add(projectNew);
        subjectITTool.createSubject(subjectDeletedId, "Abbreviation", "Teacher", projectsNew);

        try {
            //when
            subjectService.delete(subjectDeletedId);
            fail("Expected exception was not thrown");
        } catch (SubjectNotFoundException | DeletingArchiveSubjectException e) {
            //then
            fail("Thrown: " + e);
        } catch (ArchiveSubjectNotFoundException e) {
            assertTrue(true, "Thrown: " + e);
        }
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
        assertEquals(subjectsWithTeacher1New.size(), subjectsWithTeacher1.size());
        assertEquals(teacher, subjectsWithTeacher1.get(0).getTeacher());
        assertEquals(teacher, subjectsWithTeacher1.get(1).getTeacher());
        assertEquals(subjectsWithTeacher1New.get(0), subjectsWithTeacher1.get(0));
        assertEquals(subjectsWithTeacher1New.get(1), subjectsWithTeacher1.get(1));
    }

    @Test
    @Transactional
    @Rollback
    public void getAllAndGroup() {

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
        List<String> groupedList = subjectService.getAllAndGroup();

        //then

        assertEquals(1, groupedList.size());

        String[] information = groupedList.get(0).split(",");
        assertEquals(String.valueOf(subjectId) , information[0]);
        assertEquals(String.valueOf(projectId) , information[1]);
        assertEquals(abbreviation , information[2]);
        assertEquals(name+"" , information[3]);
        assertEquals(comment+"" , information[4]);
        assertEquals(teacher+"" , information[5]);
    }
}
