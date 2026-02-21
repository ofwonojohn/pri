package com.primaryschool.primaryschool.controller;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Student;
import com.primaryschool.primaryschool.service.GradeService;
import com.primaryschool.primaryschool.service.SchoolClassService;
import com.primaryschool.primaryschool.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final GradeService gradeService;
    private final StudentService studentService;
    private final SchoolClassService schoolClassService;

    @Autowired
    public ReportsController(GradeService gradeService, StudentService studentService,
                              SchoolClassService schoolClassService) {
        this.gradeService = gradeService;
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public String reportsHome(Model model) {
        List<Integer> years = gradeService.getAllYears();
        if (years.isEmpty()) {
            years = List.of(LocalDate.now().getYear());
        }
        
        model.addAttribute("classes", schoolClassService.getAllClasses());
        model.addAttribute("students", studentService.getActiveStudents());
        model.addAttribute("years", years);
        model.addAttribute("currentYear", LocalDate.now().getYear());
        
        return "reports/index";
    }

    @GetMapping("/class")
    public String classReport(@RequestParam(required = false) Long classId,
                               @RequestParam(required = false, defaultValue = "Term 1") String term,
                               @RequestParam(required = false, defaultValue = "2026") Integer year,
                               Model model) {
        
        // Always add classes to the model
        model.addAttribute("classes", schoolClassService.getAllClasses());
        
        List<Integer> years = gradeService.getAllYears();
        if (years.isEmpty()) {
            years = List.of(LocalDate.now().getYear());
        }
        model.addAttribute("years", years);
        
        if (classId != null) {
            Optional<SchoolClass> schoolClass = schoolClassService.getClassById(classId);
            if (schoolClass.isPresent()) {
                Map<String, Object> report = gradeService.generateClassReport(classId, term, year);
                
                model.addAttribute("selectedClass", schoolClass.get());
                model.addAttribute("report", report);
                model.addAttribute("term", term);
                model.addAttribute("year", year);
                
                // Get student results for the table
                List<Map<String, Object>> studentResults = gradeService.getClassPerformance(classId, term, year);
                model.addAttribute("studentResults", studentResults);
            }
        }
        
        return "reports/class";
    }

    @GetMapping("/student")
    public String studentReport(@RequestParam Long studentId,
                                @RequestParam(required = false, defaultValue = "Term 1") String term,
                                @RequestParam(required = false, defaultValue = "2026") Integer year,
                                Model model) {
        
        Optional<Student> student = studentService.getStudentById(studentId);
        if (student.isPresent()) {
            // Get student performance data
            Map<String, Object> performance = gradeService.calculateStudentPerformance(studentId, term, year);
            
            model.addAttribute("selectedStudent", student.get());
            model.addAttribute("performance", performance);
            model.addAttribute("term", term);
            model.addAttribute("year", year);
            
            // Add report card data
            if (!performance.isEmpty()) {
                model.addAttribute("subjectGrades", performance.get("grades"));
                model.addAttribute("totalMarks", performance.get("totalMarks"));
                model.addAttribute("bestFourAggregate", performance.get("best4Aggregate"));
                model.addAttribute("division", performance.get("division"));
                
                // Get class position
                Long classId = student.get().getSchoolClass() != null ? student.get().getSchoolClass().getId() : null;
                if (classId != null) {
                    List<Map<String, Object>> classPerformance = gradeService.getClassPerformance(classId, term, year);
                    int position = 1;
                    int totalStudents = classPerformance.size();
                    for (Map<String, Object> p : classPerformance) {
                        Student s = (Student) p.get("student");
                        if (s.getId().equals(studentId)) {
                            break;
                        }
                        position++;
                    }
                    model.addAttribute("classPosition", position);
                    model.addAttribute("totalStudents", totalStudents);
                }
            }
            
            // Add students list for selection
            model.addAttribute("students", studentService.getActiveStudents());
            
            List<Integer> years = gradeService.getAllYears();
            if (years.isEmpty()) {
                years = List.of(LocalDate.now().getYear());
            }
            model.addAttribute("years", years);
            
            return "reports/student";
        }
        
        return "redirect:/reports";
    }
}
