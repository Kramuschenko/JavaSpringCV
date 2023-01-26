package com.cv.demo.service;

import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.exception.MissingProjectDataException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    SubjectService subjectService;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(int id) {
        return projectRepository.findById(id);
    }

    @Transactional
    public void saveOrUpdate(Project project) throws MissingProjectDataException {

        int projectId = project.getId();

        if (project.getName() == null) {
            log.error("{} Project {} name is null", projectId == 0 ? "New" : "", projectId == 0 ? "" : projectId);
            throw new MissingProjectDataException(("Name of project can't be null : " + (projectId == 0 ? "New project" : projectId)));
        } else if (project.getSubjectId() == 0) {
            log.error("{} Subject id in project {} is 0", projectId == 0 ? "New" : "", projectId == 0 ? "" : projectId);
            throw new MissingProjectDataException(("Project must have subject id : " + (projectId == 0 ? "New project" : projectId)));
        }


        Project projectDb = getProjectById(projectId).orElseGet(Project::new);

        projectDb.setName(project.getName());
        projectDb.setComment(project.getComment());
        projectDb.setSubjectId(project.getSubjectId());
        log.info("Project {} has been created or updated", projectId == 0 ? "\"New\"" : projectId);
        projectRepository.save(projectDb);
    }


    @Transactional
    public void delete(int id) {

        log.info("Project {} has been created or updated", id);
        projectRepository.deleteById(id);
    }
}
