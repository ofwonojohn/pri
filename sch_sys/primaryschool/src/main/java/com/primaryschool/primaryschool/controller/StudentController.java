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

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student, Model model) {

        Optional<Student> existingStudent =
                studentService.findByAdmissionNumber(student.getAdmissionNumber());

        if (existingStudent.isPresent()) {
            model.addAttribute("errorMessage", "Admission number already exists!");
            model.addAttribute("classes", schoolClassService.getAllClasses());
            return "students/form";
        }

        studentService.saveStudent(student);
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

    @PostMapping("/{id}/update")
    public String updateStudent(@PathVariable Long id,
                                @ModelAttribute Student student,
                                Model model) {

        Optional<Student> existingStudent =
                studentService.findByAdmissionNumber(student.getAdmissionNumber());

        if (existingStudent.isPresent() &&
                !existingStudent.get().getId().equals(id)) {

            model.addAttribute("errorMessage", "Admission number already exists!");
            model.addAttribute("classes", schoolClassService.getAllClasses());
            return "students/form";
        }

        studentService.updateStudent(id, student);
        return "redirect:/students";
    }

    @GetMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}