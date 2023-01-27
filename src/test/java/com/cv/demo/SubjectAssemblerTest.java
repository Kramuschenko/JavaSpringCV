package com.cv.demo;

import com.cv.demo.assembler.SubjectAssembler;
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
        SubjectDto dto = new SubjectDto();
        dto.setId(1);
        dto.setAbbreviation("John");
        dto.setTeacher("Tomasz");

        Subject entity = mapper.fromDto(dto);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getAbbreviation(), dto.getAbbreviation());
        assertEquals(entity.getTeacher(), dto.getTeacher());
    }

    @Test
    public void subjectToSubjectDtoTest() {
        Subject entity = new Subject();
        entity.setId(1);
        entity.setAbbreviation("John");
        entity.setTeacher("Tomasz");

        SubjectDto dto = mapper.toDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getAbbreviation(), dto.getAbbreviation());
        assertEquals(entity.getTeacher(), dto.getTeacher());
    }

    @Test
    public void shouldHandleNullSubject() {
        Subject subject = null;

        SubjectDto subjectDto = mapper.toDto(subject);

        Assertions.assertNull(subjectDto);
    }


}
