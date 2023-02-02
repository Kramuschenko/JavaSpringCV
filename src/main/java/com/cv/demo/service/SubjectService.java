package com.cv.demo.service;

import com.cv.demo.assembler.SubjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.dto.SubjectDto;
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

    @Transactional
    public Subject saveOrUpdate(SubjectDto subjectDto) throws MissingSubjectDataException {

        Integer subjectId = subjectDto.getId();

        validate(subjectDto);

        Subject subject;

        if (subjectId == null) {
            subject = new Subject();
            Integer id = subjectRepository.generateNextSubjectId();
            subjectId = id;
            subject.setId(id);
        } else {
            subject = subjectRepository.findById(subjectId).get();
            subject.setId(subjectId);
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
    public void delete(int id) throws SubjectNotFoundException, DeletingArchiveSubjectException {if (id == 0) {
            throw new DeletingArchiveSubjectException();
        }

        Subject subject = subjectRepository.findById(id).orElseThrow(SubjectNotFoundException::new);

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

    private void moveProjectsToArchive(Subject subject) {
        List<Project> projectsToReplace = new ArrayList<>(subject.getProjects());
        Optional<Subject> subjectArchive = subjectRepository.findById(0);

        subject.getProjects().clear();
        subjectRepository.save(subject);

        List<Project> archiveProjects = subjectArchive.get().getProjects();
        archiveProjects.addAll(projectsToReplace);
        subjectArchive.get().setProjects(archiveProjects);

        projectRepository.saveAll(archiveProjects);

        log.info("Projects replaced: " + archiveProjects);
    }

    public List<SubjectDto> subjectsByTeacher(String teacher) {
        return subjectRepository.findSubjectsByTeacher(teacher).stream().map(subjectAssembler::toDto).collect(Collectors.toList());
    }

    public SubjectDto firstByTeacher(String teacher) {
        return subjectAssembler.toDto(subjectRepository.findFirstByTeacher(teacher));
    }
}
