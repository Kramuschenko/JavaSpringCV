package com.cv.demo.backend.repository;

import com.cv.demo.backend.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> getProjectBySubjectId(Integer subjectId);

    @Query(value = "select  nvl(MAX(ID), 0)+1 from PROJECT", nativeQuery = true)
    int generateNextProjectId();

}
