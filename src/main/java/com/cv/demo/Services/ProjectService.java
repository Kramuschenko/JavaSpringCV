package com.cv.demo.Services;

import com.cv.demo.db.Projects;
import com.cv.demo.db.Repository.ProjectsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service

public class ProjectService {
    @Autowired
    ProjectsRep projectsRep;
    //getting all Projects records
    public List<Projects> getAllProjects()
    {
        List<Projects> projects = new ArrayList<Projects>();
        projectsRep.findAll().forEach(Projects -> projects.add(Projects));
        return projects;
    }
    //getting a specific record
    public Projects getProjectsById(int id)
    {
        return projectsRep.findById(id).get();
    }
    public void saveOrUpdate(Projects Projects)
    {
        projectsRep.save(Projects);
    }
    //deleting a specific record
    public void delete(int id)
    {
        projectsRep.deleteById(id);
    }
}
