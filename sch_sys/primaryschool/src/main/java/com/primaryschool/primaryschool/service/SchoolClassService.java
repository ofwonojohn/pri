package com.primaryschool.primaryschool.service;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Student;
import com.primaryschool.primaryschool.entity.Teacher;
import com.primaryschool.primaryschool.repository.SchoolClassRepository;
import com.primaryschool.primaryschool.repository.StudentRepository;
import com.primaryschool.primaryschool.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public SchoolClassService(SchoolClassRepository schoolClassRepository, 
                              TeacherRepository teacherRepository,
                              StudentRepository studentRepository) {
        this.schoolClassRepository = schoolClassRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    public List<SchoolClass> getAllClasses() {
        return schoolClassRepository.findAllOrderByClassLevel();
    }

    public Optional<SchoolClass> getClassById(Long id) {
        return schoolClassRepository.findById(id);
    }

    public Optional<SchoolClass> getClassByName(String className) {
        return schoolClassRepository.findByClassName(className);
    }

    @Transactional
    public SchoolClass saveClass(SchoolClass schoolClass) {
        return schoolClassRepository.save(schoolClass);
    }

    @Transactional
    public SchoolClass updateClass(Long id, SchoolClass classDetails) {
        SchoolClass schoolClass = schoolClassRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Class not found"));

        schoolClass.setClassName(classDetails.getClassName());
        schoolClass.setClassLevel(classDetails.getClassLevel());

        return schoolClassRepository.save(schoolClass);
    }

    @Transactional
    public SchoolClass assignClassTeacher(Long classId, Long teacherId) {
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));

        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        schoolClass.setClassTeacher(teacher);
        return schoolClassRepository.save(schoolClass);
    }

    @Transactional
    public void deleteClass(Long id) {
        schoolClassRepository.deleteById(id);
    }

    public List<Student> getStudentsInClass(Long classId) {
        return studentRepository.findBySchoolClassId(classId);
    }

    public long getStudentCountInClass(Long classId) {
        return studentRepository.countBySchoolClassId(classId);
    }

    public List<SchoolClass> getClassesWithoutTeacher() {
        return schoolClassRepository.findClassesWithoutTeacher();
    }

    public void initializeDefaultClasses() {
        String[] classNames = {"P.1", "P.2", "P.3", "P.4", "P.5", "P.6", "P.7"};
        
        for (int i = 0; i < classNames.length; i++) {
            if (!schoolClassRepository.findByClassName(classNames[i]).isPresent()) {
                SchoolClass schoolClass = SchoolClass.builder()
                    .className(classNames[i])
                    .classLevel(i + 1)
                    .build();
                schoolClassRepository.save(schoolClass);
            }
        }
    }
}
