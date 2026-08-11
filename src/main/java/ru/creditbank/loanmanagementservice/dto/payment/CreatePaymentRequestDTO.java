package ru.creditbank.loanmanagementservice.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequestDTO(

    @NotNull(message = "Loan id is required")
    UUID loanId,

    @NotNull(message = "Amount is required")
    @DecimalMin(
        value = "0.01",
        message = "Amount must be at least 0.01"
    )
    @Digits(
        integer = 13,
        fraction = 2,
        message = "Amount must have up to 13 integer digits and 2 decimal places"
    )
    BigDecimal amount,

    @NotNull(message = "Payment type is required")
    PaymentType paymentType
) {

}
