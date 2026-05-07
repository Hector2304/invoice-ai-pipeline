<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <nav class="bg-white border-b border-gray-200">
      <div class="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
          <span class="font-semibold text-gray-900 text-lg">Invoice Pipeline</span>
        </div>
        <button
          @click="showModal = true"
          class="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          Subir factura
        </button>
      </div>
    </nav>

    <!-- Content -->
    <main class="max-w-6xl mx-auto px-6 py-8">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900">Facturas</h1>
        <p class="text-sm text-gray-500 mt-1">Gestiona y revisa tus facturas procesadas</p>
      </div>

      <!-- Error -->
      <div v-if="store.error" class="mb-4 bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm">
        {{ store.error }}
      </div>

      <!-- Table card -->
      <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        <!-- Loading -->
        <div v-if="store.loading" class="flex items-center justify-center py-20 text-gray-400 text-sm gap-2">
          <svg class="animate-spin w-5 h-5" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
          </svg>
          Cargando facturas...
        </div>

        <table v-else class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-100 bg-gray-50 text-left">
              <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Proveedor</th>
              <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Archivo</th>
              <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Estado</th>
              <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Fecha</th>
              <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Total</th>
              <th class="px-6 py-3"></th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="store.invoices.length === 0">
              <td colspan="6" class="px-6 py-16 text-center text-gray-400">
                <svg class="w-10 h-10 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                No hay facturas aún
              </td>
            </tr>
            <tr v-for="inv in store.invoices" :key="inv.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 font-medium text-gray-900">{{ inv.vendor ?? '—' }}</td>
              <td class="px-6 py-4 text-gray-500 max-w-[180px] truncate">{{ inv.fileName }}</td>
              <td class="px-6 py-4">
                <span :class="statusClass(inv.status)" class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold">
                  <span class="w-1.5 h-1.5 rounded-full" :class="statusDotClass(inv.status)"></span>
                  {{ inv.status }}
                </span>
              </td>
              <td class="px-6 py-4 text-gray-600">{{ inv.invoiceDate ?? '—' }}</td>
              <td class="px-6 py-4 font-medium text-gray-900">
                {{ inv.totalAmount != null ? `${inv.currency} ${inv.totalAmount}` : '—' }}
              </td>
              <td class="px-6 py-4 text-right">
                <div class="flex items-center justify-end gap-3">
                  <button
                    v-if="inv.status === 'FAILED'"
                    @click="openRetry(inv)"
                    class="text-amber-600 hover:text-amber-800 font-medium text-sm transition-colors"
                  >
                    Reintentar
                  </button>
                  <RouterLink
                    :to="`/invoices/${inv.id}`"
                    class="text-indigo-600 hover:text-indigo-800 font-medium text-sm transition-colors"
                  >
                    Ver →
                  </RouterLink>
                  <button
                    @click="handleDelete(inv.id)"
                    class="text-red-400 hover:text-red-600 transition-colors"
                    title="Eliminar"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div v-if="store.totalPages > 1" class="flex items-center justify-between px-6 py-4 border-t border-gray-100 bg-gray-50">
          <span class="text-sm text-gray-500">Página {{ store.currentPage + 1 }} de {{ store.totalPages }}</span>
          <div class="flex gap-2">
            <button
              :disabled="store.currentPage === 0"
              @click="changePage(store.currentPage - 1)"
              class="px-3 py-1.5 text-sm border border-gray-200 rounded-lg hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
            >
              ← Anterior
            </button>
            <button
              :disabled="store.currentPage + 1 >= store.totalPages"
              @click="changePage(store.currentPage + 1)"
              class="px-3 py-1.5 text-sm border border-gray-200 rounded-lg hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
            >
              Siguiente →
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- Modal -->
    <Transition name="fade">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="closeModal"></div>
        <div class="relative bg-white rounded-2xl shadow-xl w-full max-w-md p-6">
          <div class="flex items-center justify-between mb-5">
            <h2 class="text-lg font-semibold text-gray-900">{{ retryFileName ? 'Reintentar factura' : 'Subir factura' }}</h2>
            <button @click="closeModal" class="text-gray-400 hover:text-gray-600 transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div v-if="retryFileName" class="mb-4 bg-amber-50 border border-amber-200 text-amber-700 rounded-lg px-3 py-2 text-sm">
            Sube el mismo archivo: <span class="font-medium">{{ retryFileName }}</span>
          </div>

          <!-- Drop zone -->
          <label
            class="flex flex-col items-center justify-center w-full h-36 border-2 border-dashed border-gray-300 rounded-xl cursor-pointer hover:border-indigo-400 hover:bg-indigo-50 transition-colors"
            :class="{ 'border-indigo-500 bg-indigo-50': selectedFile }"
          >
            <svg class="w-8 h-8 text-gray-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
            </svg>
            <span v-if="!selectedFile" class="text-sm text-gray-500">
              Haz clic o arrastra un archivo aquí
            </span>
            <span v-else class="text-sm font-medium text-indigo-700 px-4 text-center truncate max-w-full">
              {{ selectedFile.name }}
            </span>
            <span class="text-xs text-gray-400 mt-1">PDF, JPG, PNG — máx 10MB</span>
            <input type="file" accept=".pdf,.jpg,.jpeg,.png" class="hidden" @change="onFileChange" />
          </label>

          <div v-if="uploadError" class="mt-3 bg-red-50 border border-red-200 text-red-700 rounded-lg px-3 py-2 text-sm">
            {{ uploadError }}
          </div>

          <div class="flex gap-3 mt-5">
            <button
              @click="closeModal"
              :disabled="uploading"
              class="flex-1 px-4 py-2.5 text-sm font-medium border border-gray-200 rounded-lg hover:bg-gray-50 disabled:opacity-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              @click="submit"
              :disabled="!selectedFile || uploading"
              class="flex-1 px-4 py-2.5 text-sm font-medium bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
            >
              <svg v-if="uploading" class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
              </svg>
              {{ uploading ? 'Subiendo...' : 'Subir' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useInvoiceStore } from '../stores/invoiceStore'

const store = useInvoiceStore()
const router = useRouter()
const showModal = ref(false)
const selectedFile = ref(null)
const uploading = ref(false)
const uploadError = ref(null)
const retryFileName = ref(null)

onMounted(() => store.fetchInvoices())

function changePage(page) {
  store.fetchInvoices(page)
}

function onFileChange(e) {
  selectedFile.value = e.target.files[0] ?? null
  uploadError.value = null
}

function openRetry(inv) {
  retryFileName.value = inv.fileName
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  selectedFile.value = null
  uploadError.value = null
  retryFileName.value = null
}

async function handleDelete(id) {
  if (!window.confirm('¿Eliminar esta factura? Esta acción no se puede deshacer.')) return
  try {
    await store.deleteInvoice(id)
    store.fetchInvoices(store.currentPage)
  } catch {
    alert('No se pudo eliminar la factura.')
  }
}

async function submit() {
  uploading.value = true
  uploadError.value = null
  try {
    await store.uploadInvoice(selectedFile.value)
    closeModal()
    store.fetchInvoices(store.currentPage)
  } catch (err) {
    if (err.message === 'duplicate' && err.existingId) {
      closeModal()
      router.push(`/invoices/${err.existingId}`)
    } else {
      uploadError.value = 'No se pudo subir la factura. Intenta de nuevo.'
    }
  } finally {
    uploading.value = false
  }
}

function statusClass(status) {
  return {
    PROCESSED: 'bg-emerald-50 text-emerald-700',
    FAILED: 'bg-red-50 text-red-700',
    PENDING: 'bg-amber-50 text-amber-700',
    NOT_AN_INVOICE: 'bg-orange-50 text-orange-700',
  }[status] ?? 'bg-gray-100 text-gray-600'
}

function statusDotClass(status) {
  return {
    PROCESSED: 'bg-emerald-500',
    FAILED: 'bg-red-500',
    PENDING: 'bg-amber-500',
    NOT_AN_INVOICE: 'bg-orange-500',
  }[status] ?? 'bg-gray-400'
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
