package ru.creditbank.loanmanagementservice.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanRequestDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanResponseDTO;
import ru.creditbank.loanmanagementservice.dto.loan.LoanInfoDTO;
import ru.creditbank.loanmanagementservice.dto.loan.UserLoansResponseDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.exception.DuplicateLoanException;
import ru.creditbank.loanmanagementservice.repository.LoanRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

  private final LoanRepository loanRepository;

  @Transactional(rollbackFor = Exception.class)
  public CreateLoanResponseDTO createLoan(CreateLoanRequestDTO request) {
    if (loanRepository.existsByCreditApplicationId(request.creditApplicationId())) {
      throw new DuplicateLoanException();
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

  @Transactional(readOnly = true)
  public UserLoansResponseDTO getUserLoans(Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));

    List<LoanInfoDTO> loans = loanRepository.findAllByUserId(userId)
        .stream()
        .map(loanEntity ->
            new LoanInfoDTO(
                loanEntity.getId(),
                loanEntity.getTotalAmount(),
                loanEntity.getRemainingAmount(),
                loanEntity.getNextPaymentDate(),
                loanEntity.getStatus()
            ))
        .toList();

    log.info(
        "Loans fetched for userId = {}, count = {}",
        userId,
        loans.size()
    );

    return new UserLoansResponseDTO(loans);
  }
}