package com.primaryschool.primaryschool.controller;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Student;
import com.primaryschool.primaryschool.entity.Teacher;
import com.primaryschool.primaryschool.service.SchoolClassService;
import com.primaryschool.primaryschool.service.StudentService;
import com.primaryschool.primaryschool.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    @Autowired
    public SchoolClassController(SchoolClassService schoolClassService, 
                                  StudentService studentService,
                                  TeacherService teacherService) {
        this.schoolClassService = schoolClassService;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    @GetMapping
    public String listClasses(Model model) {
        model.addAttribute("classes", schoolClassService.getAllClassesWithStudents());
        return "classes/list";
    }

    @GetMapping("/new")
    public String showNewClassForm(Model model) {
        model.addAttribute("schoolClass", new SchoolClass());
        model.addAttribute("teachers", teacherService.getActiveTeachers());
        return "classes/form";
    }

    @GetMapping("/{id}")
    public String viewClass(@PathVariable Long id, Model model) {
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(id);
        if (schoolClass.isPresent()) {
            model.addAttribute("schoolClass", schoolClass.get());
            
            List<Student> students = studentService.getStudentsByClass(id);
            model.addAttribute("students", students);
            model.addAttribute("studentCount", students.size());
            
            return "classes/view";
        }
        return "redirect:/classes";
    }

    @GetMapping("/{id}/edit")
    public String showEditClassForm(@PathVariable Long id, Model model) {
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(id);
        if (schoolClass.isPresent()) {
            model.addAttribute("schoolClass", schoolClass.get());
            model.addAttribute("teachers", teacherService.getActiveTeachers());
            return "classes/form";
        }
        return "redirect:/classes";
    }

    @PostMapping("/save")
    public String saveClass(@ModelAttribute SchoolClass schoolClass) {
        schoolClassService.saveClass(schoolClass);
        return "redirect:/classes";
    }

    @PostMapping("/{id}/update")
    public String updateClass(@PathVariable Long id, @ModelAttribute SchoolClass schoolClass) {
        schoolClassService.updateClass(id, schoolClass);
        return "redirect:/classes/" + id;
    }

    @PostMapping("/{id}/assign-teacher")
    public String assignClassTeacher(@PathVariable Long id, @RequestParam Long teacherId) {
        schoolClassService.assignClassTeacher(id, teacherId);
        return "redirect:/classes/" + id;
    }

    @GetMapping("/{id}/delete")
    public String deleteClass(@PathVariable Long id) {
        schoolClassService.deleteClass(id);
        return "redirect:/classes";
    }
}
