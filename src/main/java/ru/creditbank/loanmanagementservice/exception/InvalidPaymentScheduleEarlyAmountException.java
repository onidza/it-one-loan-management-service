package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentScheduleEarlyAmountException extends BusinessException {

  public InvalidPaymentScheduleEarlyAmountException() {
    super(
        "Early payment must be not bigger then loan remaining amount",
        HttpStatus.BAD_REQUEST
    );
  }
}
