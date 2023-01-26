package com.cv.demo.assembler;

import com.cv.demo.backend.Project;
import com.cv.demo.dto.ProjectDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectAssembler {
    public ProjectDto toDto(Project project){

        if (null == project){
            return null;
        }

        ProjectDto projectDto = new ProjectDto();

        projectDto.setId(project.getId());
        projectDto.setName(projectDto.getName());
        projectDto.setComment(projectDto.getComment());
        projectDto.setSubjectId(projectDto.getSubjectId());

        projectDto.setCreatedAt(project.getCreatedAt());
        projectDto.setModifiedAt(project.getModifiedAt());

        return projectDto;
    }

    public Project fromDto(ProjectDto projectDto){

        Project project = new Project();

        project.setId(projectDto.getId());
        project.setName(projectDto.getName());
        project.setComment(projectDto.getComment());
        project.setSubjectId(projectDto.getId());

        project.setCreatedAt(projectDto.getCreatedAt());
        project.setModifiedAt(projectDto.getModifiedAt());

        return project;
    }

}
