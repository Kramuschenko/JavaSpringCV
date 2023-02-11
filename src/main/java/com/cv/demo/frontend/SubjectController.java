package com.cv.demo.frontend;

import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.ArchiveSubjectNotFoundException;
import com.cv.demo.exception.DeletingArchiveSubjectException;
import com.cv.demo.exception.MissingSubjectAbbreviationException;
import com.cv.demo.exception.SubjectNotFoundException;
import com.cv.demo.exception.UpdatingArchiveSubjectException;
import com.cv.demo.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@CrossOrigin(maxAge = 3600)
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/subject")
    private List<SubjectDto> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/subject/id/{id}")
    private SubjectDto getSubject(@PathVariable("id") int id) throws SubjectNotFoundException {
        return subjectService.getSubjectById(id);
    }

    @DeleteMapping("/subject/id/{id}")
    private @ResponseBody ResponseEntity<String> deleteSubject(@PathVariable("id") int id) throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {
        log.info("Subject {} will be deleted", id);
        subjectService.delete(id);
        return new ResponseEntity<>("Subject deleted", HttpStatus.OK);
    }

    @PostMapping("/subject")
    private @ResponseBody ResponseEntity<String> saveSubject(@RequestBody SubjectDto subjectDto) throws MissingSubjectAbbreviationException, UpdatingArchiveSubjectException {
        subjectService.saveOrUpdate(subjectDto);
        Integer subjectId = subjectDto.getId();

        String answer = "Subject: " + (subjectId == null ? " added" : subjectId + " updated");
        log.info("Success: " + answer);
        log.debug("Saved or updated: " + subjectDto);
        return new ResponseEntity<>(answer, HttpStatus.OK);

    }

    @PostMapping("/subjects")
    private @ResponseBody ResponseEntity<String> saveSubjects(@RequestBody List<SubjectDto> subjectsDto) throws UpdatingArchiveSubjectException, MissingSubjectAbbreviationException {

        subjectService.saveOrUpdateSubjects(subjectsDto);
        String answer = "Subjects added or updated";
        log.info("Success: " + answer);
        log.debug("Saved or updated: " + subjectsDto);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @GetMapping("/subject/teacher/all/{teacherName}")
    private List<SubjectDto> subjectsByTeacher(@PathVariable("teacherName") String teacher) {
        return subjectService.getSubjectsByTeacher(teacher);
    }

    @GetMapping("/subjects-projects")
    private List<String> getAllSubjectsAndProjects() {
        return subjectService.getAllSubjectsAndProjects();
    }

}

