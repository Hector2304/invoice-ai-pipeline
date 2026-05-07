package com.proyecto.invoicepipeline.repository;

import com.proyecto.invoicepipeline.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByFileHash(String fileHash);
}
