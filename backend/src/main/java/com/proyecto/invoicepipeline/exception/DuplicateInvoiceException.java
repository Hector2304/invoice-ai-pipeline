package com.proyecto.invoicepipeline.exception;

import java.util.UUID;

public class DuplicateInvoiceException extends RuntimeException {

    private final UUID existingId;

    public DuplicateInvoiceException(UUID existingId) {
        super("File already processed");
        this.existingId = existingId;
    }

    public UUID getExistingId() {
        return existingId;
    }
}
