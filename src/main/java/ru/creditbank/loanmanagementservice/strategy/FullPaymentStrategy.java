package ru.creditbank.loanmanagementservice.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentProcessingResultDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleStatus;
import ru.creditbank.loanmanagementservice.entity.PaymentType;
import ru.creditbank.loanmanagementservice.exception.InvalidPaymentScheduleFullAmountException;
import ru.creditbank.loanmanagementservice.exception.PaymentScheduleNotFoundException;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;

@Component
@RequiredArgsConstructor
public class FullPaymentStrategy implements PaymentStrategy {

  private final PaymentScheduleRepository scheduleRepository;

  @Override
  public PaymentType getType() {
    return PaymentType.FULL;
  }

  @Override
  public PaymentProcessingResultDTO process(
      LoanEntity loan,
      BigDecimal amount
  ) {
    PaymentScheduleEntity currentSchedule =
        scheduleRepository.findFirstByLoanIdAndStatusInOrderByInstallmentNumber(
                loan.getId(),
                List.of(
                    PaymentScheduleStatus.PARTIALLY_PAID,
                    PaymentScheduleStatus.PLANNED
                )
            )
            .orElseThrow(() ->
                new PaymentScheduleNotFoundException(loan.getId()));

    BigDecimal remainingPaymentAmount =
        currentSchedule.getTotalAmount()
            .subtract(currentSchedule.getPaidAmount());

    if (amount.compareTo(remainingPaymentAmount) != 0) {
      throw new InvalidPaymentScheduleFullAmountException();
    }

    currentSchedule.markPaid();

    LocalDate nextPaymentDate =
        scheduleRepository.findByLoanIdAndInstallmentNumber(
                loan.getId(),
                currentSchedule.getInstallmentNumber() + 1
            )
            .map(PaymentScheduleEntity::getPaymentDate)
            .orElse(null);

    loan.processFullPayment(
        currentSchedule.getRemainingPrincipal(),
        nextPaymentDate
    );

    return new PaymentProcessingResultDTO(
        loan.getRemainingAmount(),
        loan.getNextPaymentDate()
    );
  }
}