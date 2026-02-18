package com.primaryschool.primaryschool.repository;

import com.primaryschool.primaryschool.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmail(String email);

    List<Teacher> findByIsActiveTrue();

    @Query("SELECT t FROM Teacher t WHERE t.isActive = true ORDER BY t.lastName, t.firstName")
    List<Teacher> findAllActiveTeachers();

    @Query("SELECT t FROM Teacher t JOIN t.subjects s WHERE s.id = :subjectId AND t.isActive = true")
    List<Teacher> findTeachersBySubjectId(@Param("subjectId") Long subjectId);
}
