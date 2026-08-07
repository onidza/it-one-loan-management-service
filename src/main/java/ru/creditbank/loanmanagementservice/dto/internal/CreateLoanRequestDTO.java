package ru.creditbank.loanmanagementservice.dto.internal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanRequestDTO(
    @NotNull
    UUID creditApplicationId,

    @NotNull
    UUID userId,

    @NotNull
    @Positive
    @Digits(integer = 13, fraction = 2)
    BigDecimal totalAmount,

    @NotNull
    @Min(1)
    Integer termMonths,

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 3, fraction = 2)
    BigDecimal interestRate
) {

}
