package com.example.qisynthbanking.controller;

import com.example.qisynthbanking.dto.request.TransferReq;
import com.example.qisynthbanking.dto.response.ResponseDto;
import com.example.qisynthbanking.dto.request.CreditWalletRequest;
import com.example.qisynthbanking.dto.response.WalletDto;
import com.example.qisynthbanking.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payment/wallets")
public class WalletController {
    private final WalletService walletService;

    @GetMapping()
    public ResponseEntity<WalletDto> getWallet(){
        return walletService.getUserWallet();
    }

    @PostMapping(value = "/fund", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> creditWallet(@RequestBody @Valid CreditWalletRequest creditRequestDto){
        return walletService.creditWallet(creditRequestDto);
    }

    @PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> transferFund(@RequestBody @Valid TransferReq transferReq){
        return walletService.transfer(transferReq);
    }
}
