package ru.creditbank.loanmanagementservice.scheduler;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleStatus;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;

@RequiredArgsConstructor
@Slf4j
@Component
public class PaymentScheduler {

  private final PaymentScheduleRepository scheduleRepository;

  @Scheduled(cron = "0 0 0 * * * ")
  @Transactional
  public void markOverduePayments() {
    int updated = scheduleRepository.markOverduePayments(
        List.of(PaymentScheduleStatus.PLANNED, PaymentScheduleStatus.PARTIALLY_PAID),
        PaymentScheduleStatus.OVERDUE,
        LocalDate.now()
    );

    log.info("Marked {} payments as overdue", updated);
  }
}