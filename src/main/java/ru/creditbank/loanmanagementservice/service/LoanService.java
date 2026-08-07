package ru.creditbank.loanmanagementservice.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanRequestDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanResponseDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateScheduleResponseDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.exception.DuplicateLoanException;
import ru.creditbank.loanmanagementservice.exception.LoanNotFoundException;
import ru.creditbank.loanmanagementservice.repository.LoanRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

  private final LoanRepository loanRepository;

  public CreateLoanResponseDTO createLoan(CreateLoanRequestDTO request) {
    if (loanRepository.existsByCreditApplicationId(request.creditApplicationId())) {
      throw new DuplicateLoanException(
          "Loan has already been submitted"
      );
    }

    LoanEntity loan = loanRepository.save(
        new LoanEntity(
            request.creditApplicationId(),
            request.userId(),
            request.totalAmount(),
            request.termMonths(),
            request.interestRate()
        )
    );

    log.info("Created a new loan for applicationId = {}",
        loan.getCreditApplicationId());

    return new CreateLoanResponseDTO(
        loan.getId(),
        loan.getStatus()
    );

  }

  public CreateScheduleResponseDTO createSchedule(UUID loanId) {
    LoanEntity loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new LoanNotFoundException(loanId));


  }
}