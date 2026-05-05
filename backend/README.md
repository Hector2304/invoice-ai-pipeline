# Backend — Invoice Processing Pipeline

REST API that receives invoices or receipts (PDF/image), extracts structured data automatically using Gemini Vision API, and persists it in PostgreSQL.

## Stack

- Java 21 + Spring Boot 3
- Gemini API (gemini-2.5-flash) with vision
- PostgreSQL + Spring Data JPA
- Maven

## Requirements

- Java 21
- PostgreSQL running locally
- Gemini API key ([Google AI Studio](https://aistudio.google.com))

## Setup

1. Create the database:
   ```sql
   CREATE DATABASE invoice_db;
   ```

2. Copy the environment file and fill in your values:
   ```bash
   cp .env.example .env
   ```

3. Edit `.env`:
   ```
   GEMINI_API_KEY=your_key_here
   DB_USERNAME=postgres
   DB_PASSWORD=your_password
   ```

4. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```

The app starts on `http://localhost:8080`. Hibernate creates the tables automatically on first run.

## API Endpoints

### Upload invoice
```
POST /api/invoices
Content-Type: multipart/form-data

file: <PDF, JPG or PNG — max 10MB>
```

Response `201 Created`:
```json
{
  "id": "uuid",
  "fileName": "invoice.pdf",
  "status": "COMPLETED"
}
```

### List invoices
```
GET /api/invoices?page=0&size=20
```

### Get invoice detail
```
GET /api/invoices/{id}
```

Response includes full extracted data and line items.

## Invoice status

| Status | Description |
|---|---|
| `PROCESSING` | File received, extraction in progress |
| `COMPLETED` | Data extracted and persisted successfully |
| `FAILED` | Gemini extraction failed — record saved without data |

## Accepted file types

`application/pdf`, `image/jpeg`, `image/png`

## Error responses

All errors follow the same format:
```json
{
  "status": 415,
  "error": "Unsupported Media Type",
  "message": "File type not allowed. Accepted: PDF, JPG, PNG",
  "timestamp": "2026-05-04T18:00:00Z"
}
```
