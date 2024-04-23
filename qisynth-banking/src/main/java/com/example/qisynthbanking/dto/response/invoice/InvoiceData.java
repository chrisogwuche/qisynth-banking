package com.example.qisynthbanking.dto.response.invoice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class InvoiceData {

       private String reference_no;
       private String amount;
       private String currency;
       private String payment_status;
       private String payment_type;
       private String transaction_purpose;
       private String description;
       private String created_At;

}
