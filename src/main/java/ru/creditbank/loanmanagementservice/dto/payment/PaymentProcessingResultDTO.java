package ru.creditbank.loanmanagementservice.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentProcessingResultDTO(
    BigDecimal newBalance,
    LocalDate nextPaymentDate
) {

}
