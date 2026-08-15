package ru.creditbank.loanmanagementservice.strategy;

import java.math.BigDecimal;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentProcessingResultDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentType;

public interface PaymentStrategy {

  PaymentType getType();

  PaymentProcessingResultDTO process(LoanEntity loan, BigDecimal amount);
}
