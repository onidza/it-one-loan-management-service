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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "payment_schedule",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_schedule_loan_installment",
            columnNames = {"loan_id", "installment_number"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentScheduleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "loan_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_schedule_loan")
  )
  private LoanEntity loan;

  @Column(name = "installment_number", nullable = false)
  private Integer installmentNumber;

  @Column(name = "payment_date", nullable = false)
  private LocalDate paymentDate;

  @Column(
      name = "principal_amount",
      precision = 15,
      scale = 2,
      nullable = false
  )
  private BigDecimal principalAmount;

  @Column(
      name = "interest_amount",
      precision = 15,
      scale = 2,
      nullable = false
  )
  private BigDecimal interestAmount;

  @Column(
      name = "total_amount",
      precision = 15,
      scale = 2,
      nullable = false
  )
  private BigDecimal totalAmount;

  @Column(
      name = "remaining_principal",
      precision = 15,
      scale = 2,
      nullable = false
  )
  private BigDecimal remainingPrincipal;

  @Column(
      name = "paid_amount",
      precision = 15,
      scale = 2,
      nullable = false
  )
  private BigDecimal paidAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentScheduleStatus status;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Version
  private Long version;

  public PaymentScheduleEntity(
      LoanEntity loan,
      Integer installmentNumber,
      LocalDate paymentDate,
      BigDecimal principalAmount,
      BigDecimal interestAmount,
      BigDecimal totalAmount,
      BigDecimal remainingPrincipal
  ) {
    this.loan = loan;
    this.installmentNumber = installmentNumber;
    this.paymentDate = paymentDate;
    this.principalAmount = principalAmount;
    this.interestAmount = interestAmount;
    this.totalAmount = totalAmount;
    this.remainingPrincipal = remainingPrincipal;
    this.paidAmount = BigDecimal.ZERO;
    this.status = PaymentScheduleStatus.PLANNED;
  }

  public void markPaid() {
    this.status = PaymentScheduleStatus.PAID;
    this.paidAmount = this.totalAmount;
  }

  public BigDecimal processPartialPayment(
      BigDecimal currentAmount
  ) {
    BigDecimal principalPaidBefore =
        this.paidAmount
            .subtract(this.interestAmount)
            .max(BigDecimal.ZERO);

    this.paidAmount =
        this.paidAmount.add(currentAmount);

    BigDecimal principalPaidAfter =
        this.paidAmount
            .subtract(this.interestAmount)
            .max(BigDecimal.ZERO);

    this.status = PaymentScheduleStatus.PARTIALLY_PAID;

    return principalPaidAfter.subtract(principalPaidBefore);
  }

  public void recalculate(
      BigDecimal principalAmount,
      BigDecimal interestAmount,
      BigDecimal totalAmount,
      BigDecimal remainingPrincipal
  ) {
    this.principalAmount = principalAmount;
    this.interestAmount = interestAmount;
    this.totalAmount = totalAmount;
    this.remainingPrincipal = remainingPrincipal;
  }

  public void cancel() {
    this.status = PaymentScheduleStatus.CANCELLED;
  }
}