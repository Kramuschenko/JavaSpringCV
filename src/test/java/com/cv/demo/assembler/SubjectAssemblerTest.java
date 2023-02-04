package com.cv.demo.assembler;

import com.cv.demo.backend.Subject;
import com.cv.demo.dto.SubjectDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectAssemblerTest {

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
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getAbbreviation(), entity.getAbbreviation());
        assertEquals(dto.getTeacher(), entity.getTeacher());
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
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getAbbreviation(), dto.getAbbreviation());
        assertEquals(entity.getTeacher(), dto.getTeacher());
    }

    @Test
    public void nullSubjectToSubjectDtoTest() {
        //given
        Subject subject = null;

        //when
        SubjectDto subjectDto = mapper.toDto(subject);

        //then
        Assertions.assertNull(subjectDto);
    }

    @Test
    public void nullSubjectDtoToSubjectTest() {
        //given
        SubjectDto subjectDto = null;

        //when
        Subject subject = mapper.fromDto(subjectDto);

        //then
        Assertions.assertNull(subject);
    }

}
