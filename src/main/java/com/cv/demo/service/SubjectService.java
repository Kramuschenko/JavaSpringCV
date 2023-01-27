package com.cv.demo.service;

import com.cv.demo.assembler.SubjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.dto.SubjectDto;
import com.cv.demo.exception.DeletingProjectException;
import com.cv.demo.exception.MissingSubjectDataException;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Log4j2
@Service
public class SubjectService {
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
    public void saveOrUpdate(SubjectDto subjectDto) throws MissingSubjectDataException {

        int subjectId = subjectDto.getId();

        validate(subjectDto);

        Subject subject = subjectRepository.findById(subjectId).orElseGet(Subject::new);

        subject.setTeacher(subjectDto.getTeacher());
        subject.setAbbreviation(subjectDto.getAbbreviation());

        log.info("Subject {} has been created or updated", subjectId == 0 ? "\"New\"" : subjectId);
        subjectRepository.save(subject);
    }

    private void validate(SubjectDto subjectDto) throws MissingSubjectDataException {
        int subjectId = subjectDto.getId();
        if (subjectDto.getAbbreviation() == null) {
            log.error("{} Subject {} abbreviation is null",
                    subjectId == 0 ? "New" : "",
                    subjectId == 0 ? "" : subjectId);
            throw new MissingSubjectDataException(("Name of project can't be null : " + (subjectId == 0 ? "New project" : subjectId)));
        }
    }

    @Transactional
    public void delete(int id) throws DeletingProjectException {

        Optional<Subject> subject = subjectRepository.findById(id);

        if (subject.isPresent()){
            if (id != 0) {
                if (subject.get().getProjects().size() > 0) {
                    log.error("In subject you wanted to remove were projects \n" +
                            "Projects were moved to Archive \n" +
                            "Archive id : 0");
                    List<Project> projectsToReplace = subject.get().getProjects();
                    for (Project project : projectsToReplace){
                        project.setSubjectId(0);
                    }
                    log.info("Subject {} was deleted", id);
                    subjectRepository.deleteById(id);
                } else {
                    log.info("Subject {} was deleted", id);
                    subjectRepository.deleteById(id);
                }
            }else {
                throw new DeletingProjectException("You can't remove Archive of subjects");
            }
        }else {
            throw new DeletingProjectException("There is no id of this subject in database");
        }


    }

    public List<SubjectDto> subjectsByTeacher(String teacher) {
        return subjectRepository.findSubjectsByTeacher(teacher).stream().map(subjectAssembler::toDto).collect(Collectors.toList());
    }

    public SubjectDto firstByTeacher(String teacher) {
        return subjectAssembler.toDto(subjectRepository.findFirstByTeacher(teacher));
    }
}
