package com.cv.demo.Services;

import com.cv.demo.db.Project;
import com.cv.demo.db.Repository.ProjectRepository;
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

    public Optional<Project> getProjectsById(int id) {
        return projectRepository.findById(id);
    }

    @Transactional
    public void saveOrUpdate(Project project) {
        Project projectDb = getProjectsById(project.getId())
                .orElseGet(Project::new);

        projectDb.setName(project.getName());
        projectDb.setComment(project.getComment());
        projectDb.setSubjectId(project.getSubjectId());
        log.atInfo().log("Project {} has been created or updated", projectDb.getId());
        projectRepository.save(projectDb);
    }


    @Transactional
    public void delete(int id) {
        projectRepository.deleteById(id);
    }

}
