package ru.creditbank.loanmanagementservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "user_loans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanEntity {

  @Column(name = "term_months", nullable = false)
  Integer termMonths;

  @Column(
      name = "interest_rate",
      precision = 5,
      scale = 2,
      nullable = false)
  BigDecimal interestRate;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)

  private UUID id;
  @Column(
      name = "credit_application_id",
      nullable = false,
      unique = true
  )
  private UUID creditApplicationId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(
      name = "total_amount",
      precision = 15,
      scale = 2,
      nullable = false
  )
  private BigDecimal totalAmount;

  @Column(
      name = "remaining_amount",
      precision = 15,
      scale = 2,
      nullable = false
  )
  private BigDecimal remainingAmount;

  @Column(name = "next_payment_date")
  private LocalDate nextPaymentDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private LoanStatus status;

  @CreationTimestamp
  @Column(
      name = "creation_date",
      nullable = false,
      updatable = false
  )
  private Instant creationDate;

  @Column(name = "activated_at")
  private Instant activatedAt;

  @UpdateTimestamp
  @Column(name = "last_updated", nullable = false)
  private Instant lastUpdated;

  @Version
  private Long version;

  public LoanEntity(
      UUID creditApplicationId,
      UUID userId,
      BigDecimal totalAmount,
      Integer termMonths,
      BigDecimal interestRate
  ) {
    this.creditApplicationId = creditApplicationId;
    this.userId = userId;
    this.totalAmount = totalAmount;
    this.termMonths = termMonths;
    this.interestRate = interestRate;
    this.remainingAmount = this.totalAmount;
    this.status = LoanStatus.CREATING;
  }

  public void activate(
      Instant activatedAt,
      LocalDate nextPaymentDate
  ) {
    this.activatedAt = activatedAt;
    this.nextPaymentDate = nextPaymentDate;
    this.status = LoanStatus.ACTIVE;
  }
}