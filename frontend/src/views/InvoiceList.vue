<template>
  <div class="min-h-screen bg-[#f6f7f2] text-slate-950">
    <nav class="sticky top-0 z-30 border-b border-slate-200/80 bg-[#f6f7f2]/85 backdrop-blur-xl">
      <div class="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
        <RouterLink to="/" class="flex min-w-0 items-center gap-3">
          <div class="grid h-10 w-10 shrink-0 place-items-center rounded-lg bg-slate-950 shadow-sm">
            <svg class="h-5 w-5 text-[#d7ff4f]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9 12h6m-6 4h6m2 5H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5.586a1 1 0 0 1 .707.293l5.414 5.414a1 1 0 0 1 .293.707V19a2 2 0 0 1-2 2z" />
            </svg>
          </div>
          <div class="min-w-0">
            <p class="truncate text-sm font-semibold leading-tight text-slate-950">Invoice Pipeline</p>
            <p class="hidden text-xs text-slate-500 sm:block">Procesamiento y revisión de facturas</p>
          </div>
        </RouterLink>

        <button
          @click="showModal = true"
          class="inline-flex h-10 items-center justify-center gap-2 rounded-lg bg-slate-950 px-4 text-sm font-semibold text-white shadow-sm transition hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-[#d7ff4f] focus:ring-offset-2 focus:ring-offset-[#f6f7f2]"
        >
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          <span class="hidden sm:inline">Subir factura</span>
          <span class="sm:hidden">Subir</span>
        </button>
      </div>
    </nav>

    <main class="mx-auto max-w-7xl px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
      <section class="mb-6 grid gap-4 md:grid-cols-[1fr_auto] md:items-end">
        <div>
          <div class="mb-3 inline-flex items-center gap-2 rounded-full border border-slate-200 bg-white/70 px-3 py-1 text-xs font-medium text-slate-600 shadow-sm">
            <span class="h-1.5 w-1.5 rounded-full bg-[#ff6b35]"></span>
            Panel de control
          </div>
          <h1 class="text-3xl font-bold tracking-normal text-slate-950 sm:text-4xl">Facturas</h1>
          <p class="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
            Revisa archivos procesados, estados de extracción y totales detectados.
          </p>
        </div>

        <div class="grid grid-cols-3 gap-2 rounded-lg border border-slate-200 bg-white p-2 shadow-sm">
          <div class="rounded-lg bg-slate-50 px-3 py-2">
            <p class="text-[11px] font-semibold uppercase text-slate-500">Total</p>
            <p class="text-lg font-bold text-slate-950">{{ store.invoices.length }}</p>
          </div>
          <div class="rounded-lg bg-emerald-50 px-3 py-2">
            <p class="text-[11px] font-semibold uppercase text-emerald-700">OK</p>
            <p class="text-lg font-bold text-emerald-900">{{ processedCount }}</p>
          </div>
          <div class="rounded-lg bg-rose-50 px-3 py-2">
            <p class="text-[11px] font-semibold uppercase text-rose-700">Fallidas</p>
            <p class="text-lg font-bold text-rose-900">{{ failedCount }}</p>
          </div>
        </div>
      </section>

      <div v-if="store.error" class="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700 shadow-sm">
        {{ store.error }}
      </div>

      <section class="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
        <div class="flex flex-col gap-3 border-b border-slate-100 px-4 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6">
          <div>
            <h2 class="text-sm font-semibold text-slate-950">Bandeja de facturas</h2>
            <p class="mt-1 text-xs text-slate-500">Ordenadas por fecha de carga más reciente</p>
          </div>
          <div class="inline-flex w-fit items-center gap-2 rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-600">
            <span class="h-1.5 w-1.5 rounded-full bg-slate-400"></span>
            Página {{ store.currentPage + 1 }}
          </div>
        </div>

        <div v-if="store.loading" class="flex items-center justify-center gap-3 py-24 text-sm font-medium text-slate-500">
          <svg class="h-5 w-5 animate-spin text-slate-900" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-20" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
            <path class="opacity-90" fill="currentColor" d="M4 12a8 8 0 0 1 8-8v8z"/>
          </svg>
          Cargando facturas...
        </div>

        <div v-else-if="store.invoices.length === 0" class="grid place-items-center px-6 py-20 text-center">
          <div class="grid h-14 w-14 place-items-center rounded-lg bg-[#d7ff4f] text-slate-950">
            <svg class="h-7 w-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.6" d="M9 12h6m-6 4h6m2 5H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5.586a1 1 0 0 1 .707.293l5.414 5.414a1 1 0 0 1 .293.707V19a2 2 0 0 1-2 2z" />
            </svg>
          </div>
          <h3 class="mt-4 text-base font-semibold text-slate-950">No hay facturas aún</h3>
          <p class="mt-1 text-sm text-slate-500">Sube un archivo para iniciar el procesamiento.</p>
        </div>

        <div v-else>
          <div class="hidden overflow-x-auto lg:block">
            <table class="w-full text-sm">
              <thead>
                <tr class="border-b border-slate-100 bg-slate-50/80 text-left">
                  <th class="px-6 py-3 text-xs font-semibold uppercase text-slate-500">Proveedor</th>
                  <th class="px-6 py-3 text-xs font-semibold uppercase text-slate-500">Archivo</th>
                  <th class="px-6 py-3 text-xs font-semibold uppercase text-slate-500">Estado</th>
                  <th class="px-6 py-3 text-xs font-semibold uppercase text-slate-500">Fecha</th>
                  <th class="px-6 py-3 text-xs font-semibold uppercase text-slate-500">Total</th>
                  <th class="px-6 py-3"></th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                <tr v-for="inv in store.invoices" :key="inv.id" class="transition hover:bg-[#fbfcf6]">
                  <td class="px-6 py-4 font-semibold text-slate-950">{{ inv.vendor ?? 'Sin proveedor' }}</td>
                  <td class="max-w-[220px] truncate px-6 py-4 text-slate-500">{{ inv.fileName }}</td>
                  <td class="px-6 py-4">
                    <span :class="statusClass(inv.status)" class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-semibold">
                      <span class="h-1.5 w-1.5 rounded-full" :class="statusDotClass(inv.status)"></span>
                      {{ inv.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-slate-600">{{ inv.invoiceDate ?? '—' }}</td>
                  <td class="px-6 py-4 font-semibold text-slate-950">
                    {{ inv.totalAmount != null ? `${inv.currency} ${inv.totalAmount}` : '—' }}
                  </td>
                  <td class="px-6 py-4 text-right">
                    <div class="flex items-center justify-end gap-2">
                      <button
                        v-if="inv.status === 'FAILED'"
                        @click="openRetry(inv)"
                        class="rounded-lg border border-amber-200 bg-amber-50 px-3 py-1.5 text-xs font-semibold text-amber-800 transition hover:bg-amber-100"
                      >
                        Reintentar
                      </button>
                      <RouterLink
                        :to="`/invoices/${inv.id}`"
                        class="rounded-lg bg-slate-950 px-3 py-1.5 text-xs font-semibold text-white transition hover:bg-slate-800"
                      >
                        Ver
                      </RouterLink>
                      <button
                        @click="handleDelete(inv.id)"
                        class="grid h-8 w-8 place-items-center rounded-lg text-slate-400 transition hover:bg-red-50 hover:text-red-600"
                        title="Eliminar"
                      >
                        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0 1 16.138 21H7.862a2 2 0 0 1-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="divide-y divide-slate-100 lg:hidden">
            <article v-for="inv in store.invoices" :key="inv.id" class="p-4">
              <div class="flex items-start justify-between gap-3">
                <div class="min-w-0">
                  <h3 class="truncate font-semibold text-slate-950">{{ inv.vendor ?? 'Sin proveedor' }}</h3>
                  <p class="mt-1 truncate text-xs text-slate-500">{{ inv.fileName }}</p>
                </div>
                <span :class="statusClass(inv.status)" class="shrink-0 rounded-full px-2.5 py-1 text-xs font-semibold">
                  {{ inv.status }}
                </span>
              </div>

              <dl class="mt-4 grid grid-cols-2 gap-3 text-sm">
                <div>
                  <dt class="text-xs font-medium text-slate-500">Fecha</dt>
                  <dd class="mt-1 font-semibold text-slate-900">{{ inv.invoiceDate ?? '—' }}</dd>
                </div>
                <div>
                  <dt class="text-xs font-medium text-slate-500">Total</dt>
                  <dd class="mt-1 font-semibold text-slate-900">{{ inv.totalAmount != null ? `${inv.currency} ${inv.totalAmount}` : '—' }}</dd>
                </div>
              </dl>

              <div class="mt-4 flex items-center justify-end gap-2">
                <button v-if="inv.status === 'FAILED'" @click="openRetry(inv)" class="rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-xs font-semibold text-amber-800">
                  Reintentar
                </button>
                <RouterLink :to="`/invoices/${inv.id}`" class="rounded-lg bg-slate-950 px-3 py-2 text-xs font-semibold text-white">
                  Ver detalle
                </RouterLink>
                <button @click="handleDelete(inv.id)" class="grid h-9 w-9 place-items-center rounded-lg border border-slate-200 text-slate-500" title="Eliminar">
                  <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0 1 16.138 21H7.862a2 2 0 0 1-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </article>
          </div>
        </div>

        <div v-if="store.totalPages > 1" class="flex flex-col gap-3 border-t border-slate-100 bg-slate-50/80 px-4 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6">
          <span class="text-sm font-medium text-slate-500">Página {{ store.currentPage + 1 }} de {{ store.totalPages }}</span>
          <div class="flex gap-2">
            <button
              :disabled="store.currentPage === 0"
              @click="changePage(store.currentPage - 1)"
              class="inline-flex h-9 items-center justify-center rounded-lg border border-slate-200 bg-white px-3 text-sm font-semibold text-slate-700 transition hover:border-slate-300 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
            >
              Anterior
            </button>
            <button
              :disabled="store.currentPage + 1 >= store.totalPages"
              @click="changePage(store.currentPage + 1)"
              class="inline-flex h-9 items-center justify-center rounded-lg border border-slate-200 bg-white px-3 text-sm font-semibold text-slate-700 transition hover:border-slate-300 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
            >
              Siguiente
            </button>
          </div>
        </div>
      </section>
    </main>

    <Transition name="fade">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-950/55 backdrop-blur-sm" @click="closeModal"></div>
        <div class="relative w-full max-w-lg overflow-hidden rounded-lg bg-white shadow-2xl">
          <div class="flex items-center justify-between border-b border-slate-100 px-6 py-5">
            <div>
              <h2 class="text-lg font-bold text-slate-950">{{ retryFileName ? 'Reintentar factura' : 'Subir factura' }}</h2>
              <p class="mt-1 text-sm text-slate-500">PDF, JPG o PNG de hasta 10 MB.</p>
            </div>
            <button @click="closeModal" class="grid h-9 w-9 place-items-center rounded-lg text-slate-400 transition hover:bg-slate-100 hover:text-slate-700">
              <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18 18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="p-6">
            <div v-if="retryFileName" class="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-800">
              Sube el mismo archivo: <span class="font-semibold">{{ retryFileName }}</span>
            </div>

            <label
              class="group flex min-h-44 w-full cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed border-slate-300 bg-slate-50 px-5 text-center transition hover:border-slate-950 hover:bg-white"
              :class="{ 'border-slate-950 bg-[#faffdd]': selectedFile }"
            >
              <div class="grid h-12 w-12 place-items-center rounded-lg bg-white text-slate-950 shadow-sm ring-1 ring-slate-200 transition group-hover:ring-slate-950">
                <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M4 16v1a3 3 0 0 0 3 3h10a3 3 0 0 0 3-3v-1m-4-8-4-4m0 0L8 8m4-4v12" />
                </svg>
              </div>
              <span v-if="!selectedFile" class="mt-3 text-sm font-semibold text-slate-700">
                Haz clic o arrastra un archivo aquí
              </span>
              <span v-else class="mt-3 max-w-full truncate px-4 text-sm font-semibold text-slate-950">
                {{ selectedFile.name }}
              </span>
              <span class="mt-1 text-xs text-slate-500">El archivo se enviará al pipeline de extracción.</span>
              <input type="file" accept=".pdf,.jpg,.jpeg,.png" class="hidden" @change="onFileChange" />
            </label>

            <div v-if="uploadError" class="mt-3 rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm font-medium text-red-700">
              {{ uploadError }}
            </div>

            <div class="mt-5 grid grid-cols-2 gap-3">
              <button
                @click="closeModal"
                :disabled="uploading"
                class="h-11 rounded-lg border border-slate-200 bg-white px-4 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:opacity-50"
              >
                Cancelar
              </button>
              <button
                @click="submit"
                :disabled="!selectedFile || uploading"
                class="inline-flex h-11 items-center justify-center gap-2 rounded-lg bg-slate-950 px-4 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-45"
              >
                <svg v-if="uploading" class="h-4 w-4 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 0 1 8-8v8z"/>
                </svg>
                {{ uploading ? 'Subiendo...' : 'Subir' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useInvoiceStore } from '../stores/invoiceStore'

const store = useInvoiceStore()
const router = useRouter()
const showModal = ref(false)
const selectedFile = ref(null)
const uploading = ref(false)
const uploadError = ref(null)
const retryFileName = ref(null)

const processedCount = computed(() => store.invoices.filter((inv) => inv.status === 'PROCESSED').length)
const failedCount = computed(() => store.invoices.filter((inv) => inv.status === 'FAILED').length)

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
    PROCESSED: 'bg-emerald-50 text-emerald-800 ring-1 ring-emerald-200',
    FAILED: 'bg-red-50 text-red-800 ring-1 ring-red-200',
    PENDING: 'bg-amber-50 text-amber-800 ring-1 ring-amber-200',
    NOT_AN_INVOICE: 'bg-orange-50 text-orange-800 ring-1 ring-orange-200',
  }[status] ?? 'bg-slate-100 text-slate-600 ring-1 ring-slate-200'
}

function statusDotClass(status) {
  return {
    PROCESSED: 'bg-emerald-500',
    FAILED: 'bg-red-500',
    PENDING: 'bg-amber-500',
    NOT_AN_INVOICE: 'bg-orange-500',
  }[status] ?? 'bg-slate-400'
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.18s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
