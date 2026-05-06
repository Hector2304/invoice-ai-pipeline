# Frontend — Invoice Processing Pipeline

Vue 3 SPA that displays and uploads invoices processed by the backend API.

## Stack

- Vue 3 + Vite
- Pinia (state management)
- Vue Router 4
- Tailwind CSS

## Requirements

- Node.js 18+
- Backend API running on `http://localhost:8080`

## Setup

```bash
npm install
npm run dev
```

The app starts on `http://localhost:5173`.

## Views

| Route | Description |
|-------|-------------|
| `/` | Paginated invoice list with upload modal and drag-and-drop |
| `/invoices/:id` | Invoice detail: extracted data and line items table |