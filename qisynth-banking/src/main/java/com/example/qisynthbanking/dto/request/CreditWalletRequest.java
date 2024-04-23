package com.example.qisynthbanking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreditWalletRequest {

    @Pattern(regexp = "\\d+", message = "Only digit allowed")
    @NotBlank(message = "amount must not be empty")
    private String amount;
}
