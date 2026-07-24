package complaint_management.repository;

import complaint_management.entity.Complaint;
import complaint_management.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByUser_Email(String email);

    Optional<Complaint> findByIdAndUser_Email(Long id, String email);
    List<Complaint> findByStatusAndUser_Email(ComplaintStatus status, String email);

}
