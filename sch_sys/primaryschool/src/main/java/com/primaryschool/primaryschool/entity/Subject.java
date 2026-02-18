package com.primaryschool.primaryschool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_name", nullable = false, unique = true)
    private String subjectName; // English, Mathematics, Science, etc.

    @Column(name = "subject_code")
    private String subjectCode; // ENG, MATH, SCI, etc.

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @ManyToMany(mappedBy = "subjects")
    @Builder.Default
    private Set<Teacher> teachers = new HashSet<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Grade> grades = new HashSet<>();
}
