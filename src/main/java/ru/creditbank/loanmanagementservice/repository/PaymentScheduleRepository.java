package ru.creditbank.loanmanagementservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleEntity;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentScheduleEntity, UUID> {

}
