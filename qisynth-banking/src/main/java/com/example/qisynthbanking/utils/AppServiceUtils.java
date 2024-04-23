package com.example.qisynthbanking.utils;

import com.example.qisynthbanking.dto.response.ResponseDto;
import com.example.qisynthbanking.exceptions.NotFoundException;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.repository.UsersRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppServiceUtils {
    private final UsersRepository userRepository;


    public Users getCurrentUser(){
        UserDetails userDetail = SecurityUtils.getAuthenticatedUser(UserDetails.class);
        return findUserByEmail(userDetail.getUsername());
    }

    public static ResponseDto setResponseDto(String status, String message) {
        return ResponseDto.builder()
                .message(message)
                .status(status)
                .build();
    }

    public Users findUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new NotFoundException("user not found"));
    }

    /* IDGenerator generates random alphanumeric and digits */
    public static String IDGenerator(@NonNull Boolean isForWalletId){
        if(isForWalletId){
            Random random = new Random();
            String rand1 = String.format("%04d",random.nextInt(9999));
            String rand2 = String.format("%04d",random.nextInt(9999));
            String rand3 = String.format("%04d",random.nextInt(9999));
            return rand1+rand2+rand3;
        }
        return UUID.randomUUID().toString().replaceAll("-","").substring(12);
    }
}
