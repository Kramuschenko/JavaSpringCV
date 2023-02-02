package com.cv.demo.assembler;

import com.cv.demo.backend.Project;
import com.cv.demo.dto.ProjectDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectAssemblerTest {

    private final ProjectAssembler mapper = Mappers.getMapper(ProjectAssembler.class);

    @Test
    public void projectDtoToProjectTest() {
        ProjectDto dto = new ProjectDto();
        dto.setId(1);
        dto.setName("John");
        dto.setComment("John");
        dto.setSubjectId(1);

        Project entity = mapper.fromDto(dto);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getComment(), dto.getComment());
        assertEquals(entity.getSubjectId(), dto.getSubjectId());
    }

    @Test
    public void projectToProjectDtoTest() {
        Project entity = new Project();
        entity.setId(1);
        entity.setName("John");
        entity.setComment("John");
        entity.setSubjectId(1);

        ProjectDto dto = mapper.toDto(entity);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getComment(), entity.getComment());
        assertEquals(dto.getSubjectId(), entity.getSubjectId());
    }

    @Test
    public void shouldHandleNullProject() {
        Project project = null;

        ProjectDto projectDto = mapper.toDto(project);

        Assertions.assertNull(projectDto);
    }
}
