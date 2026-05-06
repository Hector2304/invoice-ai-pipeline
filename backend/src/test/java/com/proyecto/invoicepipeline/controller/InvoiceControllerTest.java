package com.proyecto.invoicepipeline.controller;

import com.proyecto.invoicepipeline.exception.InvalidFileTypeException;
import com.proyecto.invoicepipeline.model.Invoice;
import com.proyecto.invoicepipeline.model.InvoiceStatus;
import com.proyecto.invoicepipeline.service.InvoiceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @Test
    void uploadInvoice_validFile_returns201() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setFileName("invoice.pdf");
        invoice.setStatus(InvoiceStatus.COMPLETED);

        when(invoiceService.processUpload(any())).thenReturn(invoice);

        MockMultipartFile file = new MockMultipartFile(
                "file", "invoice.pdf", "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/invoices").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("invoice.pdf"));
    }

    @Test
    void uploadInvoice_unsupportedType_returns415() throws Exception {
        when(invoiceService.processUpload(any()))
                .thenThrow(new InvalidFileTypeException("File type not allowed. Accepted: PDF, JPG, PNG"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.docx", "application/msword", "content".getBytes());

        mockMvc.perform(multipart("/api/invoices").file(file))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("File type not allowed. Accepted: PDF, JPG, PNG"));
    }

    @Test
    void listInvoices_returns200WithPagedContent() throws Exception {
        when(invoiceService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getInvoice_unknownId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(invoiceService.findById(id))
                .thenThrow(new EntityNotFoundException("Invoice not found: " + id));

        mockMvc.perform(get("/api/invoices/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}