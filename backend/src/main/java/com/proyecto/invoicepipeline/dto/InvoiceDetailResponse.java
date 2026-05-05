package com.proyecto.invoicepipeline.dto;

import com.proyecto.invoicepipeline.model.Invoice;
import com.proyecto.invoicepipeline.model.InvoiceLineItem;
import com.proyecto.invoicepipeline.model.InvoiceStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
public class InvoiceDetailResponse {

    private final UUID id;
    private final String fileName;
    private final InvoiceStatus status;
    private final String vendor;
    private final LocalDate invoiceDate;
    private final BigDecimal totalAmount;
    private final String currency;
    private final Instant createdAt;
    private final List<LineItemResponse> lineItems;

    public InvoiceDetailResponse(Invoice invoice) {
        this.id = invoice.getId();
        this.fileName = invoice.getFileName();
        this.status = invoice.getStatus();
        this.vendor = invoice.getVendor();
        this.invoiceDate = invoice.getInvoiceDate();
        this.totalAmount = invoice.getTotalAmount();
        this.currency = invoice.getCurrency();
        this.createdAt = invoice.getCreatedAt();
        this.lineItems = invoice.getLineItems().stream()
                .map(LineItemResponse::new)
                .toList();
    }

    @Getter
    public static class LineItemResponse {
        private final UUID id;
        private final String description;
        private final Integer quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal totalPrice;

        public LineItemResponse(InvoiceLineItem item) {
            this.id = item.getId();
            this.description = item.getDescription();
            this.quantity = item.getQuantity();
            this.unitPrice = item.getUnitPrice();
            this.totalPrice = item.getTotalPrice();
        }
    }
}
