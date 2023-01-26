package com.cv.demo.service;

import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.exception.MissingSubjectDataException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Log4j2
@Service
public class SubjectService {
    @Autowired
    SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(int id) {
        return subjectRepository.findById(id);
    }

    @Transactional
    public void saveOrUpdate(Subject subject) throws MissingSubjectDataException {

        int subjectId = subject.getId();

        if (subject.getAbbreviation() == null) {
            log.error( "{} Subject {} abbreviation is null",
                    subjectId == 0 ? "New" : "",
                    subjectId == 0 ? "" : subjectId);
            throw new MissingSubjectDataException(("Name of project can't be null : " + (subjectId == 0 ? "New project" : subjectId)));
        }

        Subject subjectDb = getSubjectById(subjectId)
                .orElseGet(Subject::new);

        subjectDb.setTeacher(subject.getTeacher());
        subjectDb.setAbbreviation(subject.getAbbreviation());
        subjectDb.setProjects(subject.getProjects());
        log.info("Subject {} has been created or updated", subjectId == 0 ? "New" : subjectId);
        subjectRepository.save(subjectDb);
    }

    @Transactional
    public void delete(int id) {
        log.error( "Subject {} has been deleted", id);
        subjectRepository.deleteById(id);
    }

    public List<Subject> subjectsByTeacher(String teacher) {
        return subjectRepository.findSubjectsByTeacher(teacher);
    }

    public Subject firstByTeacher(String teacher) {
        return subjectRepository.findFirstByTeacher(teacher);
    }
}
