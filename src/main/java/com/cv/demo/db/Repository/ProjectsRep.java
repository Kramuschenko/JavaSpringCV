package com.cv.demo.db.Repository;

import com.cv.demo.db.Projects;
import org.springframework.data.repository.CrudRepository;

@org.springframework.stereotype.Repository
public interface ProjectsRep extends CrudRepository<Projects , Long> {
}
