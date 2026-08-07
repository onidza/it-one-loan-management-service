package ru.creditbank.loanmanagementservice.dto.error;

public record ErrorResponseDTO(
    int status,
    String message
) {

}