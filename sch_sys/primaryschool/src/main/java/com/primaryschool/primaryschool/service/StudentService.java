package com.primaryschool.primaryschool.service;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Student;
import com.primaryschool.primaryschool.repository.SchoolClassRepository;
import com.primaryschool.primaryschool.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, SchoolClassRepository schoolClassRepository) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getActiveStudents() {
        return studentRepository.findByIsActiveTrue();
    }

    public List<Student> getStudentsByClass(Long classId) {
        return studentRepository.findBySchoolClassIdAndIsActiveTrue(classId);
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByAdmissionNumber(String admissionNumber) {
        return studentRepository.findByAdmissionNumber(admissionNumber);
    }

    @Transactional
    public Student saveStudent(Student student) {
        if (student.getAdmissionNumber() == null || student.getAdmissionNumber().isEmpty()) {
            student.setAdmissionNumber(generateAdmissionNumber());
        }
        if (student.getDateOfAdmission() == null || student.getDateOfAdmission().isEmpty()) {
            student.setDateOfAdmission(LocalDate.now().toString());
        }
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFirstName(studentDetails.getFirstName());
        student.setLastName(studentDetails.getLastName());
        student.setGender(studentDetails.getGender());
        student.setDateOfBirth(studentDetails.getDateOfBirth());
        student.setParentName(studentDetails.getParentName());
        student.setParentPhone(studentDetails.getParentPhone());
        student.setParentEmail(studentDetails.getParentEmail());
        student.setAddress(studentDetails.getAddress());

        return studentRepository.save(student);
    }

    @Transactional
    public Student assignStudentToClass(Long studentId, Long classId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        
        student.setSchoolClass(schoolClass);
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setIsActive(false);
        studentRepository.save(student);
    }

    @Transactional
    public void permanentlyDeleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public long getStudentCountByClass(Long classId) {
        return studentRepository.countBySchoolClassId(classId);
    }

    private String generateAdmissionNumber() {
        String prefix = "ADM" + LocalDate.now().getYear();
        String maxAdmissionNumber = studentRepository.findMaxAdmissionNumberWithPrefix(prefix);
        
        if (maxAdmissionNumber == null) {
            return prefix + "0001";
        }
        
        int number = Integer.parseInt(maxAdmissionNumber.substring(prefix.length()));
        return prefix + String.format("%04d", number + 1);
    }
}
