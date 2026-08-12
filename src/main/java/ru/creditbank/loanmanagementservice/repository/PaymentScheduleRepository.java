package ru.creditbank.loanmanagementservice.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleStatus;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentScheduleEntity, UUID> {

  Optional<PaymentScheduleEntity>
  findFirstByLoanIdAndStatusInOrderByInstallmentNumber(
      UUID loanId,
      Collection<PaymentScheduleStatus> statuses
  );

  Optional<PaymentScheduleEntity> findByLoanIdAndInstallmentNumber(
      UUID loanId,
      Integer installmentNumber
  );

  List<PaymentScheduleEntity>
  findAllByLoanIdAndStatusOrderByInstallmentNumber(
      UUID loanId,
      PaymentScheduleStatus status
  );

  boolean existsByLoanIdAndStatusIs(
      UUID loanId,
      PaymentScheduleStatus status);
}
