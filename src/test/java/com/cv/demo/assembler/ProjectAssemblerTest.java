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
        //given
        ProjectDto dto = new ProjectDto();
        dto.setId(1);
        dto.setName("John");
        dto.setComment("Clerk");
        dto.setSubjectId(2);

        //when
        Project entity = mapper.fromDto(dto);

        //then
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getComment(), entity.getComment());
        assertEquals(dto.getSubjectId(), entity.getSubjectId());
    }

    @Test
    public void projectToProjectDtoTest() {
        //given
        Project entity = new Project();
        entity.setId(1);
        entity.setName("John");
        entity.setComment("Clerk");
        entity.setSubjectId(2);

        //when
        ProjectDto dto = mapper.toDto(entity);

        //then
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getComment(), dto.getComment());
        assertEquals(entity.getSubjectId(), dto.getSubjectId());
    }

    @Test
    public void nullProjectToProjectDtoTest() {
        //given
        Project project = null;

        //when
        ProjectDto projectDto = mapper.toDto(project);

        //then
        Assertions.assertNull(projectDto);
    }

    @Test
    public void nullProjectDtoToProjectTest() {
        //given
        ProjectDto projectDto = null;

        //when
        Project project = mapper.fromDto(projectDto);

        //then
        Assertions.assertNull(project);
    }
}
