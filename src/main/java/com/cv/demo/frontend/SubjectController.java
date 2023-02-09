package com.cv.demo.frontend;

import com.cv.demo.backend.Subject;
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
        log.info(subjectDto);
        Subject subject = subjectService.saveOrUpdate(subjectDto);
        Integer subjectId = subject.getId();
        Integer subjectDtoId = subjectDto.getId();
        if (String.valueOf(subjectDtoId).equals("0")) {
            throw new UpdatingArchiveSubjectException();
        } else {
            String answer = "Subject: " + (subjectDtoId == null ?
                    subjectService.getAllSubjects().size() + " added" : subjectId + " updated");
            log.info("Success: " + (subjectDtoId == null ? "Subject created" : "Subject updated (" + subjectId + ")"));
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }
    }

    @PostMapping("/subjects")
    private @ResponseBody ResponseEntity<String> saveSubjects(@RequestBody List<SubjectDto> subjectsDto) throws UpdatingArchiveSubjectException, MissingSubjectAbbreviationException {

        int size = subjectsDto.size();
        for (int i = 0; i < size; i++) {
            try {
                saveSubject(subjectsDto.get(i));
                log.info("Subject {} of {} saved successfully", (i + 1), size);
            } catch (UpdatingArchiveSubjectException | MissingSubjectAbbreviationException e) {
                errorsLog(subjectsDto, i, e);
                throw e;
            }
        }

        String answer = "Subjects added or updated";
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    private static void errorsLog(List<SubjectDto> subjectsDto, int i, Exception e) {

        if (i > 0) {
            log.error(e + "were saved only {} elements ", (i));
            log.error("Were saved only that items: " + subjectsDto.subList(0, i));
        } else {
            log.error("Subjects were not saved");
        }
    }

    @GetMapping("/subject/teacher/all/{teacherName}")
    private List<SubjectDto> subjectsByTeacher(@PathVariable("teacherName") String teacher) {
        return subjectService.subjectsByTeacher(teacher);
    }

    @GetMapping("/subjects-projects")
    private List<String> getAllSubjectsAndProjects() {
        return subjectService.getAllSubjectsAndProjects();
    }

}

