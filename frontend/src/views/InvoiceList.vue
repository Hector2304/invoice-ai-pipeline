<template>
  <div class="page">
    <header class="page-header">
      <h1>Facturas</h1>
      <button class="btn-primary" @click="showModal = true">+ Subir factura</button>
    </header>

    <div v-if="store.error" class="alert-error">{{ store.error }}</div>

    <div v-if="store.loading" class="loading">Cargando...</div>

    <table v-else class="table">
      <thead>
        <tr>
          <th>Proveedor</th>
          <th>Archivo</th>
          <th>Estado</th>
          <th>Fecha</th>
          <th>Total</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="store.invoices.length === 0">
          <td colspan="6" class="empty">No hay facturas aún</td>
        </tr>
        <tr v-for="inv in store.invoices" :key="inv.id">
          <td>{{ inv.vendor ?? '—' }}</td>
          <td>{{ inv.fileName }}</td>
          <td><span :class="['badge', inv.status.toLowerCase()]">{{ inv.status }}</span></td>
          <td>{{ inv.invoiceDate ?? '—' }}</td>
          <td>{{ inv.totalAmount != null ? `${inv.currency} ${inv.totalAmount}` : '—' }}</td>
          <td><RouterLink :to="`/invoices/${inv.id}`" class="btn-link">Ver</RouterLink></td>
        </tr>
      </tbody>
    </table>

    <div v-if="store.totalPages > 1" class="pagination">
      <button :disabled="store.currentPage === 0" @click="changePage(store.currentPage - 1)">‹</button>
      <span>{{ store.currentPage + 1 }} / {{ store.totalPages }}</span>
      <button :disabled="store.currentPage + 1 >= store.totalPages" @click="changePage(store.currentPage + 1)">›</button>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2>Subir factura</h2>
        <p class="modal-hint">Formatos aceptados: PDF, JPG, PNG</p>

        <input type="file" accept=".pdf,.jpg,.jpeg,.png" @change="onFileChange" />

        <div v-if="uploadError" class="alert-error">{{ uploadError }}</div>

        <div class="modal-actions">
          <button class="btn-secondary" @click="closeModal" :disabled="uploading">Cancelar</button>
          <button class="btn-primary" @click="submit" :disabled="!selectedFile || uploading">
            {{ uploading ? 'Subiendo...' : 'Subir' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useInvoiceStore } from '../stores/invoiceStore'

const store = useInvoiceStore()
const showModal = ref(false)
const selectedFile = ref(null)
const uploading = ref(false)
const uploadError = ref(null)

onMounted(() => store.fetchInvoices())

function changePage(page) {
  store.fetchInvoices(page)
}

function onFileChange(e) {
  selectedFile.value = e.target.files[0] ?? null
  uploadError.value = null
}

function closeModal() {
  showModal.value = false
  selectedFile.value = null
  uploadError.value = null
}

async function submit() {
  uploading.value = true
  uploadError.value = null
  try {
    await store.uploadInvoice(selectedFile.value)
    closeModal()
    store.fetchInvoices(store.currentPage)
  } catch {
    uploadError.value = 'No se pudo subir la factura. Intenta de nuevo.'
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.page { max-width: 960px; margin: 0 auto; padding: 2rem; }

.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.page-header h1 { margin: 0; font-size: 1.5rem; }

.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 0.75rem 1rem; text-align: left; border-bottom: 1px solid #e5e7eb; }
.table th { font-weight: 600; color: #6b7280; font-size: 0.85rem; text-transform: uppercase; }
.table tr:hover td { background: #f9fafb; }

.empty { text-align: center; color: #9ca3af; padding: 2rem !important; }

.badge { display: inline-block; padding: 0.2rem 0.6rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; }
.badge.processed { background: #d1fae5; color: #065f46; }
.badge.failed { background: #fee2e2; color: #991b1b; }
.badge.pending { background: #fef3c7; color: #92400e; }

.btn-primary { background: #4f46e5; color: white; border: none; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; font-size: 0.9rem; }
.btn-primary:hover { background: #4338ca; }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-secondary { background: white; color: #374151; border: 1px solid #d1d5db; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; font-size: 0.9rem; }
.btn-secondary:hover { background: #f3f4f6; }
.btn-secondary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-link { color: #4f46e5; text-decoration: none; font-size: 0.9rem; }
.btn-link:hover { text-decoration: underline; }

.pagination { display: flex; align-items: center; gap: 1rem; justify-content: center; margin-top: 1.5rem; }
.pagination button { background: none; border: 1px solid #d1d5db; border-radius: 4px; padding: 0.3rem 0.7rem; cursor: pointer; font-size: 1rem; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }

.loading { text-align: center; padding: 3rem; color: #6b7280; }
.alert-error { background: #fee2e2; color: #991b1b; padding: 0.75rem 1rem; border-radius: 6px; margin-bottom: 1rem; }

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal { background: white; border-radius: 10px; padding: 2rem; width: 100%; max-width: 420px; }
.modal h2 { margin: 0 0 0.25rem; }
.modal-hint { color: #6b7280; font-size: 0.85rem; margin-bottom: 1.25rem; }
.modal input[type="file"] { width: 100%; margin-bottom: 1rem; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.25rem; }
</style>
