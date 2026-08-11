package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidLoanPaymentAmountException extends BusinessException {

  public InvalidLoanPaymentAmountException() {
    super(
        "Payment amount must not exceed remaining loan amount",
        HttpStatus.BAD_REQUEST
    );
  }
}
