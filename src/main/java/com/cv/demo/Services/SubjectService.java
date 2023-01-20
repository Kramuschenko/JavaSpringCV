package com.cv.demo.Services;

import com.cv.demo.db.Repository.SubjectRepository;
import com.cv.demo.db.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectsById(int id) {
        return subjectRepository.findById(id).get();
    }

    public void saveOrUpdate(Subject subject) {
        subjectRepository.save(subject);
    }

    public void delete(int id) {
        subjectRepository.deleteById(id);
    }
}
