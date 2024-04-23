package com.example.qisynthbanking.model;

import com.example.qisynthbanking.enums.PaymentStatus;
import com.example.qisynthbanking.enums.PaymentType;
import com.example.qisynthbanking.enums.Purpose;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String referenceNo;
    private BigDecimal amount;
    private String currency;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String description;
    @Enumerated(EnumType.STRING)
    private Purpose purpose;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    @ManyToOne
    @JsonManagedReference
    private Users user;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
}
