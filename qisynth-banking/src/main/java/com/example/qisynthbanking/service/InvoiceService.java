package com.example.qisynthbanking.service;


import com.example.qisynthbanking.dto.response.invoice.InvoiceData;
import com.example.qisynthbanking.enums.PaymentStatus;
import com.example.qisynthbanking.enums.PaymentType;
import com.example.qisynthbanking.enums.Purpose;
import com.example.qisynthbanking.model.Invoice;
import com.example.qisynthbanking.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface InvoiceService {

    ResponseEntity<Page<InvoiceData>> userInvoices(int pageNo, int pageSize);
    Invoice createInvoice(Users user, BigDecimal amount, String currency, Purpose purpose, PaymentStatus paymentStatus
            , PaymentType paymentType, String description);
}
