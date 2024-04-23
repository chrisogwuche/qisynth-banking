package com.example.qisynthbanking.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDetailDto {
    private String from;
    private String to;
    private String amount;
    private String user_desc;
    private String payment_desc;
}
