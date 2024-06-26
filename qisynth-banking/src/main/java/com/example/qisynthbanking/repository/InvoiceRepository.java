package com.example.qisynthbanking.repository;

import com.example.qisynthbanking.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    Optional<Invoice> findByReferenceNo(String referenceNo);
}
