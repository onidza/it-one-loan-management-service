package ru.creditbank.loanmanagementservice.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentType;
import ru.creditbank.loanmanagementservice.exception.UnsupportedPaymentType;

@Component
public class PaymentStrategyResolver {

  private final Map<PaymentType, PaymentStrategy> strategies;

  public PaymentStrategyResolver(List<PaymentStrategy> strategies) {
    this.strategies = strategies.stream()
        .collect(Collectors.toMap(
            PaymentStrategy::getType,
            Function.identity()
        ));
  }

  public PaymentStrategy resolve(PaymentType type) {
    PaymentStrategy strategy = strategies.get(type);

    if (strategy == null) {
      throw new UnsupportedPaymentType(type);
    }

    return strategy;
  }
}
