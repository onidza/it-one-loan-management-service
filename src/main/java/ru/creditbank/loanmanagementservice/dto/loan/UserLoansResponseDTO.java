package ru.creditbank.loanmanagementservice.dto.loan;

import java.util.List;

public record UserLoansResponseDTO(
    List<LoanInfoDTO> loans
) {

}
