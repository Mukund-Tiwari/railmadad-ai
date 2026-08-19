package complaint_management.dto;

import complaint_management.enums.ComplaintCategory;
import complaint_management.enums.ComplaintPriority;
import complaint_management.enums.ComplaintStatus;
import complaint_management.enums.Department;

public class ComplaintResponseDTO {

    private Long id;
    private String title;
    private ComplaintStatus status;
    private ComplaintCategory category;
    private ComplaintPriority priority;
    private Department department;

    // NEW FIELD: To show the customer who is handling their complaint
    private String assignedAdminName;

    public ComplaintResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public Department getDepartment() {
        return department;
    }

    // NEW GETTER
    public String getAssignedAdminName() {
        return assignedAdminName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public void setPriority(ComplaintPriority priority) {
        this.priority = priority;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    // NEW SETTER
    public void setAssignedAdminName(String assignedAdminName) {
        this.assignedAdminName = assignedAdminName;
    }
}