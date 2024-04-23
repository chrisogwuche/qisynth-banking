package com.example.qisynthbanking.controller;

import com.example.qisynthbanking.dto.response.invoice.InvoiceData;
import com.example.qisynthbanking.service.InvoiceService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    @GetMapping("/user")
    public ResponseEntity<Page<InvoiceData>> getUserInvoices(
            @RequestParam(value = "page-no", defaultValue = "0") int pageNo
            ,@RequestParam(value = "page-size", defaultValue = "10") int pageSize){

        return invoiceService.userInvoices(pageNo,pageSize);
    }
}
