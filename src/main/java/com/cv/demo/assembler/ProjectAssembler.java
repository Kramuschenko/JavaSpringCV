package com.cv.demo.assembler;

import com.cv.demo.backend.Project;
import com.cv.demo.dto.ProjectDto;
import org.mapstruct.Mapper;

@Mapper
public interface ProjectAssembler {

    ProjectDto toDto(Project project);

    Project fromDto(ProjectDto projectDto);

}
