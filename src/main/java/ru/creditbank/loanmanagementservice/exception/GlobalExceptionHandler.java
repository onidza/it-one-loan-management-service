package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.creditbank.loanmanagementservice.dto.error.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateLoanException.class)
  public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
      DuplicateLoanException exception
  ) {
    ErrorResponseDTO response = new ErrorResponseDTO(
        HttpStatus.BAD_REQUEST.value(),
        exception.getMessage()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  @ExceptionHandler(LoanNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
      LoanNotFoundException exception
  ) {
    ErrorResponseDTO response = new ErrorResponseDTO(
        HttpStatus.NOT_FOUND.value(),
        exception.getMessage()
    );

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(response);
  }
}
