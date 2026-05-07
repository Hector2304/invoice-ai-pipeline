package com.proyecto.invoicepipeline.service;

import com.proyecto.invoicepipeline.dto.GeminiExtractionResult;
import com.proyecto.invoicepipeline.exception.DuplicateInvoiceException;
import com.proyecto.invoicepipeline.exception.InvalidFileTypeException;
import com.proyecto.invoicepipeline.model.Invoice;
import com.proyecto.invoicepipeline.model.InvoiceLineItem;
import com.proyecto.invoicepipeline.model.InvoiceStatus;
import com.proyecto.invoicepipeline.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private static final Map<String, String> ALLOWED_MIME_TYPES = Map.of(
            "application/pdf", "application/pdf",
            "image/jpeg", "image/jpeg",
            "image/png", "image/png"
    );

    private final InvoiceRepository invoiceRepository;
    private final GeminiService geminiService;

    public Invoice processUpload(MultipartFile file) {
        validateFileType(file);

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Could not read file bytes", e);
        }

        String fileHash = computeHash(fileBytes);
        Optional<Invoice> existingOpt = invoiceRepository.findByFileHash(fileHash);

        Invoice invoice;
        if (existingOpt.isPresent()) {
            Invoice existing = existingOpt.get();
            if (existing.getStatus() != InvoiceStatus.FAILED) {
                throw new DuplicateInvoiceException(existing.getId());
            }
            invoice = existing;
            invoice.getLineItems().clear();
            invoice.setVendor(null);
            invoice.setTotalAmount(null);
            invoice.setCurrency(null);
            invoice.setInvoiceDate(null);
        } else {
            invoice = new Invoice();
            invoice.setFileName(file.getOriginalFilename());
            invoice.setFileHash(fileHash);
        }
        invoice.setStatus(InvoiceStatus.PROCESSING);
        invoiceRepository.save(invoice);

        try {
            String mimeType = ALLOWED_MIME_TYPES.get(file.getContentType());
            GeminiExtractionResult extraction = geminiService.extractInvoiceData(fileBytes, mimeType);

            if (extraction == null) {
                invoice.setStatus(InvoiceStatus.FAILED);
            } else if (!extraction.isInvoice() || hasNoInvoiceData(extraction)) {
                invoice.setStatus(InvoiceStatus.NOT_AN_INVOICE);
            } else {
                applyExtraction(invoice, extraction);
                invoice.setStatus(InvoiceStatus.COMPLETED);
            }
        } catch (Exception e) {
            log.error("Failed to process invoice file {}: {}", file.getOriginalFilename(), e.getMessage());
            invoice.setStatus(InvoiceStatus.FAILED);
        }

        return invoiceRepository.save(invoice);
    }

    public Page<Invoice> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    public Invoice findById(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Invoice not found: " + id));
    }

    public void softDelete(UUID id) {
        Invoice invoice = findById(id);
        invoice.setDeletedAt(Instant.now());
        invoice.setFileHash(null);
        invoiceRepository.save(invoice);
    }

    private String computeHash(byte[] bytes) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private boolean hasNoInvoiceData(GeminiExtractionResult extraction) {
        return extraction.getVendor() == null
                && extraction.getTotalAmount() == null
                && extraction.getCurrency() == null
                && (extraction.getLineItems() == null || extraction.getLineItems().isEmpty());
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.containsKey(contentType)) {
            throw new InvalidFileTypeException("File type not allowed. Accepted: PDF, JPG, PNG");
        }
    }

    private void applyExtraction(Invoice invoice, GeminiExtractionResult extraction) {
        invoice.setVendor(extraction.getVendor());
        invoice.setTotalAmount(extraction.getTotalAmount());
        invoice.setCurrency(extraction.getCurrency());

        if (extraction.getInvoiceDate() != null) {
            try {
                invoice.setInvoiceDate(LocalDate.parse(extraction.getInvoiceDate()));
            } catch (Exception e) {
                log.warn("Could not parse invoice date: {}", extraction.getInvoiceDate());
            }
        }

        if (extraction.getLineItems() != null) {
            List<InvoiceLineItem> lineItems = extraction.getLineItems().stream()
                    .map(item -> {
                        InvoiceLineItem lineItem = new InvoiceLineItem();
                        lineItem.setInvoice(invoice);
                        lineItem.setDescription(item.getDescription());
                        lineItem.setQuantity(item.getQuantity());
                        lineItem.setUnitPrice(item.getUnitPrice());
                        lineItem.setTotalPrice(item.getTotalPrice());
                        return lineItem;
                    })
                    .toList();
            invoice.getLineItems().addAll(lineItems);
        }
    }
}
