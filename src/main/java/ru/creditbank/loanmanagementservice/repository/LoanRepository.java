package ru.creditbank.loanmanagementservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {

  boolean existsByCreditApplicationId(UUID applicationId);
}
