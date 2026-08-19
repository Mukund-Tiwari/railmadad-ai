package complaint_management.dto;

import jakarta.validation.constraints.NotNull;

public class AdminAssignmentRequestDTO {

    @NotNull(message = "Admin ID is required")
    private Long adminId;

    // Default constructor
    public AdminAssignmentRequestDTO() {
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}