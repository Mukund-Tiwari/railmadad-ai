package complaint_management.service;

import complaint_management.dto.ComplaintHistoryDTO;
import complaint_management.dto.ComplaintRequestDTO;
import complaint_management.dto.ComplaintResponseDTO;
import complaint_management.entity.Complaint;
import complaint_management.entity.ComplaintHistory;
import complaint_management.entity.User;
import complaint_management.enums.ComplaintCategory;
import complaint_management.enums.ComplaintPriority;
import complaint_management.enums.ComplaintStatus;
import complaint_management.enums.Department;
import complaint_management.exception.ResourceNotFoundException;
import complaint_management.repository.ComplaintHistoryRepository;
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
    private final ComplaintHistoryRepository complaintHistoryRepository;

    // IMPORTANT: I added the ComplaintHistoryRepository to the constructor here!
    public ComplaintService(
            ComplaintRepository complaintRepository,
            UserRepository userRepository,
            ComplaintIntelligenceService complaintIntelligenceService,
            ComplaintHistoryRepository complaintHistoryRepository) {

        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.complaintIntelligenceService = complaintIntelligenceService;
        this.complaintHistoryRepository = complaintHistoryRepository;
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

    public ComplaintResponseDTO addComplaint(ComplaintRequestDTO complaintRequestDTO) {
        String email = getLoggedInUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email : " + email));

        Complaint complaint = new Complaint();
        complaint.setTitle(complaintRequestDTO.getTitle());
        complaint.setDescription(complaintRequestDTO.getDescription());
        complaint.setStatus(complaintRequestDTO.getStatus());
        complaint.setUser(user);

        ComplaintCategory category = complaintIntelligenceService.predictCategory(
                complaintRequestDTO.getTitle(), complaintRequestDTO.getDescription());

        ComplaintPriority priority = complaintIntelligenceService.predictPriority(
                complaintRequestDTO.getTitle(), complaintRequestDTO.getDescription());

        Department department = complaintIntelligenceService.predictDepartment(category);

        complaint.setCategory(category);
        complaint.setPriority(priority);
        complaint.setDepartment(department);

        Complaint savedComplaint = complaintRepository.save(complaint);

        return mapToResponseDTO(savedComplaint);
    }

    public Complaint getComplaintById(Long id) {
        if (isAdmin()) {
            return complaintRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id : " + id));
        }

        String email = getLoggedInUserEmail();
        return complaintRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id : " + id));
    }

    public String deleteComplaint(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id : " + id));

        complaintRepository.delete(complaint);
        return "Complaint Deleted Successfully";
    }

    public Complaint updateComplaint(Long id, Complaint updatedComplaint) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id : " + id));

        complaint.setTitle(updatedComplaint.getTitle());
        complaint.setDescription(updatedComplaint.getDescription());
        complaint.setStatus(updatedComplaint.getStatus());

        return complaintRepository.save(complaint);
    }

    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        if (isAdmin()) {
            return complaintRepository.findByStatus(status);
        }

        String email = getLoggedInUserEmail();
        return complaintRepository.findByStatusAndUser_Email(status, email);
    }

    public List<ComplaintHistoryDTO> getComplaintHistory(Long complaintId) {

        // 1. Check if the complaint actually exists
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id : " + complaintId));

        // 2. Fetch the history from the database (newest first)
        List<ComplaintHistory> historyLogs = complaintHistoryRepository.findByComplaintIdOrderByTimestampDesc(complaintId);

        // 3. Convert the Entities into clean DTOs
        List<ComplaintHistoryDTO> dtoList = new ArrayList<>();

        for (ComplaintHistory log : historyLogs) {
            ComplaintHistoryDTO dto = new ComplaintHistoryDTO();
            dto.setId(log.getId());
            dto.setOldStatus(log.getOldStatus());
            dto.setNewStatus(log.getNewStatus());
            dto.setChangedBy(log.getChangedBy());
            dto.setRemarks(log.getRemarks());
            dto.setTimestamp(log.getTimestamp());

            dtoList.add(dto);
        }

        return dtoList;
    }

    public ComplaintResponseDTO updateComplaintStatus(Long id, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id : " + id));

        ComplaintStatus currentStatus = complaint.getStatus();
        boolean isValidTransition = false;

        if(currentStatus == ComplaintStatus.OPEN && status == ComplaintStatus.IN_PROGRESS){
            isValidTransition = true;
        } else if(currentStatus == ComplaintStatus.IN_PROGRESS && status == ComplaintStatus.RESOLVED){
            isValidTransition = true;
        } else if(currentStatus == ComplaintStatus.RESOLVED && status == ComplaintStatus.CLOSED){
            isValidTransition = true;
        }

        if(!isValidTransition){
            throw new IllegalArgumentException(
                    "Invalid status transition! you cannot move from " +  currentStatus + " to " + status
            );
        }

        complaint.setStatus(status);
        Complaint updatedComplaint = complaintRepository.save(complaint);

        // --- NEW AUDIT TRAIL LOGIC ---
        ComplaintHistory history = new ComplaintHistory();
        history.setComplaint(updatedComplaint);
        history.setOldStatus(currentStatus);
        history.setNewStatus(status);
        history.setChangedBy(getLoggedInUserEmail());
        history.setRemarks("Status updated manually");

        complaintHistoryRepository.save(history);
        // -----------------------------

        return mapToResponseDTO(updatedComplaint);
    }

    public ComplaintResponseDTO assignComplaintToAdmin(Long complaintId, Long adminId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id : " + complaintId));

        User adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + adminId));

        if (!adminUser.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("User is not an ADMIN and cannot be assigned complaints.");
        }

        ComplaintStatus oldStatus = complaint.getStatus(); // Capture old status before changing

        complaint.setAssignedAdmin(adminUser);
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        Complaint updatedComplaint = complaintRepository.save(complaint);

        // --- NEW AUDIT TRAIL LOGIC ---
        ComplaintHistory history = new ComplaintHistory();
        history.setComplaint(updatedComplaint);
        history.setOldStatus(oldStatus);
        history.setNewStatus(ComplaintStatus.IN_PROGRESS);
        history.setChangedBy(getLoggedInUserEmail());
        history.setRemarks("Assigned to Admin: " + adminUser.getName());

        complaintHistoryRepository.save(history);
        // -----------------------------

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

        if (complaint.getAssignedAdmin() != null) {
            responseDTO.setAssignedAdminName(complaint.getAssignedAdmin().getName());
        }

        return responseDTO;
    }

    private String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}