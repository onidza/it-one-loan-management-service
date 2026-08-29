package ru.creditbank.loanmanagementservice.dto.kafka;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanIssuanceEventDTO(
    UUID creditApplicationId,
    UUID userId,
    BigDecimal totalAmount,
    Integer termMonths,
    BigDecimal interestRate,
    String accessToken
) {

}
