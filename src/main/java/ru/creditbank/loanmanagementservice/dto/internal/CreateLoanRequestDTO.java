package ru.creditbank.loanmanagementservice.dto.internal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanRequestDTO(

    @NotNull(message = "Credit application id is required")
    UUID creditApplicationId,

    @NotNull(message = "User id is required")
    UUID userId,

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than 0")
    @Digits(
        integer = 13,
        fraction = 2,
        message = "Total amount must have up to 13 integer digits and 2 decimal places"
    )
    BigDecimal totalAmount,

    @NotNull(message = "Loan term is required")
    @Min(
        value = 1,
        message = "Loan term must be at least 1 month"
    )
    Integer termMonths,

    @NotNull(message = "Interest rate is required")
    @DecimalMin(
        value = "0.01",
        message = "Interest rate must be at least 0.01"
    )
    @Digits(
        integer = 3,
        fraction = 2,
        message = "Interest rate must have up to 3 integer digits and 2 decimal places"
    )
    BigDecimal interestRate,

    @NotBlank(message = "Access token must not be blank")
    String accessToken
) {

}
