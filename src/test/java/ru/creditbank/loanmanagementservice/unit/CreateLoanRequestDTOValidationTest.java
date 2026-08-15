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
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanRequestDTO;

class CreateLoanRequestDTOValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validator = Validation
        .buildDefaultValidatorFactory()
        .getValidator();
  }

  @Test
  void shouldFailWhenRequiredIdsAreNull() {
    CreateLoanRequestDTO request =
        new CreateLoanRequestDTO(
            null,
            null,
            new BigDecimal("50000.00"),
            12,
            new BigDecimal("12.30")
        );

    Set<ConstraintViolation<CreateLoanRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains(
            "Credit application id is required",
            "User id is required"
        );
  }

  @Test
  void shouldFailWhenTotalAmountIsNotPositive() {
    CreateLoanRequestDTO request =
        new CreateLoanRequestDTO(
            UUID.randomUUID(),
            UUID.randomUUID(),
            BigDecimal.ZERO,
            12,
            new BigDecimal("12.30")
        );

    Set<ConstraintViolation<CreateLoanRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains(
            "Total amount must be greater than 0"
        );
  }

  @Test
  void shouldFailWhenLoanTermIsInvalid() {
    CreateLoanRequestDTO request =
        new CreateLoanRequestDTO(
            UUID.randomUUID(),
            UUID.randomUUID(),
            new BigDecimal("50000.00"),
            0,
            new BigDecimal("12.30")
        );

    Set<ConstraintViolation<CreateLoanRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains(
            "Loan term must be at least 1 month"
        );
  }

  @Test
  void shouldFailWhenInterestRateIsTooLow() {
    CreateLoanRequestDTO request =
        new CreateLoanRequestDTO(
            UUID.randomUUID(),
            UUID.randomUUID(),
            new BigDecimal("50000.00"),
            12,
            BigDecimal.ZERO
        );

    Set<ConstraintViolation<CreateLoanRequestDTO>> violations =
        validator.validate(request);

    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains(
            "Interest rate must be at least 0.01"
        );
  }
}
