package ru.creditbank.loanmanagementservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/loan-management-service/api/v1/payment")
public class LoanManagementController {
//
//  private final LoanService loanService;
//  private final PaymentService paymentService;
//
//  @GetMapping("/loans")
//  public UserLoansResponseDTO getUserLoans(
//      @AuthenticationPrincipal Jwt jwt
//  ) {
//    return loanService.getUserLoans(jwt);
//  }
//
//  @PostMapping
//  public PaymentResponseDTO createPayment(
//      @Valid @RequestBody CreatePaymentRequestDTO request,
//      @AuthenticationPrincipal Jwt jwt
//  ) {
//    return paymentService.createPayment(request, jwt);
//  }
//
//  @GetMapping("/history")
//  public PaymentHistoryResponseDTO getPaymentHistory(
//      @RequestParam UUID loanId,
//      @AuthenticationPrincipal Jwt jwt
//  ) {
//    return paymentService.getPaymentHistory(loanId, jwt);
//  }

}
