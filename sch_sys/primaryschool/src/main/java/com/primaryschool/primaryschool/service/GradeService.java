package com.primaryschool.primaryschool.service;

import com.primaryschool.primaryschool.entity.*;
import com.primaryschool.primaryschool.repository.*;
import com.primaryschool.primaryschool.util.UgandaGradeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Autowired
    public GradeService(GradeRepository gradeRepository, StudentRepository studentRepository,
                        SubjectRepository subjectRepository, SchoolClassRepository schoolClassRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public List<Grade> getGradesByStudent(Long studentId, String term, Integer year) {
        return gradeRepository.findByStudentIdAndTermAndYear(studentId, term, year);
    }

    public List<Grade> getGradesByClass(Long classId, String term, Integer year) {
        return gradeRepository.findByClassIdAndTermAndYear(classId, term, year);
    }

    public Optional<Grade> getGrade(Long studentId, Long subjectId, String term, Integer year) {
        return gradeRepository.findByStudentIdAndSubjectIdAndTermAndYear(studentId, subjectId, term, year);
    }

    @Transactional
    public Grade saveGrade(Grade grade) {
        // Calculate grade letter and remarks
        String gradeLetter = UgandaGradeUtil.getGradeLetter(grade.getMarks());
        String remarks = UgandaGradeUtil.getRemarks(grade.getMarks());

        grade.setGradeLetter(gradeLetter);
        grade.setRemarks(remarks);

        return gradeRepository.save(grade);
    }

    @Transactional
    public Grade updateGrade(Long id, Grade gradeDetails) {
        Grade grade = gradeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Grade not found"));

        grade.setMarks(gradeDetails.getMarks());
        
        // Recalculate grade letter and remarks
        String gradeLetter = UgandaGradeUtil.getGradeLetter(gradeDetails.getMarks());
        String remarks = UgandaGradeUtil.getRemarks(gradeDetails.getMarks());

        grade.setGradeLetter(gradeLetter);
        grade.setRemarks(remarks);

        return gradeRepository.save(grade);
    }

    public boolean gradeExists(Long studentId, Long subjectId, String term, Integer year) {
        return gradeRepository.findByStudentIdAndSubjectIdAndTermAndYear(studentId, subjectId, term, year)
            .isPresent();
    }

    @Transactional
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    public List<Integer> getAllYears() {
        List<Integer> years = gradeRepository.findAllDistinctYears();
        if (years.isEmpty()) {
            years = new ArrayList<>();
            years.add(java.time.LocalDate.now().getYear());
        }
        return years;
    }

    /**
     * Calculate student performance for a specific term and year
     */
    public Map<String, Object> calculateStudentPerformance(Long studentId, String term, Integer year) {
        List<Grade> grades = gradeRepository.findByStudentIdAndTermAndYear(studentId, term, year);
        
        if (grades.isEmpty()) {
            return Collections.emptyMap();
        }

        double totalMarks = grades.stream().mapToDouble(Grade::getMarks).sum();
        double averageScore = totalMarks / grades.size();

        // Get best 4 subjects for aggregate
        List<Double> marksList = grades.stream()
            .map(Grade::getMarks)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

        int best4Sum = 0;
        for (int i = 0; i < Math.min(4, marksList.size()); i++) {
            best4Sum += marksList.get(i).intValue();
        }

        String division = UgandaGradeUtil.calculateDivision(best4Sum);

        Map<String, Object> performance = new HashMap<>();
        performance.put("totalMarks", totalMarks);
        performance.put("averageScore", averageScore);
        performance.put("best4Aggregate", best4Sum);
        performance.put("division", division);
        performance.put("grades", grades);
        performance.put("subjectCount", grades.size());

        return performance;
    }

    /**
     * Get class performance summary with rankings
     */
    public List<Map<String, Object>> getClassPerformance(Long classId, String term, Integer year) {
        List<Student> students = studentRepository.findBySchoolClassIdAndIsActiveTrue(classId);
        List<Map<String, Object>> performanceList = new ArrayList<>();

        for (Student student : students) {
            List<Grade> grades = gradeRepository.findByStudentIdAndTermAndYear(student.getId(), term, year);
            
            if (!grades.isEmpty()) {
                double totalMarks = grades.stream().mapToDouble(Grade::getMarks).sum();
                double averageScore = totalMarks / grades.size();

                // Get best 4 subjects
                List<Double> marksList = grades.stream()
                    .map(Grade::getMarks)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

                int best4Sum = 0;
                for (int i = 0; i < Math.min(4, marksList.size()); i++) {
                    best4Sum += marksList.get(i).intValue();
                }

                Map<String, Object> performance = new HashMap<>();
                performance.put("student", student);
                performance.put("totalMarks", totalMarks);
                performance.put("averageScore", averageScore);
                performance.put("best4Aggregate", best4Sum);
                performance.put("division", UgandaGradeUtil.calculateDivision(best4Sum));
                performance.put("grades", grades);

                performanceList.add(performance);
            }
        }

        // Sort by total marks descending
        performanceList.sort((a, b) -> 
            Double.compare((Double) b.get("totalMarks"), (Double) a.get("totalMarks")));

        // Add positions
        for (int i = 0; i < performanceList.size(); i++) {
            performanceList.get(i).put("position", i + 1);
        }

        return performanceList;
    }

    /**
     * Get class performance summary with mean score
     */
    public Map<String, Object> getClassPerformanceWithStats(Long classId, String term, Integer year) {
        List<Map<String, Object>> performanceList = getClassPerformance(classId, term, year);
        
        Map<String, Object> result = new HashMap<>();
        result.put("students", performanceList);
        
        // Calculate mean score
        if (!performanceList.isEmpty()) {
            double sum = performanceList.stream()
                .mapToDouble(p -> (Double) p.get("averageScore"))
                .sum();
            double mean = sum / performanceList.size();
            result.put("meanScore", Math.round(mean * 100.0) / 100.0);
            
            // Get top 3
            result.put("top3", performanceList.stream().limit(3).toList());
        } else {
            result.put("meanScore", 0.0);
            result.put("top3", new ArrayList<>());
        }
        
        return result;
    }

    /**
     * Get top 3 performers in a class
     */
    public List<Map<String, Object>> getTopPerformers(Long classId, String term, Integer year) {
        List<Map<String, Object>> performanceList = getClassPerformance(classId, term, year);
        return performanceList.stream().limit(3).collect(Collectors.toList());
    }

    /**
     * Get subject average for a class
     */
    public Double getSubjectAverage(Long classId, Long subjectId, String term, Integer year) {
        return gradeRepository.getAverageMarksByClassAndSubject(classId, subjectId, term, year);
    }

    /**
     * Generate class report
     */
    public Map<String, Object> generateClassReport(Long classId, String term, Integer year) {
        List<Map<String, Object>> performanceList = getClassPerformance(classId, term, year);
        
        Map<String, Object> report = new HashMap<>();
        
        if (!performanceList.isEmpty()) {
            double classTotal = performanceList.stream()
                .mapToDouble(p -> (Double) p.get("totalMarks"))
                .sum();
            
            double classAverage = classTotal / performanceList.size();

            // Get subject averages
            List<Subject> subjects = subjectRepository.findSubjectsByClassId(classId);
            Map<String, Double> subjectAverages = new HashMap<>();
            
            for (Subject subject : subjects) {
                Double avg = getSubjectAverage(classId, subject.getId(), term, year);
                if (avg != null) {
                    subjectAverages.put(subject.getSubjectName(), avg);
                }
            }

            report.put("totalStudents", performanceList.size());
            report.put("classAverage", classAverage);
            report.put("subjectAverages", subjectAverages);
            report.put("topPerformers", performanceList.stream().limit(3).collect(Collectors.toList()));
            report.put("performanceList", performanceList);
        } else {
            report.put("totalStudents", 0);
            report.put("classAverage", 0.0);
            report.put("subjectAverages", Collections.emptyMap());
            report.put("topPerformers", Collections.emptyList());
            report.put("performanceList", Collections.emptyList());
        }

        return report;
    }
}
