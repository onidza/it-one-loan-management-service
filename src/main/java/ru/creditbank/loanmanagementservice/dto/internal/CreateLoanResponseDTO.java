package ru.creditbank.loanmanagementservice.dto.internal;

import java.util.UUID;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;

public record CreateLoanResponseDTO(
    UUID loanId,
    LoanStatus status
) {

}
