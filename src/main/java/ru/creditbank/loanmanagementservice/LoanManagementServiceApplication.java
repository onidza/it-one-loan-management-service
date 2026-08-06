package ru.creditbank.loanmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LoanManagementServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(LoanManagementServiceApplication.class, args);
  }

}
