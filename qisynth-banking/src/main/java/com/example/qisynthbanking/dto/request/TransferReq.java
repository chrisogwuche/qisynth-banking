package com.example.qisynthbanking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferReq {

    @NotBlank(message = "wallet_id must not be empty")
    private String account_no;
    @NotBlank(message = "amount must not be empty")
    @Pattern(regexp = "\\d+", message = "amount must be a digit")
    private String amount;
    @NotBlank(message = "description must not be empty")
    private String description;
    @Pattern(regexp = "\\d{4}", message = "tx_pin must be 4 digits")
    private String pin;
}
