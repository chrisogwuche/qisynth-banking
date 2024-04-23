package com.example.qisynthbanking.controller;


import com.example.qisynthbanking.dto.request.PinDto;
import com.example.qisynthbanking.dto.response.ResponseDto;
import com.example.qisynthbanking.service.PinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/pin",consumes = MediaType.APPLICATION_JSON_VALUE)
public class PinController {

    private final PinService pinService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createTransactionPin(@Valid @RequestBody PinDto pinDto){
        return pinService.createTxPin(pinDto);
    }
}
