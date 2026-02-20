package com.primaryschool.primaryschool.controller;

import com.primaryschool.primaryschool.entity.SchoolClass;
import com.primaryschool.primaryschool.entity.Subject;
import com.primaryschool.primaryschool.service.SchoolClassService;
import com.primaryschool.primaryschool.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;
    private final SchoolClassService schoolClassService;

    @Autowired
    public SubjectController(SubjectService subjectService, SchoolClassService schoolClassService) {
        this.subjectService = subjectService;
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getActiveSubjects());
        return "subjects/list";
    }

    @GetMapping("/new")
    public String showNewSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("classes", schoolClassService.getAllClasses());
        return "subjects/form";
    }

    @GetMapping("/{id}")
    public String viewSubject(@PathVariable Long id, Model model) {
        Optional<Subject> subject = subjectService.getSubjectById(id);
        if (subject.isPresent()) {
            model.addAttribute("subject", subject.get());
            return "subjects/view";
        }
        return "redirect:/subjects";
    }

    @GetMapping("/{id}/edit")
    public String showEditSubjectForm(@PathVariable Long id, Model model) {
        Optional<Subject> subject = subjectService.getSubjectById(id);
        if (subject.isPresent()) {
            model.addAttribute("subject", subject.get());
            model.addAttribute("classes", schoolClassService.getAllClasses());
            return "subjects/form";
        }
        return "redirect:/subjects";
    }

    @PostMapping("/save")
    public String saveSubject(@ModelAttribute Subject subject) {
        subjectService.saveSubject(subject);
        return "redirect:/subjects";
    }

    @PostMapping("/{id}/update")
    public String updateSubject(@PathVariable Long id, @ModelAttribute Subject subject) {
        subjectService.updateSubject(id, subject);
        return "redirect:/subjects/" + id;
    }

    @PostMapping("/{id}/assign-class")
    public String assignSubjectToClass(@PathVariable Long id, @RequestParam Long classId) {
        subjectService.assignSubjectToClass(id, classId);
        return "redirect:/subjects/" + id;
    }

    @GetMapping("/{id}/delete")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/subjects";
    }
}
