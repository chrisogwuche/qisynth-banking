package com.example.qisynthbanking.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PinDto {
    @Pattern(regexp = "\\d{4}", message = "tx_pin must be 4 digits")
    private String tx_pin;
}
