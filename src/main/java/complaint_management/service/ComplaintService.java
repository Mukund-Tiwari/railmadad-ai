package complaint_management.service;

import complaint_management.dto.ComplaintRequestDTO;
import complaint_management.dto.ComplaintResponseDTO;
import complaint_management.entity.Complaint;
import complaint_management.entity.User;
import complaint_management.enums.ComplaintCategory;
import complaint_management.enums.ComplaintPriority;
import complaint_management.enums.ComplaintStatus;
import complaint_management.enums.Department;
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
    private final ComplaintIntelligenceService complaintIntelligenceService;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            UserRepository userRepository,
            ComplaintIntelligenceService complaintIntelligenceService) {

        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.complaintIntelligenceService = complaintIntelligenceService;
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

        ComplaintCategory category =
                complaintIntelligenceService.predictCategory(
                        complaintRequestDTO.getTitle(),
                        complaintRequestDTO.getDescription()
                );

        ComplaintPriority priority =
                complaintIntelligenceService.predictPriority(
                        complaintRequestDTO.getTitle(),
                        complaintRequestDTO.getDescription()
                );

        Department department =
                complaintIntelligenceService.predictDepartment(category);

        complaint.setCategory(category);
        complaint.setPriority(priority);
        complaint.setDepartment(department);

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

    public ComplaintResponseDTO assignComplaintToAdmin(Long complaintId, Long adminId) {

        // 1. Find the Complaint
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Complaint not found with id : " + complaintId
                        ));

        // 2. Find the Admin User
        User adminUser = userRepository.findById(adminId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id : " + adminId
                        ));

        // 3. Verify the User is actually an ADMIN
        if (!adminUser.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("User is not an ADMIN and cannot be assigned complaints.");
        }

        // 4. Link the Admin to the Complaint
        complaint.setAssignedAdmin(adminUser);

        // 5. Automatically update the status
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        // 6. Save to Database
        Complaint updatedComplaint = complaintRepository.save(complaint);

        return mapToResponseDTO(updatedComplaint);
    }

    private ComplaintResponseDTO mapToResponseDTO(Complaint complaint) {

        ComplaintResponseDTO responseDTO = new ComplaintResponseDTO();

        responseDTO.setId(complaint.getId());
        responseDTO.setTitle(complaint.getTitle());
        responseDTO.setStatus(complaint.getStatus());
        responseDTO.setCategory(complaint.getCategory());
        responseDTO.setPriority(complaint.getPriority());
        responseDTO.setDepartment(complaint.getDepartment());

        // NEW LOGIC: Check if an admin is assigned, and if so, get their name!
        if (complaint.getAssignedAdmin() != null) {
            responseDTO.setAssignedAdminName(complaint.getAssignedAdmin().getName());
        }

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