package com.primaryschool.primaryschool.controller;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Subject;
import com.primaryschool.primaryschool.entity.Teacher;
import com.primaryschool.primaryschool.service.SchoolClassService;
import com.primaryschool.primaryschool.service.SubjectService;
import com.primaryschool.primaryschool.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final SchoolClassService schoolClassService;

    @Autowired
    public TeacherController(TeacherService teacherService, SubjectService subjectService,
                            SchoolClassService schoolClassService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getActiveTeachers());
        return "teachers/list";
    }

    @GetMapping("/new")
    public String showNewTeacherForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("subjects", subjectService.getActiveSubjects());
        model.addAttribute("classes", schoolClassService.getAllClasses());
        return "teachers/form";
    }

    @GetMapping("/{id}")
    public String viewTeacher(@PathVariable Long id, Model model) {
        Optional<Teacher> teacher = teacherService.getTeacherById(id);
        if (teacher.isPresent()) {
            model.addAttribute("teacher", teacher.get());
            
            // Get classes where teacher is class teacher
            List<SchoolClass> classesAsTeacher = schoolClassService.getAllClasses().stream()
                .filter(c -> c.getClassTeacher() != null && c.getClassTeacher().getId().equals(id))
                .toList();
            model.addAttribute("classesAsTeacher", classesAsTeacher);
            
            return "teachers/view";
        }
        return "redirect:/teachers";
    }

    @GetMapping("/{id}/edit")
    public String showEditTeacherForm(@PathVariable Long id, Model model) {
        Optional<Teacher> teacher = teacherService.getTeacherById(id);
        if (teacher.isPresent()) {
            model.addAttribute("teacher", teacher.get());
            model.addAttribute("subjects", subjectService.getActiveSubjects());
            model.addAttribute("classes", schoolClassService.getAllClasses());
            return "teachers/form";
        }
        return "redirect:/teachers";
    }

    @PostMapping("/save")
    public String saveTeacher(@ModelAttribute Teacher teacher, 
                               @RequestParam(required = false) Set<Long> subjectIds,
                               @RequestParam(required = false) Long classTeacherId) {
        Teacher savedTeacher = teacherService.saveTeacher(teacher);
        
        if (subjectIds != null && !subjectIds.isEmpty()) {
            teacherService.assignSubjects(savedTeacher.getId(), subjectIds);
        }
        
        if (classTeacherId != null) {
            teacherService.assignClassTeacher(savedTeacher.getId(), classTeacherId);
        }
        
        return "redirect:/teachers";
    }

    @PostMapping("/{id}/update")
    public String updateTeacher(@PathVariable Long id, @ModelAttribute Teacher teacher) {
        teacherService.updateTeacher(id, teacher);
        return "redirect:/teachers/" + id;
    }

    @PostMapping("/{id}/assign-subjects")
    public String assignSubjects(@PathVariable Long id, @RequestParam Set<Long> subjectIds) {
        teacherService.assignSubjects(id, subjectIds);
        return "redirect:/teachers/" + id;
    }

    @PostMapping("/{id}/assign-class-teacher")
    public String assignClassTeacher(@PathVariable Long id, @RequestParam Long classId) {
        teacherService.assignClassTeacher(id, classId);
        return "redirect:/teachers/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return "redirect:/teachers";
    }
}
