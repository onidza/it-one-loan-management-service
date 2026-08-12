package ru.creditbank.loanmanagementservice.strategy;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentProcessingResultDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentType;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleStatus;
import ru.creditbank.loanmanagementservice.exception.InvalidPaymentSchedulePartialAmountException;
import ru.creditbank.loanmanagementservice.exception.PaymentScheduleNotFoundException;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;

@Component
@RequiredArgsConstructor
public class PartialPaymentStrategy implements PaymentStrategy {

  private final PaymentScheduleRepository scheduleRepository;

  @Override
  public PaymentType getType() {
    return PaymentType.PARTIAL;
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

    if (amount.compareTo(remainingPaymentAmount) >= 0) {
      throw new InvalidPaymentSchedulePartialAmountException();
    }

    BigDecimal paidPrincipal =
        currentSchedule.processPartialPayment(amount);

    loan.processPartialPayment(paidPrincipal);

    return new PaymentProcessingResultDTO(
        loan.getRemainingAmount(),
        loan.getNextPaymentDate()
    );
  }
}
