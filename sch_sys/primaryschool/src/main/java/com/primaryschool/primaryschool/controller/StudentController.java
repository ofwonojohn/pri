package com.primaryschool.primaryschool.controller;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Student;
import com.primaryschool.primaryschool.service.SchoolClassService;
import com.primaryschool.primaryschool.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final SchoolClassService schoolClassService;

    @Autowired
    public StudentController(StudentService studentService, SchoolClassService schoolClassService) {
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public String listStudents(@RequestParam(required = false) Long classId, Model model) {
        List<Student> students;
        List<SchoolClass> classes = schoolClassService.getAllClasses();
        
        if (classId != null) {
            students = studentService.getStudentsByClass(classId);
            Optional<SchoolClass> selectedClass = schoolClassService.getClassById(classId);
            selectedClass.ifPresent(c -> model.addAttribute("selectedClass", c));
        } else {
            students = studentService.getActiveStudents();
        }
        
        model.addAttribute("students", students);
        model.addAttribute("classes", classes);
        model.addAttribute("selectedClassId", classId);
        
        return "students/list";
    }

    @GetMapping("/new")
    public String showNewStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("classes", schoolClassService.getAllClasses());
        return "students/form";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            model.addAttribute("student", student.get());
            return "students/view";
        }
        return "redirect:/students";
    }

    @GetMapping("/{id}/edit")
    public String showEditStudentForm(@PathVariable Long id, Model model) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            model.addAttribute("student", student.get());
            model.addAttribute("classes", schoolClassService.getAllClasses());
            return "students/form";
        }
        return "redirect:/students";
    }

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student) {
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @PostMapping("/{id}/update")
    public String updateStudent(@PathVariable Long id, @ModelAttribute Student student) {
        studentService.updateStudent(id, student);
        return "redirect:/students/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

    @PostMapping("/{id}/assign-class")
    public String assignStudentToClass(@PathVariable Long id, @RequestParam Long classId) {
        studentService.assignStudentToClass(id, classId);
        return "redirect:/students/" + id;
    }
}
