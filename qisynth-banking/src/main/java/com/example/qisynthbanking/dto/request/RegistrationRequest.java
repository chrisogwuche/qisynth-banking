package com.example.qisynthbanking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    @NotBlank(message = "first_name must not be blank")
    private String first_name;
    @NotBlank(message = "last_name must not be blank")
    private String last_name;
    private String other_name;
    @Email(message = "email must be a valid email")
    private String email;
    @NotBlank(message = "phone_number must not be blank")
    @Pattern(regexp = "\\d+", message = "phone_number must be a digit")
    private String phone_number;
    @NotBlank(message = "password must not be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])" + "(?=.*[-+_!@#$%^&*., ?]).+$"
            , message = "password must contain at least one Uppercase and a special character")
    private String password;
    @NotBlank(message = "dob must not be blank")
    private String dob;
    @NotBlank(message = "country must not be blank")
    private String country;
}
