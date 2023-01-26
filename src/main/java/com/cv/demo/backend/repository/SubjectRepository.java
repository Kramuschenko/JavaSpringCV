package com.cv.demo.backend.repository;

import com.cv.demo.backend.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    List<Subject> findSubjectsByTeacher(String teacher);

    Subject findFirstByTeacher(String teacher);

}
