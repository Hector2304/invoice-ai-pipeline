package com.proyecto.invoicepipeline.service;

import com.proyecto.invoicepipeline.dto.GeminiExtractionResult;
import com.proyecto.invoicepipeline.exception.InvalidFileTypeException;
import com.proyecto.invoicepipeline.model.Invoice;
import com.proyecto.invoicepipeline.model.InvoiceStatus;
import com.proyecto.invoicepipeline.repository.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void processUpload_validFile_savesCompletedInvoice() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "invoice.pdf", "application/pdf", "pdf-content".getBytes());

        GeminiExtractionResult extraction = new GeminiExtractionResult();
        extraction.setVendor("Acme Corp");
        extraction.setCurrency("USD");

        when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(geminiService.extractInvoiceData(any(), any())).thenReturn(extraction);

        Invoice result = invoiceService.processUpload(file);

        assertThat(result.getStatus()).isEqualTo(InvoiceStatus.COMPLETED);
        assertThat(result.getVendor()).isEqualTo("Acme Corp");
        verify(invoiceRepository, times(2)).save(any());
    }

    @Test
    void processUpload_geminiReturnsNull_savesFailedInvoice() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "invoice.pdf", "application/pdf", "pdf-content".getBytes());

        when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(geminiService.extractInvoiceData(any(), any())).thenReturn(null);

        Invoice result = invoiceService.processUpload(file);

        assertThat(result.getStatus()).isEqualTo(InvoiceStatus.FAILED);
        verifyNoMoreInteractions(geminiService);
    }

    @Test
    void processUpload_geminiThrows_savesFailedInvoice() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "invoice.pdf", "application/pdf", "pdf-content".getBytes());

        when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(geminiService.extractInvoiceData(any(), any())).thenThrow(new RuntimeException("Gemini unavailable"));

        Invoice result = invoiceService.processUpload(file);

        assertThat(result.getStatus()).isEqualTo(InvoiceStatus.FAILED);
    }

    @Test
    void processUpload_unsupportedMimeType_throwsInvalidFileTypeException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "content".getBytes());

        assertThatThrownBy(() -> invoiceService.processUpload(file))
                .isInstanceOf(InvalidFileTypeException.class);

        verifyNoInteractions(geminiService);
    }

    @Test
    void findById_unknownId_throwsEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(invoiceRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
