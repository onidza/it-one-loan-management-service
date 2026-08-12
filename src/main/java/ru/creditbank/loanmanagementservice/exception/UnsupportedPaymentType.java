package ru.creditbank.loanmanagementservice.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentType;

public class UnsupportedPaymentType extends BusinessException {

  public UnsupportedPaymentType(PaymentType type) {
    super(
        "Unsupported payment type: '%s'".formatted(type),
        HttpStatus.BAD_REQUEST
    );
  }
}
