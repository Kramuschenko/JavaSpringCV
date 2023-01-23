package com.cv.demo.Services;

import com.cv.demo.db.Repository.SubjectRepository;
import com.cv.demo.db.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(int id) {
        return subjectRepository.findById(id).get();
    }

    @Transactional
    public void saveOrUpdate(Subject subject) {
        subjectRepository.save(subject);
    }

    @Transactional
    public void delete(int id) {
        subjectRepository.deleteById(id);
    }

    public List<Subject> subjectsByTeacher(String teacher) {
        return subjectRepository.findSubjectsByTeacher(teacher);
    }

    public Subject firstByTeacher(String teacher) {
        return subjectRepository.findFirstByTeacher(teacher);
    }
}
