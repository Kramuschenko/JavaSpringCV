package com.cv.demo.service;

import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.ArchiveSubjectNotFoundException;
import com.cv.demo.exception.DeletingArchiveSubjectException;
import com.cv.demo.exception.MissingSubjectAbbreviationException;
import com.cv.demo.exception.SubjectNotFoundException;
import com.cv.demo.exception.UpdatingArchiveSubjectException;
import com.cv.demo.tools.ProjectITTool;
import com.cv.demo.tools.SubjectITTool;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.cv.demo.backend.Subject.ARCHIVE_SUBJECT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private SubjectDto createSubjectDto(Integer subjectId, String abbreviation, String teacher) {
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(subjectId);
        subjectDto.setTeacher(teacher);
        subjectDto.setAbbreviation(abbreviation);
        return subjectDto;
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllSubjects() {
        //given
        Integer secondId = 4;
        subjectITTool.createSubject(ARCHIVE_SUBJECT_ID, "ARCHIVE", "server", null, LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1));
        subjectITTool.createSubject(secondId, "Abbreviation", "Teacher");

        //when
        List<SubjectDto> subjectsDto = subjectService.getAllSubjects();

        //then
        assertEquals(2, subjectsDto.size());

        List<Integer> id = new ArrayList<>();
        id.add(subjectsDto.get(0).getId());
        id.add(subjectsDto.get(1).getId());

        assertTrue(id.contains(ARCHIVE_SUBJECT_ID));
        assertTrue(id.contains(secondId));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldReturnEmptyListWhenFindAllSubjects() {
        //given

        //when
        List<SubjectDto> subjectsDto = subjectService.getAllSubjects();

        //then
        assertTrue(subjectsDto.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindSubjectById() throws SubjectNotFoundException {
        //given
        Integer subjectID = 4;
        String abbreviation = "Abbreviation";
        String teacher = "Teacher";
        List<Project> projects = new ArrayList<>();

        subjectITTool.createSubject(subjectID, abbreviation, teacher, projects);

        //when
        SubjectDto subjectDto = subjectService.getSubjectById(subjectID);

        //then
        assertEquals(subjectID, subjectDto.getId());
        assertEquals(abbreviation, subjectDto.getAbbreviation());
        assertEquals(teacher, subjectDto.getTeacher());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindAllSubjectsAndProjects() {

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

        assertEquals(1, groupedList.size());

        String[] information = groupedList.get(0).split(",");
        assertEquals(subjectId, Integer.valueOf(information[0]));
        assertEquals(projectId, Integer.valueOf(information[1]));
        assertEquals(abbreviation, information[2]);
        assertEquals(name, information[3]);
        assertEquals(comment, information[4]);
        assertEquals(teacher, information[5]);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldReturnEmptyListWhenFindAllSubjectsAndProjects() {

        //given

        //when
        List<String> groupedList = subjectService.getAllSubjectsAndProjects();

        //then
        assertTrue(groupedList.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldFindSubjectsByTeacher() {

        //given
        Integer subject1Id = 2;
        Integer subject2Id = 4;
        String teacher = "Teacher1";
        subjectITTool.createSubject(1, "Abbreviation", "Teacher2");
        subjectITTool.createSubject(3, "Abbreviation2", "Teacher2");
        subjectITTool.createSubject(subject1Id, "Abbreviation1", teacher);
        subjectITTool.createSubject(subject2Id, "Abbreviation3", teacher);
        //when
        List<SubjectDto> subjectsWithTeacher = subjectService.getSubjectsByTeacher(teacher);

        //then
        assertEquals(2, subjectsWithTeacher.size());


        List<Integer> id = new ArrayList<>();
        id.add(subjectsWithTeacher.get(0).getId());
        id.add(subjectsWithTeacher.get(1).getId());

        assertTrue(id.contains(subject1Id));
        assertTrue(id.contains(subject2Id));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldReturnEmptyListWhenFindSubjectsByTeacher() {

        //given
        Integer subject1Id = 1;
        Integer subject2Id = 3;
        String teacher = "Teacher1";
        subjectITTool.createSubject(subject1Id, "Abbreviation", "Teacher2");
        subjectITTool.createSubject(subject2Id, "Abbreviation2", "Teacher2");

        //when
        List<SubjectDto> subjectsWithTeacher = subjectService.getSubjectsByTeacher(teacher);

        //then
        assertTrue(subjectsWithTeacher.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotFindNotExistingSubject() {
        //given
        Integer notExistingSubjectID = 2;

        //when
        assertThrows(SubjectNotFoundException.class, () -> {
            subjectService.getSubjectById(notExistingSubjectID);
        });

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateSubjectWithSpecificSubjectId() throws MissingSubjectAbbreviationException, UpdatingArchiveSubjectException {
        //given
        Integer subjectID = 4;
        String abbreviation = "Abbreviation";
        String teacher = "Teacher";
        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);

        SubjectDto subjectDto = createSubjectDto(subjectID, abbreviation, teacher);

        //when
        subjectService.saveOrUpdate(subjectDto);

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);
        List<Subject> subjects = subjectRepository.findAll();

        assertEquals(1, subjects.size());

        Subject subject = subjects.get(0);

        assertEquals(subjectID, subject.getId());
        assertEquals(abbreviation, subject.getAbbreviation());
        assertEquals(teacher, subject.getTeacher());
        assertTrue(before.isBefore(subject.getCreatedAt()));
        assertTrue(after.isAfter(subject.getCreatedAt()));
        assertTrue(before.isBefore(subject.getModifiedAt()));
        assertTrue(after.isAfter(subject.getModifiedAt()));
        assertTrue(subject.getProjects().isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateSubjectWithFirstIdGenerated() throws MissingSubjectAbbreviationException, UpdatingArchiveSubjectException {
        //given
        Integer subjectID = null;
        String abbreviation = "Abbreviation";
        Integer expectedId = 1;

        SubjectDto subjectDto = createSubjectDto(subjectID, abbreviation, null);

        //when
        subjectService.saveOrUpdate(subjectDto);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        assertEquals(1, subjects.size());
        assertEquals(expectedId, subjects.get(0).getId());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldCreateSubjectWithConsecutiveIdGenerated() throws MissingSubjectAbbreviationException, UpdatingArchiveSubjectException {
        //given
        Integer subjectID = 1;
        String abbreviation = "Abbreviation";

        subjectITTool.createSubject(subjectID, abbreviation);

        Integer subjectIDNew = null;
        String abbreviationNew = "Abbreviation2";
        Integer expectedId = 2;

        SubjectDto subjectDtoNew = createSubjectDto(subjectIDNew, abbreviationNew, null);

        //when
        Subject subject = subjectService.saveOrUpdate(subjectDtoNew);

        //then
        List<Subject> subjects = subjectRepository.findAll();
        assertEquals(2, subjects.size());
        assertEquals(expectedId, subject.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotCreateSubjectWithNullAbbreviation() {
        //given
        Integer subjectID = 1;
        String abbreviation = null;

        SubjectDto subjectDtoNew = createSubjectDto(subjectID, abbreviation, null);

        //when
        assertThrows(MissingSubjectAbbreviationException.class, () -> {
            subjectService.saveOrUpdate(subjectDtoNew);
        });

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdateSubject() throws MissingSubjectAbbreviationException, UpdatingArchiveSubjectException {
        //given
        Integer subjectId = 4;
        Integer projectId = 1;
        String teacher = "Teacher";
        String abbreviation = "Abbreviation";
        String abbreviationNew = "Abbreviation UPDATED";

        Project projectNew = projectITTool.createProject(projectId, "name");

        List<Project> projectsNew = new ArrayList<>();
        projectsNew.add(projectNew);

        LocalDateTime createdAt = LocalDateTime.of(2020, 6, 7, 12, 30, 25);
        LocalDateTime modifiedAt = LocalDateTime.of(2022, 1, 1, 0, 0, 1);

        subjectITTool.createSubject(subjectId, abbreviation, teacher, projectsNew, createdAt, modifiedAt);

        SubjectDto subjectDto = createSubjectDto(subjectId, abbreviationNew, teacher);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1L);

        //when
        subjectService.saveOrUpdate(subjectDto);

        //then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1L);

        List<Subject> subjects = subjectRepository.findAll();
        assertEquals(1, subjects.size());

        Subject subject = subjects.get(0);

        assertEquals(subjectId, subject.getId());
        assertEquals(abbreviationNew, subject.getAbbreviation());
        assertTrue(before.isBefore(subject.getModifiedAt()));
        assertTrue(after.isAfter(subject.getModifiedAt()));

        List<Project> projects = subject.getProjects();
        assertEquals(1, projects.size());
        assertEquals(projectId, projects.get(0).getId());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldDeleteSubjectWithoutProjects() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {
        //given
        subjectITTool.createSubject(4, "TEST", "TEST TEST", new ArrayList<>());

        //when
        subjectService.delete(4);

        //then
        long count = subjectRepository.count();
        assertEquals(0, count);
    }

    @Test
    @Transactional
    @Rollback
    public void shouldDeleteSubjectWithProjects() throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {

        //given
        Integer subjectDeletedId = 4;
        subjectITTool.createSubject(ARCHIVE_SUBJECT_ID, "ARCHIVE", "server", null, LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1));
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
        assertEquals(ARCHIVE_SUBJECT_ID, subject.getId());

        List<Project> projects = subject.getProjects();
        assertEquals(1, projects.size());

        Project project = projects.get(0);
        assertEquals(projectNew.getId(), project.getId());
        assertEquals(projectNew.getName(), project.getName());

    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotDeleteNotExistingSubject() {
        //given
        int notExistingSubjectId = 6;

        //when

        assertThrows(SubjectNotFoundException.class, () -> {
            subjectService.delete(notExistingSubjectId);
        });

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotDeleteArchiveSubject() {
        //given
        subjectITTool.createSubject(ARCHIVE_SUBJECT_ID, "ARCHIVE", "server", null, LocalDateTime.of(2023, 1, 1, 0, 0, 1), LocalDateTime.of(2023, 1, 1, 0, 0, 1));

        //when
        assertThrows(DeletingArchiveSubjectException.class, () -> {
            subjectService.delete(ARCHIVE_SUBJECT_ID);
        });

        //then
        //exception expected
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotDeleteSubjectWithProjectsAndNoArchive() {

        //given
        Integer subjectDeletedId = 4;
        Project projectNew = projectITTool.createProject(1, "Tmp");

        List<Project> projectsNew = new ArrayList<>();
        projectsNew.add(projectNew);

        subjectITTool.createSubject(subjectDeletedId, "Abbreviation", "Teacher", projectsNew);

        //when
        assertThrows(ArchiveSubjectNotFoundException.class, () -> {
            subjectService.delete(subjectDeletedId);
        });

        //then
        //exception expected
    }
}