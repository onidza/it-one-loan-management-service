package ru.creditbank.loanmanagementservice.integrational;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.loanmanagementservice.config.TestcontainersConfiguration;
import ru.creditbank.loanmanagementservice.dto.internal.CreateLoanResponseDTO;
import ru.creditbank.loanmanagementservice.dto.internal.CreateScheduleResponseDTO;
import ru.creditbank.loanmanagementservice.entity.LoanStatus;
import ru.creditbank.loanmanagementservice.repository.LoanRepository;
import ru.creditbank.loanmanagementservice.repository.PaymentScheduleRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestcontainersConfiguration.class)
class LoanIdempotencyIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private LoanRepository loanRepository;

  @Autowired
  private PaymentScheduleRepository scheduleRepository;

  @Test
  void shouldReturnExistingLoanForSameCreditApplication()
      throws Exception {
    UUID applicationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    String request = """
        {
          "creditApplicationId": "%s",
          "userId": "%s",
          "totalAmount": 100000.00,
          "termMonths": 12,
          "interestRate": 15.00
        }
        """.formatted(applicationId, userId);

    MvcResult firstResult =
        mockMvc.perform(post("/loan-management-service/internal/api/v1/loans")
                .with(jwt()
                    .jwt(jwt -> jwt
                        .subject("test@example.com")
                        .claim(
                            "user_id",
                            userId.toString()
                        ))
                    .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

    MvcResult secondResult =
        mockMvc.perform(post("/loan-management-service/internal/api/v1/loans")
                .with(jwt()
                    .jwt(jwt -> jwt
                        .subject("test@example.com")
                        .claim(
                            "user_id",
                            userId.toString()
                        ))
                    .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

    CreateLoanResponseDTO first =
        objectMapper.readValue(
            firstResult.getResponse().getContentAsString(),
            CreateLoanResponseDTO.class
        );

    CreateLoanResponseDTO second =
        objectMapper.readValue(
            secondResult.getResponse().getContentAsString(),
            CreateLoanResponseDTO.class
        );

    assertThat(second.loanId())
        .isEqualTo(first.loanId());

    assertThat(loanRepository.count())
        .isEqualTo(1);
  }

  @Test
  void shouldReturnExistingScheduleForRepeatedRequest() throws Exception {
    UUID applicationId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    String createLoanRequest = """
        {
          "creditApplicationId": "%s",
          "userId": "%s",
          "totalAmount": 100000.00,
          "termMonths": 12,
          "interestRate": 15.00
        }
        """.formatted(applicationId, userId);

    MvcResult loanResult =
        mockMvc.perform(post("/loan-management-service/internal/api/v1/loans")
                .with(jwt()
                    .jwt(jwt -> jwt
                        .subject("test@example.com")
                        .claim(
                            "user_id",
                            userId.toString()
                        ))
                    .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createLoanRequest))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

    CreateLoanResponseDTO loan =
        objectMapper.readValue(
            loanResult.getResponse().getContentAsString(),
            CreateLoanResponseDTO.class
        );

    MvcResult firstResult =
        mockMvc.perform(post("/loan-management-service/internal/api/v1/loans/{loanId}/schedule",
                loan.loanId())
                .with(jwt()
                    .jwt(jwt -> jwt
                        .subject("test@example.com")
                        .claim(
                            "user_id",
                            userId.toString()
                        ))
                    .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createLoanRequest))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

    MvcResult secondResult =
        mockMvc.perform(post("/loan-management-service/internal/api/v1/loans/{loanId}/schedule",
                loan.loanId())
                .with(jwt()
                    .jwt(jwt -> jwt
                        .subject("test@example.com")
                        .claim(
                            "user_id",
                            userId.toString()
                        ))
                    .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createLoanRequest))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

    CreateScheduleResponseDTO first =
        objectMapper.readValue(
            firstResult.getResponse().getContentAsString(),
            CreateScheduleResponseDTO.class
        );

    CreateScheduleResponseDTO second =
        objectMapper.readValue(
            secondResult.getResponse().getContentAsString(),
            CreateScheduleResponseDTO.class
        );

    assertThat(second.loanId())
        .isEqualTo(first.loanId());

    assertThat(second.schedulerSize())
        .isEqualTo(first.schedulerSize());

    assertThat(second.fistPaymentDate())
        .isEqualTo(first.fistPaymentDate());

    assertThat(second.loanStatus())
        .isEqualTo(first.loanStatus());

    assertThat(scheduleRepository.findAllByLoanId(loan.loanId()))
        .hasSize(12);

    assertThat(
        loanRepository.findById(loan.loanId())
            .orElseThrow()
            .getStatus()
    ).isEqualTo(LoanStatus.ACTIVE);
  }
}