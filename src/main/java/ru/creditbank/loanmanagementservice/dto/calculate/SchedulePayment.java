package ru.creditbank.loanmanagementservice.dto.calculate;

import java.math.BigDecimal;

public record SchedulePayment(
    BigDecimal principalAmount,
    BigDecimal interestAmount,
    BigDecimal totalAmount,
    BigDecimal remainingPrincipal
) {

}
