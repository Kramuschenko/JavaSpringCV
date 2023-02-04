package com.cv.demo.backend.repository;

import lombok.Data;

@Data
public class Information {
    Integer subjectId;
    Integer projectId;
    String abbreviation;
    String name;
    String comment;
    String teacher;
}
