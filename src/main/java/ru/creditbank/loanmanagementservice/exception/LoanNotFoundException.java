package ru.creditbank.loanmanagementservice.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

public class LoanNotFoundException extends BusinessException {

  public LoanNotFoundException(UUID loanId) {
    super(
        "Loan with id '%s' not found".formatted(loanId),
        HttpStatus.NOT_FOUND
    );
  }
}
