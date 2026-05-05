<template>
  <div class="page">
    <RouterLink to="/" class="back-link">← Volver a facturas</RouterLink>

    <div v-if="store.loading" class="loading">Cargando...</div>
    <div v-else-if="store.error" class="alert-error">{{ store.error }}</div>

    <template v-else-if="invoice">
      <div class="card">
        <div class="card-header">
          <div>
            <h1>{{ invoice.vendor ?? 'Sin proveedor' }}</h1>
            <span class="filename">{{ invoice.fileName }}</span>
          </div>
          <span :class="['badge', invoice.status.toLowerCase()]">{{ invoice.status }}</span>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <label>Fecha de factura</label>
            <span>{{ invoice.invoiceDate ?? '—' }}</span>
          </div>
          <div class="info-item">
            <label>Total</label>
            <span>{{ invoice.totalAmount != null ? `${invoice.currency} ${invoice.totalAmount}` : '—' }}</span>
          </div>
          <div class="info-item">
            <label>Subida el</label>
            <span>{{ formatDate(invoice.createdAt) }}</span>
          </div>
          <div class="info-item">
            <label>ID</label>
            <span class="mono">{{ invoice.id }}</span>
          </div>
        </div>
      </div>

      <div class="card" v-if="invoice.lineItems?.length">
        <h2>Líneas de detalle</h2>
        <table class="table">
          <thead>
            <tr>
              <th>Descripción</th>
              <th>Cantidad</th>
              <th>Precio unitario</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in invoice.lineItems" :key="item.id">
              <td>{{ item.description }}</td>
              <td>{{ item.quantity }}</td>
              <td>{{ item.unitPrice }}</td>
              <td>{{ item.totalPrice }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card empty-items" v-else>
        Sin líneas de detalle
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useInvoiceStore } from '../stores/invoiceStore'

const route = useRoute()
const store = useInvoiceStore()
const invoice = ref(null)

onMounted(async () => {
  invoice.value = await store.fetchInvoice(route.params.id)
})

function formatDate(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('es-ES', { dateStyle: 'medium', timeStyle: 'short' })
}
</script>

<style scoped>
.page { max-width: 800px; margin: 0 auto; padding: 2rem; }

.back-link { color: #4f46e5; text-decoration: none; font-size: 0.9rem; display: inline-block; margin-bottom: 1.5rem; }
.back-link:hover { text-decoration: underline; }

.card { background: white; border: 1px solid #e5e7eb; border-radius: 10px; padding: 1.5rem; margin-bottom: 1.5rem; }
.card h2 { margin: 0 0 1rem; font-size: 1rem; color: #374151; }

.card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
.card-header h1 { margin: 0 0 0.25rem; font-size: 1.4rem; }
.filename { color: #6b7280; font-size: 0.85rem; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.info-item { display: flex; flex-direction: column; gap: 0.25rem; }
.info-item label { font-size: 0.75rem; text-transform: uppercase; color: #9ca3af; font-weight: 600; }
.info-item span { font-size: 0.95rem; color: #111827; }
.mono { font-family: monospace; font-size: 0.8rem !important; word-break: break-all; }

.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 0.65rem 0.75rem; text-align: left; border-bottom: 1px solid #e5e7eb; font-size: 0.9rem; }
.table th { font-weight: 600; color: #6b7280; font-size: 0.8rem; text-transform: uppercase; }

.badge { display: inline-block; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.8rem; font-weight: 600; }
.badge.processed { background: #d1fae5; color: #065f46; }
.badge.failed { background: #fee2e2; color: #991b1b; }
.badge.pending { background: #fef3c7; color: #92400e; }

.loading { text-align: center; padding: 3rem; color: #6b7280; }
.alert-error { background: #fee2e2; color: #991b1b; padding: 0.75rem 1rem; border-radius: 6px; }
.empty-items { color: #9ca3af; text-align: center; }
</style>
