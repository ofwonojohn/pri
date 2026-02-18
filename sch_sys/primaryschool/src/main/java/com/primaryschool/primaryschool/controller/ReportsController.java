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
    public String classReport(@RequestParam Long classId,
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
        
        return "redirect:/reports";
    }

    @GetMapping("/student/{studentId}")
    public String studentReport(@PathVariable Long studentId,
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
        
        return "redirect:/reports";
    }
}
