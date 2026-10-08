package ru.creditbank.loanmanagementservice.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.creditbank.loanmanagementservice.dto.kafka.LoanIssuanceEventDTO;

@Component
@Slf4j
public class LoanIssuanceKafkaListener {

  @KafkaListener(
      topics = "credit.events",
      groupId = "loan-management-service"
  )
  public void handle(
      LoanIssuanceEventDTO event,
      @Header("id") String eventId,
      @Header("eventType") String eventType,
      @Header(KafkaHeaders.RECEIVED_KEY) String aggregateId
  ) {
    log.info(
        "Received kafka/debezium test event: event id = {}, event type = {}, aggregate id = {}",
        eventId, eventType, aggregateId);
  }
}
