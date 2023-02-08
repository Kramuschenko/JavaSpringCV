package com.cv.demo.assembler;

import com.cv.demo.backend.Project;
import com.cv.demo.dto.ProjectDto;
import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

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
        Assert.assertEquals(dto.getId(), entity.getId());
        Assert.assertEquals(dto.getName(), entity.getName());
        Assert.assertEquals(dto.getComment(), entity.getComment());
        Assert.assertEquals(dto.getSubjectId(), entity.getSubjectId());
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
        Assert.assertEquals(entity.getId(), dto.getId());
        Assert.assertEquals(entity.getName(), dto.getName());
        Assert.assertEquals(entity.getComment(), dto.getComment());
        Assert.assertEquals(entity.getSubjectId(), dto.getSubjectId());
    }

    @Test
    public void nullProjectToProjectDtoTest() {
        //given
        Project project = null;

        //when
        ProjectDto projectDto = mapper.toDto(project);

        //then
        Assert.assertNull(projectDto);
    }

    @Test
    public void nullProjectDtoToProjectTest() {
        //given
        ProjectDto projectDto = null;

        //when
        Project project = mapper.fromDto(projectDto);

        //then
        Assert.assertNull(project);
    }
}
