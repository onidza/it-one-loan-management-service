package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class LoanAccessDeniedException extends BusinessException {

  public LoanAccessDeniedException() {
    super(
        "Access to loan is denied",
        HttpStatus.FORBIDDEN
    );
  }
}
