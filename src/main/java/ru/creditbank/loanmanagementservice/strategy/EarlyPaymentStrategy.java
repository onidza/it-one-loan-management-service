package ru.creditbank.loanmanagementservice.strategy;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentProcessingResultDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleStatus;
import ru.creditbank.loanmanagementservice.entity.PaymentType;
import ru.creditbank.loanmanagementservice.exception.InvalidPaymentScheduleEarlyAmountException;
import ru.creditbank.loanmanagementservice.exception.InvalidPaymentScheduleEarlyStatusException;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;
import ru.creditbank.loanmanagementservice.service.ScheduleService;

@Component
@RequiredArgsConstructor
public class EarlyPaymentStrategy implements PaymentStrategy {

  private final PaymentScheduleRepository scheduleRepository;
  private final ScheduleService scheduleService;

  @Override
  public PaymentType getType() {
    return PaymentType.EARLY;
  }

  @Override
  public PaymentProcessingResultDTO process(
      LoanEntity loan,
      BigDecimal amount
  ) {
    if (amount.compareTo(loan.getRemainingAmount()) > 0) {
      throw new InvalidPaymentScheduleEarlyAmountException();
    }

    if (scheduleRepository.existsByLoanIdAndStatusIs(
        loan.getId(),
        PaymentScheduleStatus.PARTIALLY_PAID)
    ) {
      throw new InvalidPaymentScheduleEarlyStatusException();
    }

    loan.processEarlyPayment(amount);
    scheduleService.recalculateSchedule(loan);

    return new PaymentProcessingResultDTO(
        loan.getRemainingAmount(),
        loan.getNextPaymentDate()
    );
  }
}