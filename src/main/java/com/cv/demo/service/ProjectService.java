package com.cv.demo.service;

import com.cv.demo.assembler.ProjectAssembler;
import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.dto.ProjectDto;
import com.cv.demo.exception.MissingProjectDataException;
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
    ProjectRepository projectRepository;

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(projectAssembler::toDto).collect(Collectors.toList());
    }

    public Optional<ProjectDto> getProjectById(int id) {
        return projectRepository.findById(id).map(projectAssembler::toDto);
    }

    @Transactional
    public void saveOrUpdate(ProjectDto projectDto) throws MissingProjectDataException {

        int projectId = projectDto.getId();

        validate(projectDto);

        Project projectDb = projectRepository.findById(projectId).orElseGet(Project::new);

        projectDb.setName(projectDto.getName());
        projectDb.setComment(projectDto.getComment());
        projectDb.setSubjectId(projectDto.getSubjectId());
        log.info("Project {}", projectId == 0 ? "\"New\" has been created" : (projectId + " has been updated"));
        projectRepository.save(projectDb);
    }

    private void validate(ProjectDto projectDto) throws MissingProjectDataException {
        int projectId = projectDto.getId();
        if (projectDto.getName() == null) {
            log.error("{} Project {} name is null", projectId == 0 ? "New" : "", projectId == 0 ? "" : projectId);
            throw new MissingProjectDataException(("Name of project can't be null : " + (projectId == 0 ? "New project" : projectId)));
        } else if (projectDto.getSubjectId() == 0) {
            log.error("{} Subject id in project {} is 0", projectId == 0 ? "New" : "", projectId == 0 ? "" : projectId);
            throw new MissingProjectDataException(("Project must have subject id : " + (projectId == 0 ? "New project" : projectId)));
        }
    }


    @Transactional
    public void delete(int id) {
        log.info("Project {} was deleted", id);
        projectRepository.deleteById(id);
    }
}
