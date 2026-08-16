package ru.creditbank.loanmanagementservice.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.loanmanagementservice.dto.internal.CreditProfileDTO;
import ru.creditbank.loanmanagementservice.service.CreditProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-management-service/internal/api/v1/credit-profile")
@Slf4j
public class InternalCreditProfileController {

  private final CreditProfileService creditProfileService;

  @GetMapping("/user/{userId}")
  public CreditProfileDTO getCreditProfile(
      @PathVariable UUID userId
  ) {
    log.info("Receive credit profile request, user id = {}", userId);

    return creditProfileService.getCreditProfile(userId);
  }
}
