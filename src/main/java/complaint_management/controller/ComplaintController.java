package complaint_management.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import complaint_management.entity.Complaint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import complaint_management.service.ComplaintService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;
import complaint_management.enums.ComplaintStatus;
import complaint_management.dto.ComplaintRequestDTO;
import complaint_management.dto.ComplaintResponseDTO;

@RestController
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(
            ComplaintService complaintService) {

        this.complaintService = complaintService;
    }


    @GetMapping("/complaints")
    public List<ComplaintResponseDTO> home(){

        return complaintService.getAllComplaints();
    }



    @GetMapping("/complaints/{id}")
    public Complaint getComplaintById(
            @PathVariable Long id) {

        return complaintService.getComplaintById(id);
    }

    @GetMapping("/complaints/status/{status}")
    public List<Complaint> getComplaintsByStatus(
            @PathVariable ComplaintStatus status) {


        return complaintService.getComplaintsByStatus(
                status
        );


    }
    @DeleteMapping("/complaints/{id}")
    public String deleteComplaint(
            @PathVariable Long id) {

        return complaintService.deleteComplaint(id);
    }


    @PostMapping("/complaints")
    public ComplaintResponseDTO createComplaint(
            @Valid @RequestBody ComplaintRequestDTO complaintRequestDTO) {

        return complaintService.addComplaint(complaintRequestDTO);
    }

    @PutMapping("/complaints/{id}")
    public Complaint updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody Complaint updatedComplaint) {

        return complaintService.updateComplaint(
                id,
                updatedComplaint
        );
    }
}