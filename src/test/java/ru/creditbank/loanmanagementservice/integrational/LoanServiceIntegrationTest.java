package ru.creditbank.loanmanagementservice.integrational;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.loanmanagementservice.config.TestcontainersConfiguration;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanRequestDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanResponseDTO;
import ru.creditbank.loanmanagementservice.dto.loan.UserLoansResponseDTO;
import ru.creditbank.loanmanagementservice.dto.payment.CreatePaymentRequestDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentHistoryResponseDTO;
import ru.creditbank.loanmanagementservice.dto.payment.PaymentResponseDTO;
import ru.creditbank.loanmanagementservice.entity.LoanEntity;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;
import ru.creditbank.loanmanagementservice.entity.PaymentScheduleEntity;
import ru.creditbank.loanmanagementservice.entity.PaymentType;
import ru.creditbank.loanmanagementservice.exception.InvalidPaymentScheduleEarlyAmountException;
import ru.creditbank.loanmanagementservice.repository.LoanRepository;
import ru.creditbank.loanmanagementservice.repository.PaymentRepository;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;
import ru.creditbank.loanmanagementservice.service.LoanService;
import ru.creditbank.loanmanagementservice.service.PaymentScheduleService;
import ru.creditbank.loanmanagementservice.service.PaymentService;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class LoanServiceIntegrationTest {

  @Autowired
  private LoanService loanService;

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private PaymentScheduleService paymentScheduleService;

  @Autowired
  private LoanRepository loanRepository;

  @Autowired
  private PaymentScheduleRepository scheduleRepository;

  @Autowired
  private PaymentRepository paymentRepository;

  @Test
  void shouldCreateLoanAndSaveItToDatabase() {
    UUID applicationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    CreateLoanRequestDTO request =
        new CreateLoanRequestDTO(
            applicationId,
            userId,
            new BigDecimal("50000.00"),
            12,
            new BigDecimal("12.30")
        );

    CreateLoanResponseDTO response =
        loanService.createLoan(request);

    assertThat(response.loanId()).isNotNull();
    assertThat(response.status())
        .isEqualTo(LoanStatus.CREATING);

    LoanEntity savedLoan =
        loanRepository.findById(response.loanId())
            .orElseThrow();

    assertThat(savedLoan.getCreditApplicationId())
        .isEqualTo(applicationId);
    assertThat(savedLoan.getUserId())
        .isEqualTo(userId);
    assertThat(savedLoan.getTotalAmount())
        .isEqualByComparingTo("50000.00");
    assertThat(savedLoan.getRemainingAmount())
        .isEqualByComparingTo("50000.00");
    assertThat(savedLoan.getTermMonths())
        .isEqualTo(12);
    assertThat(savedLoan.getInterestRate())
        .isEqualByComparingTo("12.30");
    assertThat(savedLoan.getStatus())
        .isEqualTo(LoanStatus.CREATING);

    Jwt jwt = createJwt(userId);

    UserLoansResponseDTO userLoans =
        loanService.getUserLoans(jwt);

    assertThat(userLoans.loans())
        .hasSize(1);

    assertThat(userLoans.loans().get(0).loanId())
        .isEqualTo(response.loanId());
  }

  @Test
  void shouldCreateLoanAndSaveToDatabase() {
    UUID applicationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    CreateLoanRequestDTO request =
        new CreateLoanRequestDTO(
            applicationId,
            userId,
            new BigDecimal("50000.00"),
            12,
            new BigDecimal("12.30")
        );

    CreateLoanResponseDTO response =
        loanService.createLoan(request);

    assertThat(response.loanId()).isNotNull();
    assertThat(response.status())
        .isEqualTo(LoanStatus.CREATING);

    LoanEntity savedLoan =
        loanRepository.findById(response.loanId())
            .orElseThrow();

    assertThat(savedLoan.getCreditApplicationId())
        .isEqualTo(applicationId);

    assertThat(savedLoan.getUserId())
        .isEqualTo(userId);

    assertThat(savedLoan.getTotalAmount())
        .isEqualByComparingTo("50000.00");

    assertThat(savedLoan.getRemainingAmount())
        .isEqualByComparingTo("50000.00");

    assertThat(savedLoan.getTermMonths())
        .isEqualTo(12);

    assertThat(savedLoan.getInterestRate())
        .isEqualByComparingTo("12.30");

    assertThat(savedLoan.getStatus())
        .isEqualTo(LoanStatus.CREATING);
  }

  @Test
  void shouldCreatePaymentAndReturnPaymentHistory() {
    UUID applicationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    CreateLoanRequestDTO loanRequest =
        new CreateLoanRequestDTO(
            applicationId,
            userId,
            new BigDecimal("50000.00"),
            12,
            new BigDecimal("12.30")
        );

    CreateLoanResponseDTO createdLoan =
        loanService.createLoan(loanRequest);

    paymentScheduleService.createSchedule(
        createdLoan.loanId()
    );

    PaymentScheduleEntity firstInstallment =
        scheduleRepository
            .findByLoanIdAndInstallmentNumber(
                createdLoan.loanId(),
                1
            )
            .orElseThrow();

    Jwt jwt = createJwt(userId);

    CreatePaymentRequestDTO paymentRequest =
        new CreatePaymentRequestDTO(
            createdLoan.loanId(),
            firstInstallment.getTotalAmount(),
            PaymentType.FULL
        );

    PaymentResponseDTO paymentResponse =
        paymentService.createPayment(
            paymentRequest,
            jwt
        );

    PaymentHistoryResponseDTO history =
        paymentService.getPaymentHistory(
            createdLoan.loanId(),
            jwt
        );

    assertThat(paymentResponse.paymentId())
        .isNotNull();

    assertThat(history.payments())
        .hasSize(1);

    var payment = history.payments().get(0);

    assertThat(payment.paymentId())
        .isEqualTo(paymentResponse.paymentId());

    assertThat(payment.amount())
        .isEqualByComparingTo(
            firstInstallment.getTotalAmount()
        );

    assertThat(payment.paymentType())
        .isEqualTo(PaymentType.FULL);

    assertThat(payment.newBalance())
        .isEqualByComparingTo(
            firstInstallment.getRemainingPrincipal()
        );
  }

  @Test
  void shouldFailWhenPaymentAmountExceedsRemainingLoanAmount() {
    UUID applicationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    CreateLoanRequestDTO loanRequest =
        new CreateLoanRequestDTO(
            applicationId,
            userId,
            new BigDecimal("50000.00"),
            12,
            new BigDecimal("12.30")
        );

    CreateLoanResponseDTO createdLoan =
        loanService.createLoan(loanRequest);

    paymentScheduleService.createSchedule(createdLoan.loanId());

    Jwt jwt = createJwt(userId);

    CreatePaymentRequestDTO paymentRequest =
        new CreatePaymentRequestDTO(
            createdLoan.loanId(),
            new BigDecimal("50001.00"),
            PaymentType.EARLY
        );

    assertThatThrownBy(() ->
        paymentService.createPayment(paymentRequest, jwt)
    )
        .isInstanceOf(
            InvalidPaymentScheduleEarlyAmountException.class
        );

    LoanEntity loan =
        loanRepository.findById(createdLoan.loanId())
            .orElseThrow();

    assertThat(loan.getRemainingAmount())
        .isEqualByComparingTo("50000.00");

    assertThat(loan.getStatus())
        .isEqualTo(LoanStatus.ACTIVE);

    assertThat(
        paymentRepository.findAllByLoanIdOrderByPaymentDateDesc(
            createdLoan.loanId()
        )
    ).isEmpty();
  }

  private Jwt createJwt(UUID userId) {
    Instant now = Instant.now();

    return Jwt.withTokenValue("test-token")
        .header("alg", "HS256")
        .subject("test@mail.ru")
        .claim("user_id", userId.toString())
        .issuedAt(now)
        .expiresAt(now.plusSeconds(900))
        .build();
  }
}
