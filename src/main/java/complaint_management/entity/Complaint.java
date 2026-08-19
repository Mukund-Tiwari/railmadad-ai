package complaint_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import complaint_management.enums.ComplaintCategory;
import complaint_management.enums.ComplaintPriority;
import complaint_management.enums.ComplaintStatus;
import complaint_management.enums.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    private ComplaintPriority priority;

    @Enumerated(EnumType.STRING)
    private Department department;

    // First Relationship: The user who created the complaint
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Second Relationship: The Admin assigned to resolve the complaint
    @ManyToOne
    @JoinColumn(name = "assigned_admin_id")
    @JsonIgnore
    private User assignedAdmin;

    public Complaint() {
    }

    public Complaint(Long id,
                     String title,
                     String description,
                     ComplaintStatus status,
                     ComplaintCategory category,
                     ComplaintPriority priority,
                     Department department,
                     User user,
                     User assignedAdmin) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.category = category;
        this.priority = priority;
        this.department = department;
        this.user = user;
        this.assignedAdmin = assignedAdmin;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

    public User getUser() {
        return user;
    }

    public User getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setAssignedAdmin(User assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }
}