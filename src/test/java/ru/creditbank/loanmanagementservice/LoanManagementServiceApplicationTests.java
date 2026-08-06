package ru.creditbank.loanmanagementservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.creditbank.loanmanagementservice.config.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class LoanManagementServiceApplicationTests {

  @Test
  void contextLoads() {
  }

}
