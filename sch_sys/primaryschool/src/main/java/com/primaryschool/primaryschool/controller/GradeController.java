package com.primaryschool.primaryschool.controller;

import com.primaryschool.primaryschool.entity.*;
import com.primaryschool.primaryschool.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/performance")
public class GradeController {

    private final GradeService gradeService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final SchoolClassService schoolClassService;

    @Autowired
    public GradeController(GradeService gradeService, StudentService studentService,
                          SubjectService subjectService, SchoolClassService schoolClassService) {
        this.gradeService = gradeService;
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public String performanceHome(Model model) {
        List<Integer> years = gradeService.getAllYears();
        if (years.isEmpty()) {
            years = List.of(LocalDate.now().getYear());
        }
        
        model.addAttribute("classes", schoolClassService.getAllClasses());
        model.addAttribute("subjects", subjectService.getActiveSubjects());
        model.addAttribute("years", years);
        model.addAttribute("currentYear", LocalDate.now().getYear());
        
        return "performance/index";
    }

    @GetMapping("/entry")
    public String enterMarks(@RequestParam Long classId, 
                             @RequestParam Long subjectId,
                             @RequestParam String term,
                             @RequestParam Integer year,
                             Model model) {
        
        List<Student> students = studentService.getStudentsByClass(classId);
        Optional<Subject> subject = subjectService.getSubjectById(subjectId);
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(classId);
        
        if (subject.isPresent() && schoolClass.isPresent()) {
            model.addAttribute("students", students);
            model.addAttribute("subject", subject.get());
            model.addAttribute("schoolClass", schoolClass.get());
            model.addAttribute("term", term);
            model.addAttribute("year", year);
            
            // Check for existing grades
            Map<Long, Grade> existingGrades = new HashMap<>();
            for (Student student : students) {
                Optional<Grade> grade = gradeService.getGrade(student.getId(), subjectId, term, year);
                grade.ifPresent(g -> existingGrades.put(student.getId(), g));
            }
            model.addAttribute("existingGrades", existingGrades);
            
            return "performance/entry";
        }
        
        return "redirect:/performance";
    }

    @PostMapping("/save-grade")
    public String saveGrade(@RequestParam Long studentId,
                            @RequestParam Long subjectId,
                            @RequestParam Long classId,
                            @RequestParam String term,
                            @RequestParam Integer year,
                            @RequestParam Double marks) {
        
        // Check if grade already exists
        boolean exists = gradeService.gradeExists(studentId, subjectId, term, year);
        
        if (exists) {
            // Update existing grade
            Optional<Grade> existingGrade = gradeService.getGrade(studentId, subjectId, term, year);
            if (existingGrade.isPresent()) {
                Grade grade = existingGrade.get();
                grade.setMarks(marks);
                gradeService.updateGrade(grade.getId(), grade);
            }
        } else {
            // Create new grade
            Student student = studentService.getStudentById(studentId).orElse(null);
            Subject subject = subjectService.getSubjectById(subjectId).orElse(null);
            SchoolClass schoolClass = schoolClassService.getClassById(classId).orElse(null);
            
            if (student != null && subject != null && schoolClass != null) {
                Grade grade = Grade.builder()
                    .student(student)
                    .subject(subject)
                    .schoolClass(schoolClass)
                    .term(term)
                    .year(year)
                    .marks(marks)
                    .build();
                gradeService.saveGrade(grade);
            }
        }
        
        return "redirect:/performance/entry?classId=" + classId + "&subjectId=" + subjectId + "&term=" + term + "&year=" + year;
    }

    @GetMapping("/student/{studentId}")
    public String viewStudentPerformance(@PathVariable Long studentId,
                                         @RequestParam(required = false, defaultValue = "Term 1") String term,
                                         @RequestParam(required = false, defaultValue = "2026") Integer year,
                                         Model model) {
        
        Optional<Student> student = studentService.getStudentById(studentId);
        if (student.isPresent()) {
            Map<String, Object> performance = gradeService.calculateStudentPerformance(studentId, term, year);
            model.addAttribute("student", student.get());
            model.addAttribute("performance", performance);
            model.addAttribute("term", term);
            model.addAttribute("year", year);
            
            List<Integer> years = gradeService.getAllYears();
            if (years.isEmpty()) {
                years = List.of(LocalDate.now().getYear());
            }
            model.addAttribute("years", years);
            
            return "performance/student";
        }
        
        return "redirect:/students";
    }

    @GetMapping("/class")
    public String viewClassPerformance(@RequestParam Long classId,
                                        @RequestParam(required = false, defaultValue = "Term 1") String term,
                                        @RequestParam(required = false, defaultValue = "2026") Integer year,
                                        Model model) {
        
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(classId);
        if (schoolClass.isPresent()) {
            List<Map<String, Object>> performanceList = gradeService.getClassPerformance(classId, term, year);
            
            model.addAttribute("schoolClass", schoolClass.get());
            model.addAttribute("performanceList", performanceList);
            model.addAttribute("term", term);
            model.addAttribute("year", year);
            
            // Get top 3
            List<Map<String, Object>> top3 = performanceList.stream().limit(3).toList();
            model.addAttribute("top3", top3);
            
            List<Integer> years = gradeService.getAllYears();
            if (years.isEmpty()) {
                years = List.of(LocalDate.now().getYear());
            }
            model.addAttribute("years", years);
            
            return "performance/class";
        }
        
        return "redirect:/classes";
    }

    @GetMapping("/report/class")
    public String generateClassReport(@RequestParam Long classId,
                                      @RequestParam(required = false, defaultValue = "Term 1") String term,
                                      @RequestParam(required = false, defaultValue = "2026") Integer year,
                                      Model model) {
        
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(classId);
        if (schoolClass.isPresent()) {
            Map<String, Object> report = gradeService.generateClassReport(classId, term, year);
            
            model.addAttribute("schoolClass", schoolClass.get());
            model.addAttribute("report", report);
            model.addAttribute("term", term);
            model.addAttribute("year", year);
            
            List<Integer> years = gradeService.getAllYears();
            if (years.isEmpty()) {
                years = List.of(LocalDate.now().getYear());
            }
            model.addAttribute("years", years);
            
            return "reports/class";
        }
        
        return "redirect:/classes";
    }

    @GetMapping("/report/student/{studentId}")
    public String generateStudentReport(@PathVariable Long studentId,
                                        @RequestParam(required = false, defaultValue = "Term 1") String term,
                                        @RequestParam(required = false, defaultValue = "2026") Integer year,
                                        Model model) {
        
        Optional<Student> student = studentService.getStudentById(studentId);
        if (student.isPresent()) {
            Map<String, Object> performance = gradeService.calculateStudentPerformance(studentId, term, year);
            
            model.addAttribute("student", student.get());
            model.addAttribute("performance", performance);
            model.addAttribute("term", term);
            model.addAttribute("year", year);
            
            List<Integer> years = gradeService.getAllYears();
            if (years.isEmpty()) {
                years = List.of(LocalDate.now().getYear());
            }
            model.addAttribute("years", years);
            
            return "reports/student";
        }
        
        return "redirect:/students";
    }
}
