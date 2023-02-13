package com.cv.demo.service;

import com.cv.demo.assembler.SubjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cv.demo.backend.Subject.ARCHIVE_SUBJECT_ID;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SubjectService {

    private final ProjectRepository projectRepository;

    private final SubjectRepository subjectRepository;

    private final SubjectAssembler subjectAssembler = Mappers.getMapper(SubjectAssembler.class);

    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subjectAssembler::toDto)
                .collect(Collectors.toList());
    }

    public SubjectDto getSubjectById(Integer id) throws SubjectNotFoundException {
        return subjectRepository.findById(id)
                .map(subjectAssembler::toDto)
                .orElseThrow(SubjectNotFoundException::new);
    }

    public List<String> getAllSubjectsAndProjects() {
        return subjectRepository.getAllSubjectsAndProjects();
    }

    public List<SubjectDto> getSubjectsByTeacher(String teacher) {
        return subjectRepository.findSubjectsByTeacher(teacher).stream()
                .map(subjectAssembler::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void saveOrUpdateSubjects(List<SubjectDto> subjectsDto) throws MissingSubjectAbbreviationException, UpdatingArchiveSubjectException, NegativeSubjectIdException {
        int size = subjectsDto.size();
        for (int i = 0; i < size; i++) {
            try {
                saveOrUpdate(subjectsDto.get(i));
                log.info("Subject {} of {} saved successfully", (i + 1), size);
            } catch (MissingSubjectAbbreviationException | UpdatingArchiveSubjectException | NegativeSubjectIdException e) {
                errorsLog(subjectsDto, i, e);
                throw e;
            }
        }
    }

    private static void errorsLog(List<SubjectDto> subjectsDto, int i, Exception e) {

        if (i > 0) {
            log.error(e + "were saved only {} elements ", (i));
            log.error("Were saved only that items: " + subjectsDto.subList(0, i));
        } else {
            log.error("Subjects were not saved");
        }
    }

    @Transactional
    public Subject saveOrUpdate(SubjectDto subjectDto) throws MissingSubjectAbbreviationException, UpdatingArchiveSubjectException, NegativeSubjectIdException {

        Integer subjectId = subjectDto.getId();

        validate(subjectDto);

        Subject subject;

        if (subjectId == null) {
            subject = new Subject();
            subjectId = subjectRepository.generateNextSubjectId();
            subject.setId(subjectId);
        } else {
            Integer finalSubjectId = subjectId;
            subject = subjectRepository.findById(subjectId).orElseGet(() -> {
                Subject subjectNew = new Subject();
                subjectNew.setId(finalSubjectId);
                return subjectNew;
            });
        }

        if (subject.getProjects() == null) {
            subject.setProjects(new ArrayList<>());
        }

        subject.setTeacher(subjectDto.getTeacher());
        subject.setAbbreviation(subjectDto.getAbbreviation());
        subject.setModifiedAt(LocalDateTime.now());

        log.info("Subject {} has been created or updated", subjectId == subjectRepository.generateNextSubjectId()-1 ? "\"New\"" : subjectId);
        log.debug("Created or updated: {}", subject);
        return subjectRepository.save(subject);
    }

    private void validate(SubjectDto subjectDto) throws MissingSubjectAbbreviationException, UpdatingArchiveSubjectException, NegativeSubjectIdException {

        Integer subjectId = subjectDto.getId();

        if (subjectId != null && subjectId < 0) {
            throw new NegativeSubjectIdException();
        }

        if (subjectDto.getAbbreviation() == null) {
            log.error("Subject abbreviation is null");
            throw new MissingSubjectAbbreviationException();
        }

        if (subjectId == ARCHIVE_SUBJECT_ID) {
            throw new UpdatingArchiveSubjectException();
        }
    }

    @Transactional
    public void delete(Integer id) throws SubjectNotFoundException, DeletingArchiveSubjectException, ArchiveSubjectNotFoundException {

        if (id == ARCHIVE_SUBJECT_ID) {
            throw new DeletingArchiveSubjectException();
        }

        Subject subject = subjectRepository.findById(id).orElseThrow(SubjectNotFoundException::new);

        if (!subject.getProjects().isEmpty()) {
            log.error("In subject you wanted to remove were projects ; Projects were moved to Archive ; Archive id : {}", ARCHIVE_SUBJECT_ID);
            moveProjectsToArchive(subject);
        }
        log.info("Subject {} was deleted", id);
        log.debug("Deleted: {}", subject);
        subjectRepository.deleteById(id);

    }

    private void moveProjectsToArchive(Subject subject) throws ArchiveSubjectNotFoundException {
        List<Project> projectsToReplace = new ArrayList<>(subject.getProjects());
        Optional<Subject> subjectArchiveOpt = subjectRepository.findById(ARCHIVE_SUBJECT_ID);

        Subject subjectArchive = subjectArchiveOpt.orElseThrow(ArchiveSubjectNotFoundException::new);

        subject.getProjects().clear();
        subjectRepository.save(subject);

        subjectArchive.getProjects().addAll(projectsToReplace);
        projectRepository.saveAll(subjectArchive.getProjects());

        log.info("Projects of removed subject {} moved to archive", subject.getId());
        log.debug("Replaced projects: {}", projectsToReplace);
    }
}
