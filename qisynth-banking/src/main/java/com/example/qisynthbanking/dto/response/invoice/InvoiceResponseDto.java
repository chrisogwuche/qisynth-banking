package com.example.qisynthbanking.dto.response.invoice;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class InvoiceResponseDto {
    private List<InvoiceData> data;
}
