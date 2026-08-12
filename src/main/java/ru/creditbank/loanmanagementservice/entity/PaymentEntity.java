package ru.creditbank.loanmanagementservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "loan_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_payment_loan")
  )
  private LoanEntity loan;

  @Column(
      name = "amount",
      nullable = false,
      precision = 15,
      scale = 2
  )
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(
      name = "payment_type",
      nullable = false,
      length = 20
  )
  private PaymentType paymentType;

  @CreationTimestamp
  @Column(
      name = "payment_date",
      nullable = false,
      updatable = false
  )
  private Instant paymentDate;

  @Column(
      name = "balance_after",
      nullable = false,
      precision = 15,
      scale = 2
  )
  private BigDecimal balanceAfter;

  public PaymentEntity(
      LoanEntity loan,
      BigDecimal amount,
      PaymentType paymentType,
      BigDecimal balanceAfter
  ) {
    this.loan = loan;
    this.amount = amount;
    this.paymentType = paymentType;
    this.balanceAfter = balanceAfter;
  }
}
