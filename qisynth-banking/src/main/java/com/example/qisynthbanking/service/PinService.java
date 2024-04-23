package com.example.qisynthbanking.service;


import com.example.qisynthbanking.dto.request.PinDto;
import com.example.qisynthbanking.dto.response.ResponseDto;
import com.example.qisynthbanking.model.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface PinService {
    ResponseEntity<ResponseDto> createTxPin(PinDto pinDto);
    boolean validatePin(String pin, String encodedPin);
}
