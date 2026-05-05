# Invoice Processing Pipeline

Full-stack application that receives invoices or receipts (PDF/image), extracts structured data automatically using Gemini Vision API, and persists it in PostgreSQL.

## Structure

```
invoice-pipeline/
├── backend/    — REST API (Java 21 + Spring Boot 3)
└── frontend/   — Web UI (Vue 3 + Vite)
```

## Stack

**Backend**
- Java 21 + Spring Boot 3
- Gemini API (gemini-2.5-flash) with vision
- PostgreSQL + Spring Data JPA
- Maven

**Frontend**
- Vue 3 + Vite

## Quick start

See setup instructions in each folder:
- [backend/README.md](backend/README.md)
- [frontend/README.md](frontend/README.md)