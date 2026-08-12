package ru.creditbank.loanmanagementservice.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.loanmanagementservice.entity.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

  List<PaymentEntity> findAllByLoanId(UUID loanId);
}
