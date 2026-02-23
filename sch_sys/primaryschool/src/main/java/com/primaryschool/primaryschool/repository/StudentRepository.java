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

    Optional<Student> findByAdmissionNumber(String admissionNumber);

    List<Student> findBySchoolClassId(Long classId);

    List<Student> findByIsActiveTrue();

    List<Student> findBySchoolClassIdAndIsActiveTrue(Long classId);

    @Query("SELECT s FROM Student s WHERE s.schoolClass.id = :classId AND s.isActive = true ORDER BY s.lastName, s.firstName")
    List<Student> findActiveStudentsByClassId(@Param("classId") Long classId);

    @Query("SELECT MAX(s.admissionNumber) FROM Student s WHERE s.admissionNumber LIKE :prefix%")
    String findMaxAdmissionNumberWithPrefix(@Param("prefix") String prefix);

    long countBySchoolClassId(Long classId);
}

