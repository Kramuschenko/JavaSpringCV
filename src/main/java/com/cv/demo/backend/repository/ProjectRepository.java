package com.cv.demo.backend.repository;

import com.cv.demo.backend.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    @Query(value = "select  MAX(ID)+1 from PROJECT", nativeQuery = true)
    int generateNextProjectId();

}
