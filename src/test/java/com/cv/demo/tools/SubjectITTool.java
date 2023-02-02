package com.cv.demo.tools;

import com.cv.demo.backend.Project;
import com.cv.demo.backend.Subject;
import com.cv.demo.backend.repository.ProjectRepository;
import com.cv.demo.backend.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SubjectITTool {

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public Subject createSubject(int id , String abbreviation) {
        return createSubject(id , abbreviation , null , null ,null , null);
    }

    @Transactional
    public Subject createSubject(int id , String abbreviation , String teacher) {
        return createSubject(id , abbreviation , null , null ,teacher , null);
    }

    @Transactional
    public Subject createSubject(int id , String abbreviation , String teacher , List<Project> projects) {
        return createSubject(id , abbreviation , null , null ,teacher , projects);
    }

    @Transactional
    public Subject createSubject(int id, String abbreviation , LocalDateTime createdAt, LocalDateTime modifiedAt, String teacher , List<Project> projects) {
        Subject subject = new Subject();

        subject.setId(id);
        subject.setAbbreviation(abbreviation);
        subject.setCreatedAt(createdAt);
        subject.setModifiedAt(modifiedAt);
        subject.setTeacher(teacher);

        if (projects == null) {
            projects = new ArrayList<>();
        }
        subject.setProjects(projects);

        return subjectRepository.save(subject);
    }

}
