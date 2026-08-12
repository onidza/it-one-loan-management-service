package ru.creditbank.loanmanagementservice.strategy;

import java.math.BigDecimal;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentProcessingResultDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentType;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;

public interface PaymentStrategy {

  PaymentType getType();

  PaymentProcessingResultDTO process(LoanEntity loan, BigDecimal amount);
}
