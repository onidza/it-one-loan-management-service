package ru.creditbank.loanmanagementservice.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanRequestDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanResponseDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateScheduleResponseDTO;
import ru.creditbank.loanmanagementservice.service.LoanService;
import ru.creditbank.loanmanagementservice.service.ScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-management-service/internal/api/v1/loans")
@Slf4j
public class InternalLoanController {

  private final LoanService loanService;
  private final ScheduleService scheduleService;

  @PostMapping
  public CreateLoanResponseDTO createLoan(
      @Valid @RequestBody CreateLoanRequestDTO request
  ) {
    return loanService.createLoan(request);
  }

  @PostMapping("/{loanId}/schedule")
  public CreateScheduleResponseDTO createSchedule(
      @PathVariable UUID loanId
  ) {
    return scheduleService.createSchedule(loanId);
  }
}
