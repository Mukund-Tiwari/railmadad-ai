package complaint_management.controller;

import complaint_management.dto.AdminAssignmentRequestDTO;
import complaint_management.dto.ComplaintResponseDTO;
import complaint_management.dto.StatusUpdateRequestDTO;
import complaint_management.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/complaints")
public class AdminComplaintController {

    private final ComplaintService complaintService;

    public AdminComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public List<ComplaintResponseDTO> getAllComplaintsForAdmin() {
        return complaintService.getAllComplaints();
    }

    @PatchMapping("/{id}/status-test")
    public String testPatchWithId(@PathVariable Long id) {

        System.out.println("PATCH WITH ID API HIT");
        System.out.println("Complaint ID: " + id);

        return "PATCH is working for complaint id: " + id;
    }

    @PatchMapping("/{id}/status")
    public ComplaintResponseDTO updateComplaintStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequestDTO requestDTO) {

        return complaintService.updateComplaintStatus(
                id,
                requestDTO.getStatus()
        );
    }

    // NEW ENDPOINT: To assign a complaint to an admin
    @PatchMapping("/{id}/assign")
    public ComplaintResponseDTO assignComplaint(
            @PathVariable Long id,
            @Valid @RequestBody AdminAssignmentRequestDTO requestDTO) {

        return complaintService.assignComplaintToAdmin(
                id,
                requestDTO.getAdminId()
        );
    }

    @DeleteMapping("/{id}")
    public String deleteComplaintByAdmin(@PathVariable Long id) {
        return complaintService.deleteComplaint(id);
    }
}