package com.proyecto.invoicepipeline.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiExtractionResult {

    private boolean isInvoice = true;
    private String vendor;
    private String invoiceDate;
    private BigDecimal totalAmount;
    private String currency;
    private List<LineItemResult> lineItems;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LineItemResult {
        private String description;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
