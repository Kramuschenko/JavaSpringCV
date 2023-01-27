package com.cv.demo.assembler;

import com.cv.demo.backend.Subject;
import com.cv.demo.dto.SubjectDto;
import org.mapstruct.Mapper;

@Mapper
public interface SubjectAssembler {

    SubjectDto toDto(Subject subject);

    Subject fromDto(SubjectDto subjectDto);

}
