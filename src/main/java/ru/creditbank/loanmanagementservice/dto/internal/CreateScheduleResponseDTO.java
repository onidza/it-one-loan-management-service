package ru.creditbank.loanmanagementservice.dto.internal;

import java.time.LocalDate;
import java.util.UUID;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;

public record CreateScheduleResponseDTO(
    UUID loanId,
    Integer schedulerSize,
    LocalDate fistPaymentDate,
    LoanStatus loanStatus
) {

}
