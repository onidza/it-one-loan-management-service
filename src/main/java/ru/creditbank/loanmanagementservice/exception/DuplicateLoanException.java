package ru.creditbank.loanmanagementservice.exception;

public class DuplicateLoanException extends RuntimeException {

  public DuplicateLoanException(String message) {
    super(message);
  }
}
