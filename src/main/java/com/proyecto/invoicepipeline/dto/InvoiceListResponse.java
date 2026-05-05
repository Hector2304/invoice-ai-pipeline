package com.proyecto.invoicepipeline.dto;

import com.proyecto.invoicepipeline.model.Invoice;
import com.proyecto.invoicepipeline.model.InvoiceStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class InvoiceListResponse {

    private final UUID id;
    private final String fileName;
    private final InvoiceStatus status;
    private final String vendor;
    private final LocalDate invoiceDate;
    private final BigDecimal totalAmount;
    private final String currency;
    private final Instant createdAt;

    public InvoiceListResponse(Invoice invoice) {
        this.id = invoice.getId();
        this.fileName = invoice.getFileName();
        this.status = invoice.getStatus();
        this.vendor = invoice.getVendor();
        this.invoiceDate = invoice.getInvoiceDate();
        this.totalAmount = invoice.getTotalAmount();
        this.currency = invoice.getCurrency();
        this.createdAt = invoice.getCreatedAt();
    }
}