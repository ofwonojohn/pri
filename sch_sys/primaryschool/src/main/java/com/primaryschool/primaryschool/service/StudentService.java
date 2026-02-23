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
    public StudentService(StudentRepository studentRepository,
                          SchoolClassRepository schoolClassRepository) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    /* ==============================
            BASIC FETCH METHODS
       ============================== */

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

    public Optional<Student> findByAdmissionNumber(String admissionNumber) {
        return studentRepository.findByAdmissionNumber(admissionNumber);
    }

    /* ==============================
            SAVE STUDENT
       ============================== */

    @Transactional
    public Student saveStudent(Student student) {

        // Prevent duplicate admission number
        Optional<Student> existing =
                studentRepository.findByAdmissionNumber(student.getAdmissionNumber());

        if (existing.isPresent()) {
            throw new RuntimeException("Admission number already exists!");
        }

        // Auto-generate if empty
        if (student.getAdmissionNumber() == null || student.getAdmissionNumber().isEmpty()) {
            student.setAdmissionNumber(generateAdmissionNumber());
        }

        // Set admission date if null
        // if (student.getDateOfAdmission() == null) {
        //     student.setDateOfAdmission(LocalDate.now());
        // }

        // Default active
        if (student.getIsActive() == null) {
            student.setIsActive(true);
        }

        return studentRepository.save(student);
    }

    /* ==============================
            UPDATE STUDENT
       ============================== */

    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Prevent duplicate admission number (if changed)
        Optional<Student> existing =
                studentRepository.findByAdmissionNumber(studentDetails.getAdmissionNumber());

        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Admission number already exists!");
        }

        student.setAdmissionNumber(studentDetails.getAdmissionNumber());
        student.setFirstName(studentDetails.getFirstName());
        student.setLastName(studentDetails.getLastName());
        student.setGender(studentDetails.getGender());
        student.setDateOfBirth(studentDetails.getDateOfBirth());
        student.setParentName(studentDetails.getParentName());
        student.setParentPhone(studentDetails.getParentPhone());
        student.setParentEmail(studentDetails.getParentEmail());
        student.setAddress(studentDetails.getAddress());
        student.setSchoolClass(studentDetails.getSchoolClass());

        return studentRepository.save(student);
    }

    /* ==============================
            ASSIGN CLASS
       ============================== */

    @Transactional
    public Student assignStudentToClass(Long studentId, Long classId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        student.setSchoolClass(schoolClass);

        return studentRepository.save(student);
    }

    /* ==============================
            DELETE (SOFT DELETE)
       ============================== */

    @Transactional
    public void deleteStudent(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setIsActive(false);

        studentRepository.save(student);
    }

    /* ==============================
            HARD DELETE
       ============================== */

    @Transactional
    public void permanentlyDeleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    /* ==============================
            COUNT
       ============================== */

    public long getStudentCountByClass(Long classId) {
        return studentRepository.countBySchoolClassId(classId);
    }

    /* ==============================
        ADMISSION NUMBER GENERATOR
       ============================== */

    private String generateAdmissionNumber() {

        String prefix = "ADM" + LocalDate.now().getYear();

        String maxAdmissionNumber =
                studentRepository.findMaxAdmissionNumberWithPrefix(prefix);

        if (maxAdmissionNumber == null) {
            return prefix + "0001";
        }

        int number = Integer.parseInt(
                maxAdmissionNumber.substring(prefix.length())
        );

        return prefix + String.format("%04d", number + 1);
    }
}