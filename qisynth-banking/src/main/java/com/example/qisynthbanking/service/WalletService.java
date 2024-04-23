package com.example.qisynthbanking.service;

import com.example.qisynthbanking.dto.request.CreditWalletRequest;
import com.example.qisynthbanking.dto.request.TransferReq;
import com.example.qisynthbanking.dto.response.ResponseDto;
import com.example.qisynthbanking.dto.response.WalletDto;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.model.Wallet;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface WalletService {
    ResponseEntity<WalletDto> getUserWallet();
    ResponseEntity<ResponseDto> creditWallet(CreditWalletRequest creditRequestDto);
    Wallet createWallet(Users user);
    ResponseEntity<ResponseDto> transfer(TransferReq transferReq);
}
