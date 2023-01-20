package com.cv.demo.db.Repository;

import com.cv.demo.db.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject , Integer> {
}
