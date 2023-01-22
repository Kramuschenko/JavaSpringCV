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
        if (projectRepository.findById(id).isPresent())
            return projectRepository.findById(id).get();
        else
            return null;
    }

    @Transactional
    public void saveOrUpdate(Project project) {
        List<Project> tmp = subjectService.getSubjectsById(project.getSubject_id()).getProjects();

        Project project1 = getProjectsById(project.getId());

        if (project1 != null) {
            for (int i = 0; i < tmp.size(); i++) {
                if (tmp.get(i).getId() == project1.getId()) {
                    project1.setCreatedAt(tmp.get(i).getCreatedAt());
                    tmp.remove(tmp.get(i));
                }
            }

            project1.setName(project.getName());
            project1.setComment(project.getComment());
            project1.setSubject_id(project.getSubject_id());
            setModificationInformation(project1);
            System.err.println(project1);
        } else {
            project1 = new Project();
            project1.setName(project.getName());
            project1.setComment(project.getComment());
            project1.setSubject_id(project.getSubject_id());
            setCreationInformation(project1);
        }

        tmp.add(project1);
        subjectService.getSubjectsById(project.getSubject_id()).setProjects(tmp);

        projectRepository.save(project1);
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
