package com.cv.demo.assembler;

import com.cv.demo.backend.Subject;
import com.cv.demo.dto.SubjectDto;
import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

public class SubjectAssemblerTest {

    private final SubjectAssembler mapper = Mappers.getMapper(SubjectAssembler.class);

    @Test
    public void subjectDtoToSubjectTest() {
        //given
        SubjectDto dto = new SubjectDto();
        dto.setId(1);
        dto.setAbbreviation("John");
        dto.setTeacher("Tomasz");

        //when
        Subject entity = mapper.fromDto(dto);

        //then
        Assert.assertEquals(dto.getId(), entity.getId());
        Assert.assertEquals(dto.getAbbreviation(), entity.getAbbreviation());
        Assert.assertEquals(dto.getTeacher(), entity.getTeacher());
    }

    @Test
    public void subjectToSubjectDtoTest() {

        //Given
        Subject entity = new Subject();
        entity.setId(1);
        entity.setAbbreviation("John");
        entity.setTeacher("Tomasz");

        //when
        SubjectDto dto = mapper.toDto(entity);

        //then
        Assert.assertEquals(entity.getId(), dto.getId());
        Assert.assertEquals(entity.getAbbreviation(), dto.getAbbreviation());
        Assert.assertEquals(entity.getTeacher(), dto.getTeacher());
    }

    @Test
    public void nullSubjectToSubjectDtoTest() {
        //given
        Subject subject = null;

        //when
        SubjectDto subjectDto = mapper.toDto(subject);

        //then
        Assert.assertNull(subjectDto);
    }

    @Test
    public void nullSubjectDtoToSubjectTest() {
        //given
        SubjectDto subjectDto = null;

        //when
        Subject subject = mapper.fromDto(subjectDto);

        //then
        Assert.assertNull(subject);
    }

}
