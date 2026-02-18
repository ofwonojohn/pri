package com.primaryschool.primaryschool.repository;

import com.primaryschool.primaryschool.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.term = :term AND g.year = :year")
    List<Grade> findByStudentIdAndTermAndYear(
        @Param("studentId") Long studentId,
        @Param("term") String term,
        @Param("year") Integer year
    );

    @Query("SELECT g FROM Grade g WHERE g.schoolClass.id = :classId AND g.term = :term AND g.year = :year")
    List<Grade> findByClassIdAndTermAndYear(
        @Param("classId") Long classId,
        @Param("term") String term,
        @Param("year") Integer year
    );

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.subject.id = :subjectId AND g.term = :term AND g.year = :year")
    Optional<Grade> findByStudentIdAndSubjectIdAndTermAndYear(
        @Param("studentId") Long studentId,
        @Param("subjectId") Long subjectId,
        @Param("term") String term,
        @Param("year") Integer year
    );

    @Query("SELECT g FROM Grade g WHERE g.schoolClass.id = :classId AND g.subject.id = :subjectId AND g.term = :term AND g.year = :year")
    List<Grade> findByClassIdAndSubjectIdAndTermAndYear(
        @Param("classId") Long classId,
        @Param("subjectId") Long subjectId,
        @Param("term") String term,
        @Param("year") Integer year
    );

    @Query("SELECT DISTINCT g.year FROM Grade g ORDER BY g.year DESC")
    List<Integer> findAllDistinctYears();

    @Query("SELECT AVG(g.marks) FROM Grade g WHERE g.schoolClass.id = :classId AND g.subject.id = :subjectId AND g.term = :term AND g.year = :year")
    Double getAverageMarksByClassAndSubject(
        @Param("classId") Long classId,
        @Param("subjectId") Long subjectId,
        @Param("term") String term,
        @Param("year") Integer year
    );
}
