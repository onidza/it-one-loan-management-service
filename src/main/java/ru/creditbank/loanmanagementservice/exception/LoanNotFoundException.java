package ru.creditbank.loanmanagementservice.exception;

import java.util.UUID;

public class LoanNotFoundException extends RuntimeException {

  public LoanNotFoundException(UUID loanId) {
    super("Loan with id '%s' not found".formatted(loanId));
  }
}
