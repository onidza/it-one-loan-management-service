package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidLoanStatusException extends BusinessException {

  public InvalidLoanStatusException() {
    super(
        "Payment schedule can only be created for loan in CREATING status",
        HttpStatus.CONFLICT
    );
  }
}
