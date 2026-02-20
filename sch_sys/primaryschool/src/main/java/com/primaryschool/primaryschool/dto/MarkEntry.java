package com.primaryschool.primaryschool.dto;

public class MarkEntry {
    private Long studentId;
    private Double score;

    public MarkEntry() {
    }

    public MarkEntry(Long studentId, Double score) {
        this.studentId = studentId;
        this.score = score;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
