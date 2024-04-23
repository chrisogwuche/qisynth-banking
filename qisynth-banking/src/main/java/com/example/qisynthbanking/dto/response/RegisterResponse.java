package com.example.qisynthbanking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {
    private String status;
    private String message;
    private TokenResponse data;
}
