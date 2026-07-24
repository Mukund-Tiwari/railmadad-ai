package complaint_management.dto;

import complaint_management.enums.ComplaintStatus;

public class ComplaintResponseDTO {

    private Long id;
    private String title;
    private ComplaintStatus status;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }
}