package com.primaryschool.primaryschool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "gender", nullable = false)
    private String gender; // Male, Female

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "qualification")
    private String qualification; // Certificate, Diploma, Degree

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
        name = "teacher_subjects",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @Builder.Default
    private Set<Subject> subjects = new HashSet<>();

    @OneToMany(mappedBy = "classTeacher")
    @Builder.Default
    private Set<SchoolClass> assignedClasses = new HashSet<>();

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
