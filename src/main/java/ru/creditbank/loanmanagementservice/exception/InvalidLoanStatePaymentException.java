package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidLoanStatePaymentException extends BusinessException {

  public InvalidLoanStatePaymentException() {
    super(
        "Payment for loan is allowed only with status ACTIVE ",
        HttpStatus.CONFLICT
    );
  }
}
