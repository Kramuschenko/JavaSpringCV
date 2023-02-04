package com.cv.demo.service;

import com.cv.demo.assembler.ProjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectDataException;
import com.cv.demo.exception.ProjectNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ProjectService {

    private final ProjectAssembler projectAssembler = Mappers.getMapper(ProjectAssembler.class);

    @Autowired
    public ProjectRepository projectRepository;

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(projectAssembler::toDto).collect(Collectors.toList());
    }

    public Optional<ProjectDto> getProjectById(int id) {
        return projectRepository.findById(id).map(projectAssembler::toDto);
    }

    @Transactional
    public void saveOrUpdate(ProjectDto projectDto) throws MissingProjectDataException {

        Integer projectId = projectDto.getId();

        validate(projectDto);

        Project projectDb;

        if (projectId == null) {
            projectDb = new Project();
            if (projectRepository.findAll().size() > 0) {
                //creating of project with generated ID
                projectDb.setId(projectRepository.generateNextProjectId());
            }else {
                //creating of the first project
                projectDb.setId(1);
            }
        } else {
            if (projectRepository.findById(projectId).isPresent()) {
                //updating project
                projectDb = projectRepository.findById(projectId).get();
                projectDb.setId(projectDto.getId());
            }else {
                //creating project with the specific id
                projectDb = new Project();
                projectDb.setId(projectDto.getId());
            }
        }

        projectDb.setName(projectDto.getName());
        projectDb.setComment(projectDto.getComment());
        projectDb.setSubjectId(projectDto.getSubjectId());
        log.info("Project {}", projectId == null ? "\"New\" has been created" : (projectId + " has been updated"));
        projectRepository.save(projectDb);
    }

    private void validate(ProjectDto projectDto) throws MissingProjectDataException {
        Integer projectId = projectDto.getId();
        if (projectDto.getName() == null) {
            log.error("{} Project {} name is null", projectId == null ? "New" : "", projectId == null ? "" : projectId);
            throw new MissingProjectDataException(("Name of project can't be null : " + (projectId == null ? "New project\n" : projectId)));
        } else if (projectDto.getSubjectId() == null) {
            log.error("{} Subject id in project {} is null", projectId == null ? "New" : "", projectId == null ? "" : projectId);
            throw new MissingProjectDataException(("Project must have subject id : " + (projectId == null ? "New project\n" : projectId)));
        }
    }


    @Transactional
    public void delete(int id) throws ProjectNotFoundException {

        if (!projectRepository.findById(id).isPresent()) {
            throw new ProjectNotFoundException();
        }

        log.info("Project {} was deleted", id);
        projectRepository.deleteById(id);
    }
}
