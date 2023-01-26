package com.cv.demo.dto;

import com.cv.demo.backend.Project;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Setter
public class SubjectDto {
    private int id;
    private String abbreviation;
    private String teacher;
    private List<Project> projects;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
