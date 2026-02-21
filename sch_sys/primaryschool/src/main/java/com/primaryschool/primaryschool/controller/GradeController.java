package com.primaryschool.primaryschool.controller;

import com.primaryschool.primaryschool.dto.MarkEntry;
import com.primaryschool.primaryschool.entity.*;
import com.primaryschool.primaryschool.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
                             @RequestParam String term,
                             @RequestParam Integer year,
                             Model model) {
        
        List<Student> students = studentService.getStudentsByClass(classId);
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(classId);
        
        if (schoolClass.isPresent()) {
            model.addAttribute("students", students);
            model.addAttribute("schoolClass", schoolClass.get());
            model.addAttribute("term", term);
            model.addAttribute("year", year);
            
            // Get all active subjects
            List<Subject> allSubjects = subjectService.getActiveSubjects();
            
            // Filter subjects based on class level
            // P.1-P.3 (classLevel 1-3): All subjects
            // P.4-P.7 (classLevel 4-7): Only English, Mathematics, Science, Social Studies
            List<Subject> subjects;
            Integer classLevel = schoolClass.get().getClassLevel();
            
            if (classLevel != null && classLevel >= 4) {
                // P.4 to P.7 - only core subjects
                List<String> coreSubjects = List.of("English", "Mathematics", "Science", "Social Studies");
                subjects = allSubjects.stream()
                    .filter(s -> coreSubjects.stream()
                        .anyMatch(cs -> s.getSubjectName().equalsIgnoreCase(cs)))
                    .collect(Collectors.toList());
            } else {
                // P.1 to P.3 - all subjects
                subjects = allSubjects;
            }
            
            model.addAttribute("subjects", subjects);
            
            // Check for existing grades - Map: studentId -> (subjectId -> Grade)
            Map<Long, Map<Long, Grade>> existingGrades = new HashMap<>();
            for (Student student : students) {
                Map<Long, Grade> studentGrades = new HashMap<>();
                for (Subject subj : subjects) {
                    Optional<Grade> grade = gradeService.getGrade(student.getId(), subj.getId(), term, year);
                    grade.ifPresent(g -> studentGrades.put(subj.getId(), g));
                }
                existingGrades.put(student.getId(), studentGrades);
            }
            model.addAttribute("existingGrades", existingGrades);
            
            return "performance/entry";
        }
        
        return "redirect:/performance";
    }

    @PostMapping("/save-grades")
    public String saveGrades(@RequestParam Long classId,
                            @RequestParam String term,
                            @RequestParam Integer year,
                            @RequestParam(required = false) List<Long> studentIds,
                            @RequestParam(required = false) List<Long> subjectIds,
                            @RequestParam(required = false) List<Double> marks) {
        
        // Debug logging
        System.out.println("Saving grades - studentIds: " + studentIds + ", subjectIds: " + subjectIds + ", marks: " + marks);
        
        if (studentIds != null && !studentIds.isEmpty() && 
            subjectIds != null && !subjectIds.isEmpty() && 
            marks != null && !marks.isEmpty()) {
            
            for (int i = 0; i < studentIds.size(); i++) {
                Long studentId = studentIds.get(i);
                Long subjectId = subjectIds.get(i);
                Double score = marks.get(i);
                
                // Skip empty values
                if (studentId == null || subjectId == null || score == null || score.isNaN()) {
                    continue;
                }
                
                System.out.println("Processing - Student: " + studentId + ", Subject: " + subjectId + ", Score: " + score);
                
                // Check if grade already exists
                boolean exists = gradeService.gradeExists(studentId, subjectId, term, year);
                
                if (exists) {
                    // Update existing grade
                    Optional<Grade> existingGrade = gradeService.getGrade(studentId, subjectId, term, year);
                    if (existingGrade.isPresent()) {
                        Grade grade = existingGrade.get();
                        grade.setMarks(score);
                        gradeService.updateGrade(grade.getId(), grade);
                        System.out.println("Updated grade for student: " + studentId);
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
                            .marks(score)
                            .build();
                        gradeService.saveGrade(grade);
                        System.out.println("Created new grade for student: " + studentId);
                    }
                }
            }
        } else {
            System.out.println("No data to save - lists are empty or null");
        }
        
        return "redirect:/performance/entry?classId=" + classId + "&term=" + term + "&year=" + year;
    }

    @GetMapping("/view")
    public String viewMarks(@RequestParam Long classId, 
                          @RequestParam String term,
                          @RequestParam Integer year,
                          Model model) {
        
        List<Student> students = studentService.getStudentsByClass(classId);
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(classId);
        
        if (schoolClass.isPresent()) {
            model.addAttribute("students", students);
            model.addAttribute("schoolClass", schoolClass.get());
            model.addAttribute("term", term);
            model.addAttribute("year", year);
            
            // Get all active subjects
            List<Subject> allSubjects = subjectService.getActiveSubjects();
            
            // Filter subjects based on class level
            List<Subject> subjects;
            Integer classLevel = schoolClass.get().getClassLevel();
            
            if (classLevel != null && classLevel >= 4) {
                // P.4 to P.7 - only core subjects
                List<String> coreSubjects = List.of("English", "Mathematics", "Science", "Social Studies");
                subjects = allSubjects.stream()
                    .filter(s -> coreSubjects.stream()
                        .anyMatch(cs -> s.getSubjectName().equalsIgnoreCase(cs)))
                    .collect(Collectors.toList());
            } else {
                // P.1 to P.3 - all subjects
                subjects = allSubjects;
            }
            
            model.addAttribute("subjects", subjects);
            
            // Get existing grades - Map: studentId -> (subjectId -> Grade)
            Map<Long, Map<Long, Grade>> existingGrades = new HashMap<>();
            for (Student student : students) {
                Map<Long, Grade> studentGrades = new HashMap<>();
                for (Subject subj : subjects) {
                    Optional<Grade> grade = gradeService.getGrade(student.getId(), subj.getId(), term, year);
                    grade.ifPresent(g -> studentGrades.put(subj.getId(), g));
                }
                existingGrades.put(student.getId(), studentGrades);
            }
            model.addAttribute("existingGrades", existingGrades);
            
            return "performance/view";
        }
        
        return "redirect:/performance";
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
