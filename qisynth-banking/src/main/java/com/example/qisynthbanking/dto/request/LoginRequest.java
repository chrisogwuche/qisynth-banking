package com.example.qisynthbanking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Email(message = "email must be in an email format")
    private String email;
    @NotBlank(message = "password must not be blank")
    private String password;
}
