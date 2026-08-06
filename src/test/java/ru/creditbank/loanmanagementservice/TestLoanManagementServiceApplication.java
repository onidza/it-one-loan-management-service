package ru.creditbank.loanmanagementservice;

import org.springframework.boot.SpringApplication;

public class TestLoanManagementServiceApplication {

  public static void main(String[] args) {
    SpringApplication.from(LoanManagementServiceApplication::main)
        .with(TestcontainersConfiguration.class).run(args);
  }

}
