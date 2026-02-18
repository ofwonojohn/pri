package com.primaryschool.primaryschool.service;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Subject;
import com.primaryschool.primaryschool.repository.SchoolClassRepository;
import com.primaryschool.primaryschool.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository, SchoolClassRepository schoolClassRepository) {
        this.subjectRepository = subjectRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<Subject> getActiveSubjects() {
        return subjectRepository.findAllActiveSubjects();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public Optional<Subject> getSubjectByName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName);
    }

    public List<Subject> getSubjectsByClass(Long classId) {
        return subjectRepository.findSubjectsByClassId(classId);
    }

    @Transactional
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject updateSubject(Long id, Subject subjectDetails) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Subject not found"));

        subject.setSubjectName(subjectDetails.getSubjectName());
        subject.setSubjectCode(subjectDetails.getSubjectCode());
        subject.setIsActive(subjectDetails.getIsActive());

        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject assignSubjectToClass(Long subjectId, Long classId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found"));

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));

        subject.setSchoolClass(schoolClass);
        return subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Subject not found"));
        subject.setIsActive(false);
        subjectRepository.save(subject);
    }

    public void initializeDefaultSubjects() {
        String[][] subjects = {
            {"English", "ENG"},
            {"Mathematics", "MATH"},
            {"Science", "SCI"},
            {"Social Studies", "SST"},
            {"Religious Education", "R.E."},
            {"Local Language", "LANG"},
            {"Physical Education", "P.E."},
            {"Art and Craft", "ART"},
            {"Music", "MUSIC"},
            {"Agriculture", "AGR"},
            {"Home Economics", "H.E."}
        };

        for (String[] subject : subjects) {
            if (!subjectRepository.findBySubjectName(subject[0]).isPresent()) {
                Subject subj = Subject.builder()
                    .subjectName(subject[0])
                    .subjectCode(subject[1])
                    .isActive(true)
                    .build();
                subjectRepository.save(subj);
            }
        }
    }
}
