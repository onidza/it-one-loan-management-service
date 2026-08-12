package ru.creditbank.loanmanagementservice.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

public class PaymentsNotFoundException extends BusinessException {

  public PaymentsNotFoundException(UUID loanId) {
    super(
        "Loan id = '%s' still have no payments".formatted(loanId),
        HttpStatus.NOT_FOUND
    );
  }
}
