package com.primaryschool.primaryschool.controller;

import com.primaryschool.primaryschool.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final SchoolClassService schoolClassService;
    private final SubjectService subjectService;

    @Autowired
    public HomeController(StudentService studentService, TeacherService teacherService,
                          SchoolClassService schoolClassService, SubjectService subjectService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.schoolClassService = schoolClassService;
        this.subjectService = subjectService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", studentService.getActiveStudents().size());
        model.addAttribute("totalTeachers", teacherService.getActiveTeachers().size());
        model.addAttribute("totalClasses", schoolClassService.getAllClasses().size());
        model.addAttribute("totalSubjects", subjectService.getActiveSubjects().size());
        
        return "dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect(Model model) {
        return "redirect:/";
    }
}
