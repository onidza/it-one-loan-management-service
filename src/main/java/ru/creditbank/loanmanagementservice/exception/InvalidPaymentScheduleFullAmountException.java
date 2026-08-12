package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentScheduleFullAmountException extends BusinessException {

  public InvalidPaymentScheduleFullAmountException() {
    super(
        "Payment amount must not exceed remaining loan amount",
        HttpStatus.BAD_REQUEST
    );
  }
}
