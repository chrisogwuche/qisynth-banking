package com.example.qisynthbanking.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TokenResponse {
    private String token;
    private String refresh_token;
    private Date issued_at;
    private Date expires_at;

}
