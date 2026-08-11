package ru.creditbank.loanmanagementservice.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.loanmanagementservice.dto.loan.LoanInfoDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {

  boolean existsByCreditApplicationId(UUID applicationId);

  List<LoanEntity> findAllByUserId(UUID userId);
}
