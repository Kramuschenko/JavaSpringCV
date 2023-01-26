package com.cv.demo.assembler;

import com.cv.demo.backend.Subject;
import com.cv.demo.dto.SubjectDto;
import org.springframework.stereotype.Component;

@Component
public class SubjectAssembler {
    public SubjectDto toDto(Subject subject){
        if (null == subject){
            return null;
        }

        SubjectDto subjectDto = new SubjectDto();

        subjectDto.setId(subjectDto.getId());
        subjectDto.setAbbreviation(subjectDto.getAbbreviation());
        subjectDto.setProjects(subject.getProjects());
        subjectDto.setTeacher(subject.getTeacher());

        subjectDto.setCreatedAt(subject.getCreatedAt());
        subjectDto.setModifiedAt(subject.getModifiedAt());

        return subjectDto;
    }

    public Subject fromDto(SubjectDto subjectDto){
        Subject subject = new Subject();

        subject.setId(subjectDto.getId());
        subject.setAbbreviation(subjectDto.getAbbreviation());
        subject.setTeacher(subjectDto.getTeacher());
        subject.setProjects(subjectDto.getProjects());

        subject.setCreatedAt(subjectDto.getCreatedAt());
        subject.setModifiedAt(subject.getModifiedAt());

        return subject;
    }
}
