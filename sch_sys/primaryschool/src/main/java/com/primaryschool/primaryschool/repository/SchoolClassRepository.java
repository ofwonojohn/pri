package com.primaryschool.primaryschool.repository;

import com.primaryschool.primaryschool.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    Optional<SchoolClass> findByClassName(String className);

    List<SchoolClass> findAllByOrderByClassLevelAsc();

    @Query("SELECT sc FROM SchoolClass sc ORDER BY sc.classLevel ASC")
    List<SchoolClass> findAllOrderByClassLevel();

    @Query("SELECT sc FROM SchoolClass sc WHERE sc.classTeacher IS NULL")
    List<SchoolClass> findClassesWithoutTeacher();
}
