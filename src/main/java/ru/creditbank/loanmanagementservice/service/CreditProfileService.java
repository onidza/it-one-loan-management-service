package ru.creditbank.loanmanagementservice.service;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.loanmanagementservice.dto.internal.CreditProfileDTO;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleStatus;
import ru.creditbank.loanmanagementservice.repository.LoanRepository;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditProfileService {

  private final PaymentScheduleRepository scheduleRepository;
  private final LoanRepository loanRepository;

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public CreditProfileDTO getCreditProfile(UUID userId) {

    long overduePaymentsCount = scheduleRepository.countByLoanUserIdAndStatus(
        userId,
        PaymentScheduleStatus.OVERDUE
    );

    long completedPaymentsCount = scheduleRepository.countByLoanUserIdAndStatus(
        userId,
        PaymentScheduleStatus.PAID
    );

    BigDecimal totalRemainingAmount = loanRepository.sumRemainingAmountByUserIdAndStatus(
        userId,
        LoanStatus.ACTIVE
    );

    log.info(
        "Credit profile calculated, userId = {}, overduePaymentsCount = {}, "
            + "completedPaymentsCount = {}, totalRemainingAmount = {}",
        userId,
        overduePaymentsCount,
        completedPaymentsCount,
        totalRemainingAmount
    );

    return new CreditProfileDTO(
        overduePaymentsCount,
        completedPaymentsCount,
        totalRemainingAmount
    );
  }
}
