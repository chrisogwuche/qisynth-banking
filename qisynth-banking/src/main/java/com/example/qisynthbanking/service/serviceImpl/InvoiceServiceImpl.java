package com.example.qisynthbanking.service.serviceImpl;

import com.example.qisynthbanking.dto.response.invoice.InvoiceData;
import com.example.qisynthbanking.enums.PaymentStatus;
import com.example.qisynthbanking.enums.PaymentType;
import com.example.qisynthbanking.enums.Purpose;
import com.example.qisynthbanking.model.Invoice;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.service.InvoiceService;
import com.example.qisynthbanking.utils.AppServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    private static final String DATE_FORMATTER= "dd-MMMM-yyyy";
    private final AppServiceUtils appServiceUtils;


    @Override
    public ResponseEntity<Page<InvoiceData>> userInvoices(int pageNo, int pageSize){
        return  ResponseEntity.ok(getUserInvoices(appServiceUtils.getCurrentUser(),pageNo,pageSize));
    }

    @Override
    public Invoice createInvoice(Users user, BigDecimal amount, String currency, Purpose purpose
            , PaymentStatus paymentStatus,PaymentType paymentType, String description) {
        return setInvoice(user, amount, currency, purpose, paymentStatus, paymentType, description);
    }

    private Invoice setInvoice(Users user, BigDecimal amount, String currency, Purpose purpose,
                               PaymentStatus paymentStatus, PaymentType paymentType, String description){
        log.info("setInvoice::");
        Invoice invoice = new Invoice();
        invoice.setReferenceNo(AppServiceUtils.IDGenerator(false));
        invoice.setDescription(description);
        invoice.setPaymentStatus(paymentStatus);
        invoice.setAmount(amount);
        invoice.setPurpose(purpose);
        invoice.setUser(user);
        invoice.setCurrency(currency);
        invoice.setPaymentType(paymentType);
        return invoice;
    }

    private Page<InvoiceData> getUserInvoices(Users user, int pageNo, int pageSize) {
        log.info("getUserInvoices::");
        List<InvoiceData> invoices = user.getInvoiceList().stream()
                .map(this::mapToInvoiceResponseDto).toList();

        Pageable pageRequest = PageRequest.of(pageNo, pageSize);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start+pageRequest.getPageSize()),invoices.size());
        List<InvoiceData> pageContent = invoices.subList(start,end);

        return new PageImpl<>(pageContent,pageRequest,invoices.size());
    }

    private InvoiceData mapToInvoiceResponseDto(Invoice x){
        log.info("mapToInvoiceResponseDto::");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String createAt = x.getCreatedAt().atOffset(ZoneOffset.UTC).format(formatter);
        return InvoiceData.builder()
               .reference_no(x.getReferenceNo())
               .description(x.getDescription())
               .created_At(createAt)
               .payment_status(x.getPaymentStatus().name())
                .transaction_purpose(x.getPurpose().name())
                .amount(String.valueOf(x.getAmount()))
                .currency(x.getCurrency())
                .payment_type(x.getPaymentType().name())
               .build();
    }
}
