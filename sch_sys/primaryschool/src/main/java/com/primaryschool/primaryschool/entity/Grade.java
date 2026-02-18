package com.primaryschool.primaryschool.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Column(name = "term", nullable = false)
    private String term; // Term 1, Term 2, Term 3

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "marks", nullable = false)
    private Double marks; // 0-100

    @Column(name = "grade_letter")
    private String gradeLetter; // D1, D2, C3, C4, C5, C6, P7, P8, F9

    @Column(name = "remarks")
    private String remarks; // Excellent, Good, Fair, Poor

    @Column(name = "is_final")
    @Builder.Default
    private Boolean isFinal = false;
}
