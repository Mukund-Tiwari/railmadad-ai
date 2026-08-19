package complaint_management.service;

import complaint_management.enums.ComplaintCategory;
import complaint_management.enums.ComplaintPriority;
import complaint_management.enums.Department;
import org.springframework.stereotype.Service;

@Service
public class ComplaintIntelligenceService {

    public ComplaintCategory predictCategory(String title, String description) {

        String text = combineText(title, description);

        if (text.contains("toilet") ||
                text.contains("dirty") ||
                text.contains("clean") ||
                text.contains("garbage") ||
                text.contains("smell")) {

            return ComplaintCategory.CLEANLINESS;
        }

        if (text.contains("water") ||
                text.contains("leakage") ||
                text.contains("tap")) {

            return ComplaintCategory.WATER;
        }

        if (text.contains("food") ||
                text.contains("meal") ||
                text.contains("tea") ||
                text.contains("breakfast") ||
                text.contains("lunch") ||
                text.contains("dinner")) {

            return ComplaintCategory.FOOD;
        }

        if (text.contains("medical") ||
                text.contains("doctor") ||
                text.contains("accident") ||
                text.contains("emergency") ||
                text.contains("injury")) {

            return ComplaintCategory.MEDICAL;
        }

        if (text.contains("theft") ||
                text.contains("stolen") ||
                text.contains("fight") ||
                text.contains("security") ||
                text.contains("harassment")) {

            return ComplaintCategory.SECURITY;
        }

        if (text.contains("fan") ||
                text.contains("light") ||
                text.contains("charging") ||
                text.contains("socket") ||
                text.contains("electric")) {

            return ComplaintCategory.ELECTRICAL;
        }

        return ComplaintCategory.GENERAL;
    }

    public ComplaintPriority predictPriority(String title, String description) {

        String text = combineText(title, description);

        if (text.contains("emergency") ||
                text.contains("accident") ||
                text.contains("medical") ||
                text.contains("injury") ||
                text.contains("fire") ||
                text.contains("theft") ||
                text.contains("harassment")) {

            return ComplaintPriority.HIGH;
        }

        if (text.contains("urgent") ||
                text.contains("leakage") ||
                text.contains("not working") ||
                text.contains("dirty") ||
                text.contains("security")) {

            return ComplaintPriority.MEDIUM;
        }

        return ComplaintPriority.LOW;
    }

    public Department predictDepartment(ComplaintCategory category) {

        if (category == ComplaintCategory.CLEANLINESS) {
            return Department.HOUSEKEEPING;
        }

        if (category == ComplaintCategory.WATER) {
            return Department.MECHANICAL;
        }

        if (category == ComplaintCategory.FOOD) {
            return Department.CATERING;
        }

        if (category == ComplaintCategory.MEDICAL) {
            return Department.MEDICAL_TEAM;
        }

        if (category == ComplaintCategory.SECURITY) {
            return Department.SECURITY_TEAM;
        }

        if (category == ComplaintCategory.ELECTRICAL) {
            return Department.ELECTRICAL_TEAM;
        }

        return Department.GENERAL_SUPPORT;
    }

    private String combineText(String title, String description) {

        return (title + " " + description).toLowerCase();
    }
}