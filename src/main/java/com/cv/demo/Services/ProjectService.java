package com.cv.demo.Services;

import com.cv.demo.db.Project;
import com.cv.demo.db.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    SubjectService subjectService;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectsById(int id) {
        return projectRepository.findById(id).get();
    }

    @Transactional
    public void saveOrUpdate(Project project) {
        List<Project> tmp = subjectService.getSubjectsById(project.getSubject_id()).getProjects();
        tmp.add(project);
        subjectService.getSubjectsById(project.getSubject_id()).setProjects(tmp);
        if (project.getCreatedAt() == null)
            setCreationInformation(project);
        else
            setModificationInformation(project);
        projectRepository.save(project);
    }

    @Transactional
    public void delete(int id) {
        projectRepository.deleteById(id);
    }

    public static void setCreationInformation(Project project) {
        project.setCreatedAt(LocalDateTime.now());
    }

    public static void setModificationInformation(Project project) {
        project.setModifiedAt(LocalDateTime.now());
    }
}
