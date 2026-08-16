package ru.creditbank.loanmanagementservice.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {

  boolean existsByCreditApplicationId(UUID applicationId);

  List<LoanEntity> findAllByUserId(UUID userId);

  @Query("""
      select coalesce(sum(l.remainingAmount), 0)
      from LoanEntity l
      where l.userId = :userId
      and l.status = :activeStatus
      """)
  BigDecimal sumRemainingAmountByUserIdAndStatus(
      @Param("userId") UUID userId,
      @Param("activeStatus") LoanStatus activeStatus
  );
}
