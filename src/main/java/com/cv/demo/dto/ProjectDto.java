package com.cv.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private int id;
    private String name;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int subjectId;

}
