package com.proyecto.invoicepipeline.dto;

import com.proyecto.invoicepipeline.model.Invoice;
import com.proyecto.invoicepipeline.model.InvoiceStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InvoiceUploadResponse {

    private final UUID id;
    private final String fileName;
    private final InvoiceStatus status;

    public InvoiceUploadResponse(Invoice invoice) {
        this.id = invoice.getId();
        this.fileName = invoice.getFileName();
        this.status = invoice.getStatus();
    }
}