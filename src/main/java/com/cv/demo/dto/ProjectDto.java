package com.cv.demo.dto;

import lombok.Data;

@Data
public class ProjectDto {
    private int id;
    private String name;
    private String comment;
    private int subjectId;

}
