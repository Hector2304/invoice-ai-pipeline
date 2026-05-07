package com.proyecto.invoicepipeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.invoicepipeline.config.GeminiConfig;
import com.proyecto.invoicepipeline.dto.GeminiExtractionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient geminiWebClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;

    private static final String EXTRACTION_PROMPT = """
            Analyze this document. If it is NOT an invoice or receipt, return ONLY: {"isInvoice": false}

            If it IS an invoice or receipt, extract the following and return ONLY a valid JSON object, no markdown, no explanation.

            JSON structure required:
            {
              "isInvoice": true,
              "vendor": "string or null",
              "invoiceDate": "YYYY-MM-DD or null",
              "totalAmount": number or null,
              "currency": "ISO 4217 code (USD, EUR, MXN, etc.) or null",
              "lineItems": [
                {
                  "description": "string",
                  "quantity": number or null,
                  "unitPrice": number or null,
                  "totalPrice": number or null
                }
              ]
            }
            """;

    public GeminiExtractionResult extractInvoiceData(byte[] fileBytes, String mimeType) {
        String base64File = Base64.getEncoder().encodeToString(fileBytes);

        Map<String, Object> requestBody = buildRequestBody(base64File, mimeType);

        String rawResponse = geminiWebClient.post()
                .uri("/models/{model}:generateContent", geminiConfig.getModel())
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class).map(body -> {
                            log.error("Gemini API error {}: {}", response.statusCode(), body);
                            return new RuntimeException("Gemini API error: " + body);
                        }))
                .bodyToMono(String.class)
                .block();

        return parseGeminiResponse(rawResponse);
    }

    private Map<String, Object> buildRequestBody(String base64File, String mimeType) {
        Map<String, Object> inlineData = Map.of(
                "mime_type", mimeType,
                "data", base64File
        );

        Map<String, Object> filePart = Map.of("inline_data", inlineData);
        Map<String, Object> textPart = Map.of("text", EXTRACTION_PROMPT);

        Map<String, Object> content = Map.of("parts", List.of(filePart, textPart));

        return Map.of("contents", List.of(content));
    }

    private GeminiExtractionResult parseGeminiResponse(String rawResponse) {
        try {
            var responseNode = objectMapper.readTree(rawResponse);
            String text = responseNode
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();

            String cleanJson = cleanJsonResponse(text);
            return objectMapper.readValue(cleanJson, GeminiExtractionResult.class);
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", e.getMessage());
            return null;
        }
    }

    private String cleanJsonResponse(String text) {
        String cleaned = text.strip();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```$", "").strip();
        }
        return cleaned;
    }
}