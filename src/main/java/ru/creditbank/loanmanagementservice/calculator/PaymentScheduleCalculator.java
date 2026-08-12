package ru.creditbank.loanmanagementservice.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;
import ru.creditbank.loanmanagementservice.dto.calculate.SchedulePayment;

@Component
public class PaymentScheduleCalculator {

  private static final MathContext MC = MathContext.DECIMAL128;
  private static final int MONEY_SCALE = 2;

  public SchedulePayment calculate(
      BigDecimal principal,
      BigDecimal annualInterestRate,
      int termMonths,
      int installmentNumber,
      BigDecimal remainingPrincipal
  ) {
    BigDecimal monthlyRate = annualInterestRate
        .divide(BigDecimal.valueOf(100), MC)
        .divide(BigDecimal.valueOf(12), MC);

    BigDecimal monthlyPayment = calculateAnnuityPayment(
        principal,
        monthlyRate,
        termMonths
    );

    BigDecimal interestAmount = remainingPrincipal
        .multiply(monthlyRate, MC)
        .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

    BigDecimal principalAmount;
    BigDecimal totalAmount;

    if (installmentNumber == termMonths) {
      principalAmount = remainingPrincipal;

      totalAmount = principalAmount
          .add(interestAmount)
          .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    } else {
      principalAmount = monthlyPayment
          .subtract(interestAmount)
          .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

      totalAmount = monthlyPayment;
    }

    BigDecimal newRemainingPrincipal = remainingPrincipal
        .subtract(principalAmount)
        .max(BigDecimal.ZERO)
        .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

    return new SchedulePayment(
        principalAmount,
        interestAmount,
        totalAmount,
        newRemainingPrincipal
    );
  }

  private BigDecimal calculateAnnuityPayment(
      BigDecimal principal,
      BigDecimal monthlyRate,
      int termMonths
  ) {
    BigDecimal factor = BigDecimal.ONE
        .add(monthlyRate)
        .pow(termMonths, MC);

    BigDecimal numerator = principal
        .multiply(monthlyRate, MC)
        .multiply(factor, MC);

    BigDecimal denominator =
        factor.subtract(BigDecimal.ONE);

    return numerator
        .divide(denominator, MC)
        .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
  }
}