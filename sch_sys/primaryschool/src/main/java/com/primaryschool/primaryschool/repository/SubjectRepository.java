package com.primaryschool.primaryschool.repository;

import com.primaryschool.primaryschool.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findBySubjectName(String subjectName);

    Optional<Subject> findBySubjectCode(String subjectCode);

    List<Subject> findByIsActiveTrue();

    @Query("SELECT s FROM Subject s WHERE s.isActive = true ORDER BY s.subjectName")
    List<Subject> findAllActiveSubjects();

    @Query("SELECT s FROM Subject s WHERE s.schoolClass.id = :classId AND s.isActive = true")
    List<Subject> findSubjectsByClassId(@Param("classId") Long classId);

    @Query("SELECT s FROM Subject s WHERE s.schoolClass IS NULL AND s.isActive = true")
    List<Subject> findSubjectsWithoutClass();
}
