package com.cv.demo.tools;

import com.cv.demo.backend.Project;
import com.cv.demo.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class ProjectITTool {
    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public Project createProject(int id, String name) {
        return createProject(id, name, null, null, null, null);
    }

    @Transactional
    public Project createProject(int id, String name, int subjectId) {
        return createProject(id, name, subjectId, null, null, null);
    }

    @Transactional
    public Project createProject(int id, String name, int subjectId, String comment) {
        return createProject(id, name, subjectId, comment, null, null);
    }

    @Transactional
    public Project createProject(int id, String name, Integer subjectId, String comment, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        Project project = new Project();
        project.setId(id);
        project.setComment(comment);
        project.setCreatedAt(createdAt);
        project.setModifiedAt(modifiedAt);
        project.setName(name);
        project.setSubjectId(subjectId);

        return projectRepository.save(project);
    }
}
