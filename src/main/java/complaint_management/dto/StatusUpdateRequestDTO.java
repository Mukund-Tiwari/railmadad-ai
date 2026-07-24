package complaint_management.dto;

import complaint_management.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequestDTO {

    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    public StatusUpdateRequestDTO() {
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }
}