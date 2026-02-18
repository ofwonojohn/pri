package com.primaryschool.primaryschool.util;

/**
 * Uganda Primary School Grading System
 * Based on the Uganda Primary Curriculum
 */
public class UgandaGradeUtil {

    /**
     * Get grade letter based on marks
     * Uganda Primary School Grading Scale:
     * D1: 90-100 (Distinction 1)
     * D2: 80-89 (Distinction 2)
     * C3: 75-79 (Credit 3)
     * C4: 70-74 (Credit 4)
     * C5: 65-69 (Credit 5)
     * C6: 60-64 (Credit 6)
     * P7: 55-59 (Pass 7)
     * P8: 50-54 (Pass 8)
     * F9: 0-49 (Fail 9)
     */
    public static String getGradeLetter(Double marks) {
        if (marks == null) return "N/A";
        
        if (marks >= 90) return "D1";
        else if (marks >= 80) return "D2";
        else if (marks >= 75) return "C3";
        else if (marks >= 70) return "C4";
        else if (marks >= 65) return "C5";
        else if (marks >= 60) return "C6";
        else if (marks >= 55) return "P7";
        else if (marks >= 50) return "P8";
        else return "F9";
    }

    /**
     * Get remarks based on marks
     */
    public static String getRemarks(Double marks) {
        if (marks == null) return "N/A";
        
        if (marks >= 90) return "Excellent";
        else if (marks >= 80) return "Very Good";
        else if (marks >= 70) return "Good";
        else if (marks >= 60) return "Satisfactory";
        else if (marks >= 50) return "Pass";
        else return "Fail";
    }

    /**
     * Calculate division based on aggregate (best 4 subjects)
     * Division I: 4-12 (best 4 subjects)
     * Division II: 13-24
     * Division III: 25-36
     * Division IV: 37 and above
     */
    public static String calculateDivision(int aggregate) {
        if (aggregate <= 12) return "I";
        else if (aggregate <= 24) return "II";
        else if (aggregate <= 36) return "III";
        else return "IV";
    }

    /**
     * Get grade points for aggregate calculation
     * D1 = 1, D2 = 2, C3 = 3, C4 = 4, C5 = 5, C6 = 6, P7 = 7, P8 = 8, F9 = 9
     */
    public static int getGradePoints(String gradeLetter) {
        if (gradeLetter == null) return 9;
        
        return switch (gradeLetter) {
            case "D1" -> 1;
            case "D2" -> 2;
            case "C3" -> 3;
            case "C4" -> 4;
            case "C5" -> 5;
            case "C6" -> 6;
            case "P7" -> 7;
            case "P8" -> 8;
            default -> 9;
        };
    }

    /**
     * Get grade description
     */
    public static String getGradeDescription(String gradeLetter) {
        if (gradeLetter == null) return "Not Graded";
        
        return switch (gradeLetter) {
            case "D1" -> "Distinction 1";
            case "D2" -> "Distinction 2";
            case "C3" -> "Credit 3";
            case "C4" -> "Credit 4";
            case "C5" -> "Credit 5";
            case "C6" -> "Credit 6";
            case "P7" -> "Pass 7";
            case "P8" -> "Pass 8";
            case "F9" -> "Fail 9";
            default -> "Not Graded";
        };
    }
}
