package complaint_management.repository;

import complaint_management.entity.ComplaintHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintHistoryRepository extends JpaRepository<ComplaintHistory, Long> {

    // Custom method to fetch the audit trail for a specific complaint
    List<ComplaintHistory> findByComplaintIdOrderByTimestampDesc(Long complaintId);
}