package ru.creditbank.loanmanagementservice.dto.internal;

import java.math.BigDecimal;

public record CreditProfileDTO(
    long existingOverduePaymentsCount,
    long completedPaymentsCount,
    BigDecimal totalRemainingAmount
) {

}
