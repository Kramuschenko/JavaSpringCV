package com.cv.demo.service;

import com.cv.demo.assembler.SubjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.Information;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.ArchiveSubjectNotFoundException;
import com.cv.demo.exception.DeletingArchiveSubjectException;
import com.cv.demo.exception.MissingSubjectDataException;
import com.cv.demo.exception.SubjectNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Log4j2
@Service
public class SubjectService {
    @Autowired
    private ProjectRepository projectRepository;
    private final SubjectAssembler subjectAssembler = Mappers.getMapper(SubjectAssembler.class);

    @Autowired
    SubjectRepository subjectRepository;

    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream().map(subjectAssembler::toDto).collect(Collectors.toList());
    }

    public Optional<SubjectDto> getSubjectById(int id) {
        return subjectRepository.findById(id).map(subjectAssembler::toDto);
    }

    public List<String> getAllAndGroup(){
        return subjectRepository.getAllAndGroup();
    }

    @Transactional
    public Subject saveOrUpdate(SubjectDto subjectDto) throws MissingSubjectDataException {

        Integer subjectId = subjectDto.getId();

        validate(subjectDto);

        Subject subject;

        if (subjectId == null) {
            subject = new Subject();
            if (subjectRepository.findAll().size() > 0) {
                //creating subject with generated id
                Integer id = subjectRepository.generateNextSubjectId();
                subjectId = id;
                subject.setId(id);
            } else {
                //creating first subject
                subjectId = 1;
                subject.setId(subjectId);
            }

        } else {
            if (subjectRepository.findById(subjectId).isPresent()) {
                //updating subject
                subject = subjectRepository.findById(subjectId).get();
                subject.setId(subjectId);
            } else {
                //creating subject with specific id
                subject = new Subject();
                subject.setId(subjectId);
            }

        }

        if (subject.getProjects() == null) {
            subject.setProjects(new ArrayList<>());
        }

        subject.setTeacher(subjectDto.getTeacher());
        subject.setAbbreviation(subjectDto.getAbbreviation());

        log.info("Subject {} has been created or updated", subjectId == 0 ? "\"New\"" : subjectId);
        return subjectRepository.save(subject);
    }

    private void validate(SubjectDto subjectDto) throws MissingSubjectDataException {
        if (subjectDto.getAbbreviation() == null) {
            log.error("Subject abbreviation is null");
            throw new MissingSubjectDataException(("Name of project can't be null\n"));
        }
    }

    @Transactional
    public void delete(int id) throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {

        Subject subject = subjectRepository.findById(id).orElseThrow(SubjectNotFoundException::new);

        if (id == 0) {
            throw new DeletingArchiveSubjectException();
        }

        if (subject.getProjects().isEmpty()) {
            log.info("Subject {} was deleted", id);
            subjectRepository.deleteById(id);
        } else {
            log.error("In subject you wanted to remove were projects ; Projects were moved to Archive ; Archive id : 0");

            moveProjectsToArchive(subject);

            log.info("Subject {} was deleted", id);
            subjectRepository.deleteById(id);
        }

    }

    private void moveProjectsToArchive(Subject subject) throws ArchiveSubjectNotFoundException {
        List<Project> projectsToReplace = new ArrayList<>(subject.getProjects());
        Optional<Subject> subjectArchive = subjectRepository.findById(0);

        subject.getProjects().clear();
        subjectRepository.save(subject);
        if (subjectArchive.isPresent()) {
            List<Project> archiveProjects = subjectArchive.get().getProjects();
            archiveProjects.addAll(projectsToReplace);
            subjectArchive.get().setProjects(archiveProjects);

            projectRepository.saveAll(archiveProjects);

            log.info("Projects replaced: " + archiveProjects);
        } else {
            log.error("There is no Archive of projects in database");
            throw new ArchiveSubjectNotFoundException();
        }
    }

    public List<SubjectDto> subjectsByTeacher(String teacher) {
        return subjectRepository.findSubjectsByTeacher(teacher).stream().map(subjectAssembler::toDto).collect(Collectors.toList());
    }
}
