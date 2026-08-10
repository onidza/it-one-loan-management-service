package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicateLoanException extends BusinessException {

  public DuplicateLoanException() {
    super(
        "Loan has already been submitted",
        HttpStatus.BAD_REQUEST
    );
  }
}
