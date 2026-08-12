package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentScheduleEarlyStatusException extends BusinessException {

  public InvalidPaymentScheduleEarlyStatusException() {
    super(
        "For prepayment, you do not need to have payments with the status PARTIALLY_PAID",
        HttpStatus.CONFLICT
    );
  }
}
