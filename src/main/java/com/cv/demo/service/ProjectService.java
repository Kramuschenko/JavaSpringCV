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

    public List<ProjectDto> getProjectsBySubjectId(Integer id) throws SubjectNotFoundException {
        subjectRepository.findById(id).orElseThrow(SubjectNotFoundException::new);

        return projectRepository.getProjectBySubjectId(id).stream()
                .map(projectAssembler::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveOrUpdateProjects(List<ProjectDto> projectsDto) throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException {
        int size = projectsDto.size();
        for (int i = 0; i < size; i++) {

            try {
                saveOrUpdate(projectsDto.get(i));
                log.info("Project {} of {} saved successfully", (i + 1), size);
            } catch (MissingProjectNameException | MissingProjectSubjectIdException e) {
                errorsLog(projectsDto, i, e);
                throw e;
            }
        }
    }

    private static void errorsLog(List<ProjectDto> projectsDto, int i, Exception e) {
        if (i > 0) {
            log.error(e + "were saved only {} elements ", (i));
            log.error("Were saved only that items: " + projectsDto.subList(0, i));
        } else {
            log.error("Subjects were not saved");
        }
    }

    @Transactional
    public void saveOrUpdate(ProjectDto projectDto) throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException {

        Integer projectId = projectDto.getId();

        validate(projectDto);

        Project project;

        if (projectId == null) {
            project = new Project();
            projectId = projectRepository.generateNextProjectId();
            project.setId(projectId);
        } else {
            Integer finalProjectId = projectId;
            project = projectRepository.findById(projectId).orElseGet(() -> {
                Project projectNew = new Project();
                projectNew.setId(finalProjectId);
                return projectNew;
            });
        }

        project.setName(projectDto.getName());
        project.setComment(projectDto.getComment());
        project.setSubjectId(projectDto.getSubjectId());
        project.setModifiedAt(LocalDateTime.now());

        log.info("Project {} has been created or updated", projectId);
        log.debug("Created or updated: {}", project);
        projectRepository.save(project);
    }

    private void validate(ProjectDto projectDto) throws MissingProjectNameException, MissingProjectSubjectIdException, SubjectNotFoundException {
        Integer projectId = projectDto.getId();

        if (projectDto.getSubjectId() == null) {
            log.error("{} Subject id in project {} is null", projectId == null ? "New" : "", projectId == null ? "" : projectId);
            throw new MissingProjectSubjectIdException();
        }

        if (projectDto.getName() == null) {
            log.error("{} Project {} name is null", projectId == null ? "New" : "", projectId == null ? "" : projectId);
            throw new MissingProjectNameException();
        }
        subjectRepository.findById(projectDto.getSubjectId()).orElseThrow(SubjectNotFoundException::new);

    }


    @Transactional
    public void delete(int id) throws ProjectNotFoundException {
        Project project = projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);

        log.info("Project {} was deleted", id);
        log.debug("Deleted: {}", project);
        projectRepository.delete(project);
    }
}
