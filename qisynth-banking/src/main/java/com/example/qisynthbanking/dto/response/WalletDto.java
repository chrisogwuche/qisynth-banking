package com.example.qisynthbanking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WalletDto {
    private String account_no;
    private String balance;

}
