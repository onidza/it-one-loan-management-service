package ru.creditbank.loanmanagementservice.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

public class PaymentScheduleNotFoundException extends BusinessException {

  public PaymentScheduleNotFoundException(UUID loanId) {
    super(
        "Payment schedule for loan '%s' not found".formatted(loanId),
        HttpStatus.NOT_FOUND
    );
  }
}
