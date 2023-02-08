package com.cv.demo.service;

import com.cv.demo.assembler.ProjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.backend.repository.SubjectRepository;
import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectNameException;
import com.cv.demo.exception.MissingProjectSubjectIdException;
import com.cv.demo.exception.ProjectNotFoundException;
import com.cv.demo.exception.SubjectNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProjectService {

    private final SubjectRepository subjectRepository;

    private final ProjectAssembler projectAssembler = Mappers.getMapper(ProjectAssembler.class);

    private final ProjectRepository projectRepository;

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectAssembler::toDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(int id) throws ProjectNotFoundException {
        return projectRepository.findById(id)
                .map(projectAssembler::toDto)
                .orElseThrow(ProjectNotFoundException::new);
    }

    public List<ProjectDto> getProjectBySubjectId(Integer id) throws SubjectNotFoundException {
        subjectRepository.findById(id).orElseThrow(SubjectNotFoundException::new);

        return projectRepository.getProjectBySubjectId(id).stream()
                .map(projectAssembler::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveOrUpdate(ProjectDto projectDto) throws MissingProjectNameException, MissingProjectSubjectIdException {

        Integer projectId = projectDto.getId();

        validate(projectDto);

        Project projectDb;

        if (projectId == null) {
            projectDb = new Project();
            projectDb.setId(projectRepository.generateNextProjectId());
        } else {
            projectDb = projectRepository.findById(projectId).orElseGet( () -> {
                Project projectNew = new Project();
                projectNew.setId(projectId);
                return projectNew;
            });
        }

        projectDb.setName(projectDto.getName());
        projectDb.setComment(projectDto.getComment());
        projectDb.setSubjectId(projectDto.getSubjectId());
        projectDb.setModifiedAt(LocalDateTime.now());

        log.info("Project {}", projectId == null ? "\"New\" has been created" : (projectId + " has been updated"));
        projectRepository.save(projectDb);
    }

    private void validate(ProjectDto projectDto) throws MissingProjectNameException, MissingProjectSubjectIdException {
        Integer projectId = projectDto.getId();

        if (projectDto.getName() == null) {
            log.error("{} Project {} name is null", projectId == null ? "New" : "", projectId == null ? "" : projectId);
            throw new MissingProjectNameException();
        }

        if (projectDto.getSubjectId() == null) {
            log.error("{} Subject id in project {} is null", projectId == null ? "New" : "", projectId == null ? "" : projectId);
            throw new MissingProjectSubjectIdException();
        }
    }


    @Transactional
    public void delete(int id) throws ProjectNotFoundException {
       Project project = projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);

        log.info("Project {} was deleted", id);
        projectRepository.delete(project);
    }
}
