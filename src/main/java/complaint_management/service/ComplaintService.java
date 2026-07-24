package complaint_management.service;

import complaint_management.dto.ComplaintRequestDTO;
import complaint_management.dto.ComplaintResponseDTO;
import complaint_management.entity.Complaint;
import complaint_management.entity.User;
import complaint_management.enums.ComplaintStatus;
import complaint_management.exception.ResourceNotFoundException;
import complaint_management.repository.ComplaintRepository;
import complaint_management.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            UserRepository userRepository) {

        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    public List<ComplaintResponseDTO> getAllComplaints() {

        List<Complaint> complaints;

        if (isAdmin()) {
            complaints = complaintRepository.findAll();
        } else {
            String email = getLoggedInUserEmail();
            complaints = complaintRepository.findByUser_Email(email);
        }

        List<ComplaintResponseDTO> responseList = new ArrayList<>();

        for (Complaint complaint : complaints) {
            responseList.add(mapToResponseDTO(complaint));
        }

        return responseList;
    }

    public ComplaintResponseDTO addComplaint(
            ComplaintRequestDTO complaintRequestDTO) {

        String email = getLoggedInUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email : " + email
                        ));

        Complaint complaint = new Complaint();

        complaint.setTitle(complaintRequestDTO.getTitle());
        complaint.setDescription(complaintRequestDTO.getDescription());
        complaint.setStatus(complaintRequestDTO.getStatus());
        complaint.setUser(user);

        Complaint savedComplaint = complaintRepository.save(complaint);

        return mapToResponseDTO(savedComplaint);
    }

    public Complaint getComplaintById(Long id) {

        if (isAdmin()) {
            return complaintRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Complaint not found with id : " + id
                            ));
        }

        String email = getLoggedInUserEmail();

        return complaintRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Complaint not found with id : " + id
                        ));
    }

    public String deleteComplaint(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Complaint not found with id : " + id
                        ));

        complaintRepository.delete(complaint);

        return "Complaint Deleted Successfully";
    }

    public Complaint updateComplaint(
            Long id,
            Complaint updatedComplaint) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Complaint not found with id : " + id
                        ));

        complaint.setTitle(updatedComplaint.getTitle());
        complaint.setDescription(updatedComplaint.getDescription());
        complaint.setStatus(updatedComplaint.getStatus());

        return complaintRepository.save(complaint);
    }

    public List<Complaint> getComplaintsByStatus(
            ComplaintStatus status) {

        if (isAdmin()) {
            return complaintRepository.findByStatus(status);
        }

        String email = getLoggedInUserEmail();

        return complaintRepository.findByStatusAndUser_Email(status, email);
    }

    public ComplaintResponseDTO updateComplaintStatus(
            Long id,
            ComplaintStatus status) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Complaint not found with id : " + id
                        ));

        complaint.setStatus(status);

        Complaint updatedComplaint = complaintRepository.save(complaint);

        return mapToResponseDTO(updatedComplaint);
    }




    private ComplaintResponseDTO mapToResponseDTO(Complaint complaint) {

        ComplaintResponseDTO responseDTO = new ComplaintResponseDTO();

        responseDTO.setId(complaint.getId());
        responseDTO.setTitle(complaint.getTitle());
        responseDTO.setStatus(complaint.getStatus());

        return responseDTO;
    }

    private String getLoggedInUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }

    private boolean isAdmin() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }
}