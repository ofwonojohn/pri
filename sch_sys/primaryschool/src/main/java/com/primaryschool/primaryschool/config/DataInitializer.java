package com.primaryschool.primaryschool.config;

import com.primaryschool.primaryschool.service.SchoolClassService;
import com.primaryschool.primaryschool.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SchoolClassService schoolClassService;
    private final SubjectService subjectService;

    @Autowired
    public DataInitializer(SchoolClassService schoolClassService, SubjectService subjectService) {
        this.schoolClassService = schoolClassService;
        this.subjectService = subjectService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize default classes (P.1 to P.7)
        schoolClassService.initializeDefaultClasses();
        
        // Initialize default subjects
        subjectService.initializeDefaultSubjects();
    }
}
