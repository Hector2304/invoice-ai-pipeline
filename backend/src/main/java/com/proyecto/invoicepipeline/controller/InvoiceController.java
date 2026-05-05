package com.proyecto.invoicepipeline.controller;

import com.proyecto.invoicepipeline.dto.InvoiceDetailResponse;
import com.proyecto.invoicepipeline.dto.InvoiceListResponse;
import com.proyecto.invoicepipeline.dto.InvoiceUploadResponse;
import com.proyecto.invoicepipeline.model.Invoice;
import com.proyecto.invoicepipeline.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<InvoiceUploadResponse> uploadInvoice(@RequestParam("file") MultipartFile file) {
        Invoice invoice = invoiceService.processUpload(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(new InvoiceUploadResponse(invoice));
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceListResponse>> listInvoices(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<InvoiceListResponse> page = invoiceService.findAll(pageable)
                .map(InvoiceListResponse::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDetailResponse> getInvoice(@PathVariable UUID id) {
        Invoice invoice = invoiceService.findById(id);
        return ResponseEntity.ok(new InvoiceDetailResponse(invoice));
    }
}