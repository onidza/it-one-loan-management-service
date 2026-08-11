package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidLoanStatusCreateScheduleException extends BusinessException {

  public InvalidLoanStatusCreateScheduleException() {
    super(
        "Payment schedule can only be created for loan in CREATING status",
        HttpStatus.CONFLICT
    );
  }
}
