package com.cv.demo.backend.repository;

import com.cv.demo.backend.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    List<Subject> findSubjectsByTeacher(String teacher);

    Subject findFirstByTeacher(String teacher);

    /*@Query(value = "SELECT SUBJECT.ID AS SUBJECT_ID , PROJECT.ID AS PROJECT_ID , ABBREVIATION, NAME , COMMENT  , TEACHER FROM SUBJECT INNER JOIN PROJECT ON SUBJECT.ID = PROJECT.SUBJECT_ID")
    List<Subject> findAll();*/
}
