package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentSchedulePartialAmountException extends BusinessException {

  public InvalidPaymentSchedulePartialAmountException() {
    super(
        "Payment amount must be less remaining loan amount",
        HttpStatus.BAD_REQUEST
    );
  }
}
