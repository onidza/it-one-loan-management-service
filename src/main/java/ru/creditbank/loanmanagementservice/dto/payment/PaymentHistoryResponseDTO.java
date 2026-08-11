package ru.creditbank.loanmanagementservice.dto.payment;

import java.util.List;

public record PaymentHistoryResponseDTO(
    List<PaymentHistoryItemDTO> payments
) {

}
