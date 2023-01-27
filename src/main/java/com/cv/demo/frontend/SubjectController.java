package com.cv.demo.frontend;

import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.DeletingProjectException;
import com.cv.demo.exception.MissingSubjectDataException;
import com.cv.demo.service.ProjectService;
import com.cv.demo.service.SubjectService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/subject")
    private List<SubjectDto> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/subject/id/{id}")
    private SubjectDto getSubject(@PathVariable("id") int id) {
        return subjectService.getSubjectById(id).orElse(null);
    }

    @DeleteMapping("/subject/id/{id}")
    private @ResponseBody ResponseEntity<String> deleteSubject(@PathVariable("id") int id) throws DeletingProjectException {
        log.info("Subject {} will be deleted", id);
        subjectService.delete(id);
        return new ResponseEntity<>("Subject deleted", HttpStatus.OK);
    }

    @PostMapping("/subject")
    private @ResponseBody ResponseEntity<String> saveSubject(@RequestBody SubjectDto subjectDto) throws MissingSubjectDataException {
        log.info(subjectDto);
        subjectService.saveOrUpdate(subjectDto);
        String answer = "Subject: " + (subjectDto.getId() == 0 ?
                subjectService.getAllSubjects().size() + " added" : subjectDto.getId() + " updated");
        log.info("Success: " + (subjectDto.getId() == 0 ? "Subject created" : "Subject updated (" + subjectDto.getId() + ")"));
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @GetMapping("/subject/teacher/{teacherName}")
    private SubjectDto firstByTeacher(@PathVariable("teacherName") String teacher) {
        return subjectService.firstByTeacher(teacher);
    }

    @GetMapping("/subject/teacher/all/{teacherName}")
    private List<SubjectDto> subjectsByTeacher(@PathVariable("teacherName") String teacher) {
        return subjectService.subjectsByTeacher(teacher);
    }
}
