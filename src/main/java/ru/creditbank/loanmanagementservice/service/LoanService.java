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
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanRequestDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanResponseDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateScheduleResponseDTO;
import ru.creditbank.loanmanagementservice.dto.loanInfo.UserLoansResponseDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleEntity;
import ru.creditbank.loanmanagementservice.exception.DuplicateLoanException;
import ru.creditbank.loanmanagementservice.exception.InvalidLoanStatusException;
import ru.creditbank.loanmanagementservice.exception.LoanNotFoundException;
import ru.creditbank.loanmanagementservice.repository.LoanRepository;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

  private final LoanRepository loanRepository;
  private final PaymentScheduleRepository paymentScheduleRepository;

  private final Clock clock;
  private final PaymentScheduleCalculator scheduleCalculator;

  @Transactional(rollbackFor = Exception.class)
  public CreateLoanResponseDTO createLoan(CreateLoanRequestDTO request) {
    if (loanRepository.existsByCreditApplicationId(request.creditApplicationId())) {
      throw new DuplicateLoanException();
    }

    LoanEntity loan = loanRepository.save(
        new LoanEntity(
            request.creditApplicationId(),
            request.userId(),
            request.totalAmount(),
            request.termMonths(),
            request.interestRate()
        )
    );

    log.info("Created a new loan for applicationId = {}",
        loan.getCreditApplicationId());

    return new CreateLoanResponseDTO(
        loan.getId(),
        loan.getStatus()
    );
  }

  @Transactional(rollbackFor = Exception.class)
  public CreateScheduleResponseDTO createSchedule(UUID loanId) {
    LoanEntity loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new LoanNotFoundException(loanId));

    if (loan.getStatus() != LoanStatus.CREATING) {
      throw new InvalidLoanStatusException();
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
              loan,
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

    paymentScheduleRepository.saveAll(schedule);

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

  @Transactional(readOnly = true)
  public UserLoansResponseDTO getUserLoans(String subject) {

  }
}