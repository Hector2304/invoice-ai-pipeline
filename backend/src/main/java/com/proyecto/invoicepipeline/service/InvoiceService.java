package com.proyecto.invoicepipeline.service;

import com.proyecto.invoicepipeline.dto.GeminiExtractionResult;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

        Invoice invoice = new Invoice();
        invoice.setFileName(file.getOriginalFilename());
        invoice.setStatus(InvoiceStatus.PROCESSING);
        invoiceRepository.save(invoice);

        try {
            String mimeType = ALLOWED_MIME_TYPES.get(file.getContentType());
            GeminiExtractionResult extraction = geminiService.extractInvoiceData(file.getBytes(), mimeType);

            if (extraction == null) {
                invoice.setStatus(InvoiceStatus.FAILED);
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
