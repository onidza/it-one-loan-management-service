package ru.creditbank.loanmanagementservice.unit;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.creditbank.loanmanagementservice.dto.payment.CreatePaymentRequestDTO;
import ru.creditbank.loanmanagementservice.entity.PaymentType;

class CreatePaymentRequestDTOValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validator = Validation
        .buildDefaultValidatorFactory()
        .getValidator();
  }

  @Test
  void shouldFailWhenLoanIdIsNull() {
    CreatePaymentRequestDTO request =
        new CreatePaymentRequestDTO(
            null,
            new BigDecimal("1000.00"),
            PaymentType.FULL
        );

    Set<ConstraintViolation<CreatePaymentRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains(
            "Loan id is required"
        );
  }

  @Test
  void shouldFailWhenAmountIsTooLow() {
    CreatePaymentRequestDTO request =
        new CreatePaymentRequestDTO(
            UUID.randomUUID(),
            BigDecimal.ZERO,
            PaymentType.FULL
        );

    Set<ConstraintViolation<CreatePaymentRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains(
            "Amount must be at least 0.01"
        );
  }

  @Test
  void shouldFailWhenPaymentTypeIsNull() {
    CreatePaymentRequestDTO request =
        new CreatePaymentRequestDTO(
            UUID.randomUUID(),
            new BigDecimal("1000.00"),
            null
        );

    Set<ConstraintViolation<CreatePaymentRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains(
            "Payment type is required"
        );
  }
}
