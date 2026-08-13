package ru.creditbank.loanmanagementservice.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.loanmanagementservice.dto.payment.CreatePaymentRequestDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentHistoryItemDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentHistoryResponseDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentProcessingResultDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentResponseDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;
import ru.creditbank.loanmanagementservice.entity.PaymentEntity;
import ru.creditbank.loanmanagementservice.exception.InvalidLoanStatePaymentException;
import ru.creditbank.loanmanagementservice.exception.LoanAccessDeniedException;
import ru.creditbank.loanmanagementservice.exception.LoanNotFoundException;
import ru.creditbank.loanmanagementservice.exception.PaymentsNotFoundException;
import ru.creditbank.loanmanagementservice.repository.LoanRepository;
import ru.creditbank.loanmanagementservice.repository.PaymentRepository;
import ru.creditbank.loanmanagementservice.strategy.PaymentStrategy;
import ru.creditbank.loanmanagementservice.strategy.PaymentStrategyResolver;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final LoanRepository loanRepository;
  private final PaymentStrategyResolver paymentStrategyResolver;

  @Transactional(rollbackFor = Exception.class)
  public PaymentResponseDTO createPayment(
      CreatePaymentRequestDTO request,
      Jwt jwt
  ) {
    LoanEntity loan = loanRepository.findById(request.loanId())
        .orElseThrow(() -> new LoanNotFoundException(request.loanId()));

    UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
    if (!userId.equals(loan.getUserId())) {
      throw new LoanAccessDeniedException();
    }

    if (loan.getStatus() != LoanStatus.ACTIVE) {
      throw new InvalidLoanStatePaymentException();
    }

    PaymentStrategy strategy =
        paymentStrategyResolver.resolve(request.paymentType());

    PaymentProcessingResultDTO result =
        strategy.process(
            loan,
            request.amount()
        );

    PaymentEntity payment = paymentRepository.save(
        new PaymentEntity(
            loan,
            request.amount(),
            request.paymentType(),
            result.newBalance()
        )
    );

    return new PaymentResponseDTO(
        payment.getId(),
        result.newBalance(),
        result.nextPaymentDate()
    );
  }

  @Transactional(readOnly = true)
  public PaymentHistoryResponseDTO getPaymentHistory(
      UUID loanId,
      Jwt jwt
  ) {
    LoanEntity loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new LoanNotFoundException(loanId));

    UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
    if (!userId.equals(loan.getUserId())) {
      throw new LoanAccessDeniedException();
    }

    List<PaymentHistoryItemDTO> payments =
        paymentRepository.findAllByLoanIdOrderByPaymentDateDesc(loanId)
            .stream()
            .map(payment -> new PaymentHistoryItemDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentType(),
                payment.getBalanceAfter()
            ))
            .toList();

    if (payments.isEmpty()) {
      throw new PaymentsNotFoundException(loanId);
    }

    return new PaymentHistoryResponseDTO(payments);
  }
}