package com.example.qisynthbanking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDto {
    @NotBlank(message = "id must not be empty")
    private String id;
}
