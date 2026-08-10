package ru.creditbank.loanmanagementservice.exception;

public class InvalidLoanStatusException extends RuntimeException {

  public InvalidLoanStatusException(String message) {
    super(message);
  }
}
