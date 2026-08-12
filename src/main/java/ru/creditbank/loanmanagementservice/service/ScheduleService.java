package ru.creditbank.loanmanagementservice.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.loanmanagementservice.calculator.PaymentScheduleCalculator;
import ru.creditbank.loanmanagementservice.dto.calculate.SchedulePayment;
import ru.creditbank.loanmanagementservice.dto.internal.CreateScheduleResponseDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleStatus;
import ru.creditbank.loanmanagementservice.exception.InvalidLoanStatusCreateScheduleException;
import ru.creditbank.loanmanagementservice.exception.LoanNotFoundException;
import ru.creditbank.loanmanagementservice.repository.LoanRepository;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

  private final PaymentScheduleRepository scheduleRepository;
  private final LoanRepository loanRepository;
  private final Clock clock;
  private final PaymentScheduleCalculator scheduleCalculator;

  @Transactional(rollbackFor = Exception.class)
  public CreateScheduleResponseDTO createSchedule(UUID loanId) {
    LoanEntity loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new LoanNotFoundException(loanId));

    if (loan.getStatus() != LoanStatus.CREATING) {
      throw new InvalidLoanStatusCreateScheduleException();
    }

    Instant activatedAt = clock.instant();
    LocalDate firstPaymentDate = activatedAt
        .atZone(clock.getZone())
        .toLocalDate()
        .plusMonths(1);

    BigDecimal remainingPrincipal = loan.getTotalAmount();
    List<PaymentScheduleEntity> schedule = new ArrayList<>();

    for (int i = 1; i <= loan.getTermMonths(); i++) {
      LocalDate paymentDate = firstPaymentDate.plusMonths(i - 1L);

      SchedulePayment calculation =
          scheduleCalculator.calculate(
              loan.getTotalAmount(),
              loan.getInterestRate(),
              loan.getTermMonths(),
              i,
              remainingPrincipal
          );

      PaymentScheduleEntity scheduleLine = new PaymentScheduleEntity(
          loan,
          i,
          paymentDate,
          calculation.principalAmount(),
          calculation.interestAmount(),
          calculation.totalAmount(),
          calculation.remainingPrincipal()
      );

      schedule.add(scheduleLine);
      remainingPrincipal = calculation.remainingPrincipal();
    }

    scheduleRepository.saveAll(schedule);

    log.info(
        "Payment schedule created for loanId = {}, installmentsCount = {}",
        loanId,
        schedule.size()
    );

    loan.activate(activatedAt, firstPaymentDate);

    log.info(
        "Loan activated, loanId = {}, activatedAt = {}, firstPaymentDate = {}",
        loanId,
        activatedAt,
        firstPaymentDate
    );

    return new CreateScheduleResponseDTO(
        loan.getId(),
        schedule.size(),
        firstPaymentDate,
        loan.getStatus()
    );
  }

  @Transactional(rollbackFor = Exception.class)
  public void recalculateSchedule(LoanEntity loan) {
    List<PaymentScheduleEntity> plannedSchedule =
        scheduleRepository
            .findAllByLoanIdAndStatusOrderByInstallmentNumber(
                loan.getId(),
                PaymentScheduleStatus.PLANNED
            );

    if (plannedSchedule.isEmpty()) {
      return;
    }

    if (loan.getRemainingAmount().signum() == 0) {
      plannedSchedule.forEach(PaymentScheduleEntity::cancel);

      log.info(
          "Remaining payment schedule cancelled for closed loanId = {}",
          loan.getId()
      );

      return;
    }

    BigDecimal newPrincipal = loan.getRemainingAmount();
    BigDecimal remainingPrincipal = newPrincipal;

    int remainingTerm = plannedSchedule.size();

    for (int i = 0; i < plannedSchedule.size(); i++) {
      PaymentScheduleEntity scheduleLine =
          plannedSchedule.get(i);

      SchedulePayment calculation =
          scheduleCalculator.calculate(
              newPrincipal,
              loan.getInterestRate(),
              remainingTerm,
              i + 1,
              remainingPrincipal
          );

      scheduleLine.recalculate(
          calculation.principalAmount(),
          calculation.interestAmount(),
          calculation.totalAmount(),
          calculation.remainingPrincipal()
      );

      remainingPrincipal = calculation.remainingPrincipal();
    }

    log.info(
        "Payment schedule recalculated for loanId = {}, "
            + "installmentsCount = {}, remainingPrincipal = {}",
        loan.getId(),
        plannedSchedule.size(),
        loan.getRemainingAmount()
    );
  }
}