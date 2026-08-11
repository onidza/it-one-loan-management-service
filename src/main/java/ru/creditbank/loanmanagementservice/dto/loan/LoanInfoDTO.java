package ru.creditbank.loanmanagementservice.dto.loan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;

public record LoanInfoDTO(
    UUID loanId,
    BigDecimal totalAmount,
    BigDecimal remainingAmount,
    LocalDate nextPaymentDate,
    LoanStatus status
) {

}
