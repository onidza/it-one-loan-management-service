package ru.creditbank.loanmanagementservice.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.loanmanagementservice.dto.loan.UserLoansResponseDTO;
import ru.creditbank.loanmanagementservice.dto.payment.CreatePaymentRequestDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentHistoryResponseDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentResponseDTO;
import ru.creditbank.loanmanagementservice.service.LoanService;
import ru.creditbank.loanmanagementservice.service.PaymentService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/loan-management-service/api/v1/payment")
public class LoanManagementController {

  private final LoanService loanService;
  private final PaymentService paymentService;

  @GetMapping("/loans")
  public UserLoansResponseDTO getUserLoans(
      @AuthenticationPrincipal Jwt jwt
  ) {
    return loanService.getUserLoans(jwt);
  }

  @PostMapping
  public PaymentResponseDTO createPayment(
      @Valid @RequestBody CreatePaymentRequestDTO request,
      @AuthenticationPrincipal Jwt jwt
  ) {
    return paymentService.createPayment(request, jwt);
  }

  @GetMapping("/history")
  public PaymentHistoryResponseDTO getPaymentHistory(
      @RequestParam UUID loanId,
      @AuthenticationPrincipal Jwt jwt
  ) {
    return paymentService.getPaymentHistory(loanId, jwt);
  }
}