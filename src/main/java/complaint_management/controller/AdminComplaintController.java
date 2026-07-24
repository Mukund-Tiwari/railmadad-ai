package complaint_management.controller;

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

    @PatchMapping("/{id}/status")
    public ComplaintResponseDTO updateComplaintStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequestDTO requestDTO) {

        return complaintService.updateComplaintStatus(
                id,
                requestDTO.getStatus()
        );
    }

    @DeleteMapping("/{id}")
    public String deleteComplaintByAdmin(@PathVariable Long id) {
        return complaintService.deleteComplaint(id);
    }
}