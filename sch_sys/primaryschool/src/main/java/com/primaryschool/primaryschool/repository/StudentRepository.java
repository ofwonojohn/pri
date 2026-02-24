package com.primaryschool.primaryschool.repository;

import com.primaryschool.primaryschool.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /* ==============================
        BASIC FIND METHODS
       ============================== */

    Optional<Student> findByAdmissionNumber(String admissionNumber);

    List<Student> findBySchoolClassId(Long classId);

    List<Student> findByIsActiveTrue();

    List<Student> findBySchoolClassIdAndIsActiveTrue(Long classId);

    long countBySchoolClassId(Long classId);

    /* ==============================
        SORTED ACTIVE STUDENTS
       ============================== */

    @Query("SELECT s FROM Student s " +
           "WHERE s.schoolClass.id = :classId " +
           "AND s.isActive = true " +
           "ORDER BY s.lastName, s.firstName")
    List<Student> findActiveStudentsByClassId(@Param("classId") Long classId);

    /* ==============================
        ADMISSION NUMBER GENERATOR
       ============================== */

    @Query("SELECT MAX(s.admissionNumber) FROM Student s " +
           "WHERE s.admissionNumber LIKE CONCAT(:prefix, '%')")
    String findMaxAdmissionNumberWithPrefix(@Param("prefix") String prefix);
}