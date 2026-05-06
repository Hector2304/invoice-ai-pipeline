# Invoice Processing Pipeline

Full-stack application that receives invoices or receipts (PDF/image), extracts structured data automatically using Gemini Vision API, and persists it in PostgreSQL.

## Features

- Upload invoices as PDF, JPG, or PNG (max 10MB)
- Automatic data extraction via Gemini Vision: vendor, date, total amount, currency, line items
- Paginated invoice list with status tracking (`PROCESSING` / `COMPLETED` / `FAILED`)
- Invoice detail view with full extracted data and line items table
- Consistent JSON error responses with proper HTTP status codes

## Stack

**Backend**
- Java 21 + Spring Boot 3
- Gemini API (`gemini-2.5-flash`) for vision-based extraction
- PostgreSQL + Spring Data JPA
- Maven + Lombok

**Frontend**
- Vue 3 + Vite
- Pinia (state management) + Vue Router 4
- Tailwind CSS

## Structure

```
invoice-pipeline/
├── backend/    — REST API (Spring Boot)
└── frontend/   — SPA (Vue 3)
```

## Quick start

1. **Backend** — see [backend/README.md](backend/README.md)
2. **Frontend** — see [frontend/README.md](frontend/README.md)

## API overview

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/invoices` | Upload invoice (multipart/form-data) |
| `GET` | `/api/invoices` | List invoices (paginated) |
| `GET` | `/api/invoices/{id}` | Get invoice detail |