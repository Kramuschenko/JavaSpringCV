package com.cv.demo.dto;

import lombok.Data;

@Data
public class ProjectDto {
    private Integer id;
    private String name;
    private String comment;
    private Integer subjectId;

}
