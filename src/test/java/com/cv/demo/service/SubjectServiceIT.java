package com.cv.demo.service;

import com.cv.demo.assembler.SubjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.tools.ProjectITTool;
import com.cv.demo.tools.SubjectITTool;
import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.DeletingArchiveSubjectException;
import com.cv.demo.exception.MissingSubjectDataException;
import com.cv.demo.exception.SubjectNotFoundException;
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
    public void savingSubjectTest() throws MissingSubjectDataException {
        //given
        Subject subject = subjectITTool.createSubject(4, "TEST", "TEST TEST");

        //when
        subjectService.saveOrUpdate(subjectAssembler.toDto(subject));
        List<Subject> subjects = subjectRepository.findAll();

        //then
        assertEquals(1, subjects.size());
        assertEquals(Optional.of(subject), subjectRepository.findById(4));
    }

    @Test
    @Transactional
    @Rollback
    public void updatingSubjectTest() throws MissingSubjectDataException {
        //given
        Subject subject = subjectITTool.createSubject(4, "TEST", "TEST TEST");

        //when
        subject.setAbbreviation("TEST UPDATED");
        subjectService.saveOrUpdate(subjectAssembler.toDto(subject));
        List<Subject> subjects = subjectRepository.findAll();

        //then
        assertEquals(1, subjects.size());
        assertEquals(Optional.of(subject), subjectRepository.findById(4));
    }

    @Test
    @Transactional
    @Rollback
    public void deletingSubjectWithoutProjectsTest() throws SubjectNotFoundException, DeletingArchiveSubjectException {
        //given
        subjectITTool.createSubject(4, "TEST", "TEST TEST", new ArrayList<>());

        //when
        subjectService.delete(4);

        //then
        assertEquals(Optional.empty(), subjectRepository.findById(4));
    }

    @Test
    @Transactional
    @Rollback
    public void notExistingDeletingSubjectTest() {
        //given

        try {
            //when
            subjectService.delete(6);
            fail("Expected exception was not thrown");
        } catch (SubjectNotFoundException e) {
            //then
            assertTrue(true, "Thrown: " + e);
        } catch (DeletingArchiveSubjectException e) {
            fail("Thrown: " + e);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void zeroIndexDeletingSubjectTest() {
        //given
        subjectITTool.createSubject(0, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);

        try {
            //when
            subjectService.delete(0);
            fail("Expected exception was not thrown");
        } catch (SubjectNotFoundException e) {
            //then
            fail("Thrown: " + e);
        } catch (DeletingArchiveSubjectException e) {
            assertTrue(true, "Thrown: " + e);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void getSubjectByIdTest() throws MissingSubjectDataException {
        //given
        Subject subject = subjectITTool.createSubject(4, "TEST", "TEST TEST");
        SubjectDto subjectDto = subjectAssembler.toDto(subject);

        //when
        Optional<SubjectDto> optionalSubjectDto = Optional.of(subjectDto);

        //then
        assertEquals(optionalSubjectDto, subjectService.getSubjectById(4));
    }

    @Test
    @Transactional
    @Rollback
    public void getAllSubjectsTest() {
        //given
        Subject subjectArchive = subjectITTool.createSubject(0, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);
        Subject subject = subjectITTool.createSubject(4, "TEST", "TEST TEST");
        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(subjectArchive);
        subjectList.add(subject);

        //when
        List<Subject> subjects = subjectRepository.findAll();

        //then
        assertEquals(2, subjects.size());
        assertEquals(subjectList, subjects);
    }


    @Test
    @Transactional
    @Rollback
    public void deletingSubjectWithProjects() throws SubjectNotFoundException, DeletingArchiveSubjectException {

        //given
        subjectITTool.createSubject(0, "ARCHIVE", LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1), "server", null);
        List<Project> projectsNew = new ArrayList<>();
        Project projectNew = projectITTool.createProject(1, "Tmp");
        projectsNew.add(projectNew);
        subjectITTool.createSubject(4, "TEST", "TEST TEST", projectsNew);

        //when
        subjectService.delete(4);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        log.info(subjectRepository.findAll().size());

        assertEquals(1, subjects.size());

        Subject subject = subjects.get(0);

        assertEquals(0, subject.getId());

        List<Project> projects = subject.getProjects();
        assertEquals(1 , projects.size());
        assertEquals(projectsNew , projects);

    }

}
