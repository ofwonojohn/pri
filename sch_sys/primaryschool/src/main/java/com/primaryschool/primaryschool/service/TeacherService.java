package com.primaryschool.primaryschool.service;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Subject;
import com.primaryschool.primaryschool.entity.Teacher;
import com.primaryschool.primaryschool.repository.SchoolClassRepository;
import com.primaryschool.primaryschool.repository.SubjectRepository;
import com.primaryschool.primaryschool.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, SubjectRepository subjectRepository, 
                          SchoolClassRepository schoolClassRepository) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public List<Teacher> getActiveTeachers() {
        return teacherRepository.findAllActiveTeachers();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Optional<Teacher> getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    public List<Teacher> getTeachersBySubject(Long subjectId) {
        return teacherRepository.findTeachersBySubjectId(subjectId);
    }

    @Transactional
    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        teacher.setFirstName(teacherDetails.getFirstName());
        teacher.setLastName(teacherDetails.getLastName());
        teacher.setGender(teacherDetails.getGender());
        teacher.setDateOfBirth(teacherDetails.getDateOfBirth());
        teacher.setPhoneNumber(teacherDetails.getPhoneNumber());
        teacher.setEmail(teacherDetails.getEmail());
        teacher.setAddress(teacherDetails.getAddress());
        teacher.setQualification(teacherDetails.getQualification());

        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher assignSubjects(Long teacherId, Set<Long> subjectIds) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Set<Subject> subjects = new HashSet<>(subjectRepository.findAllById(subjectIds));
        teacher.setSubjects(subjects);
        
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher assignClassTeacher(Long teacherId, Long classId) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));

        // Remove teacher from previous class if they were class teacher
        List<SchoolClass> classes = schoolClassRepository.findAll();
        for (SchoolClass sc : classes) {
            if (sc.getClassTeacher() != null && sc.getClassTeacher().getId().equals(teacherId)) {
                sc.setClassTeacher(null);
                schoolClassRepository.save(sc);
            }
        }

        // Assign teacher as class teacher
        schoolClass.setClassTeacher(teacher);
        schoolClassRepository.save(schoolClass);

        return teacher;
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setIsActive(false);
        teacherRepository.save(teacher);
    }
}
