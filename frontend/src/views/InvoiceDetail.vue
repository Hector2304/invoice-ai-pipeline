<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <nav class="bg-white border-b border-gray-200">
      <div class="max-w-6xl mx-auto px-6 py-4 flex items-center gap-2">
        <div class="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
          <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
        </div>
        <span class="font-semibold text-gray-900 text-lg">Invoice Pipeline</span>
      </div>
    </nav>

    <main class="max-w-6xl mx-auto px-6 py-8">
      <!-- Back -->
      <RouterLink to="/" class="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-indigo-600 transition-colors mb-6">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        Volver a facturas
      </RouterLink>

      <div v-if="store.loading" class="flex items-center justify-center py-20 text-gray-400 text-sm gap-2">
        <svg class="animate-spin w-5 h-5" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
        </svg>
        Cargando factura...
      </div>

      <div v-else-if="store.error" class="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm">
        {{ store.error }}
      </div>

      <template v-else-if="invoice">
        <!-- Header card -->
        <div class="bg-white rounded-xl border border-gray-200 shadow-sm p-6 mb-5">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h1 class="text-2xl font-bold text-gray-900">{{ invoice.vendor ?? 'Sin proveedor' }}</h1>
              <p class="text-sm text-gray-500 mt-1">{{ invoice.fileName }}</p>
            </div>
            <span :class="statusClass(invoice.status)" class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-semibold shrink-0">
              <span class="w-2 h-2 rounded-full" :class="statusDotClass(invoice.status)"></span>
              {{ invoice.status }}
            </span>
          </div>

          <div class="grid grid-cols-2 md:grid-cols-4 gap-6 mt-6 pt-6 border-t border-gray-100">
            <div>
              <p class="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-1">Fecha de factura</p>
              <p class="text-sm font-medium text-gray-900">{{ invoice.invoiceDate ?? '—' }}</p>
            </div>
            <div>
              <p class="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-1">Total</p>
              <p class="text-sm font-medium text-gray-900">
                {{ invoice.totalAmount != null ? `${invoice.currency} ${invoice.totalAmount}` : '—' }}
              </p>
            </div>
            <div>
              <p class="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-1">Subida el</p>
              <p class="text-sm font-medium text-gray-900">{{ formatDate(invoice.createdAt) }}</p>
            </div>
            <div>
              <p class="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-1">ID</p>
              <p class="text-xs font-mono text-gray-500 break-all">{{ invoice.id }}</p>
            </div>
          </div>
        </div>

        <!-- Line items card -->
        <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-100">
            <h2 class="text-sm font-semibold text-gray-900">Líneas de detalle</h2>
          </div>

          <div v-if="!invoice.lineItems?.length" class="flex flex-col items-center justify-center py-14 text-gray-400">
            <svg class="w-8 h-8 mb-2 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 6h16M4 10h16M4 14h10" />
            </svg>
            <span class="text-sm">Sin líneas de detalle</span>
          </div>

          <table v-else class="w-full text-sm">
            <thead>
              <tr class="bg-gray-50 border-b border-gray-100 text-left">
                <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Descripción</th>
                <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide text-right">Cantidad</th>
                <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide text-right">Precio unitario</th>
                <th class="px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide text-right">Total</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              <tr v-for="item in invoice.lineItems" :key="item.id" class="hover:bg-gray-50 transition-colors">
                <td class="px-6 py-4 text-gray-900">{{ item.description }}</td>
                <td class="px-6 py-4 text-gray-600 text-right">{{ item.quantity ?? '—' }}</td>
                <td class="px-6 py-4 text-gray-600 text-right">{{ item.unitPrice ?? '—' }}</td>
                <td class="px-6 py-4 font-medium text-gray-900 text-right">{{ item.totalPrice ?? '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>
    </main>
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

function statusClass(status) {
  return {
    PROCESSED: 'bg-emerald-50 text-emerald-700',
    FAILED: 'bg-red-50 text-red-700',
    PENDING: 'bg-amber-50 text-amber-700',
  }[status] ?? 'bg-gray-100 text-gray-600'
}

function statusDotClass(status) {
  return {
    PROCESSED: 'bg-emerald-500',
    FAILED: 'bg-red-500',
    PENDING: 'bg-amber-500',
  }[status] ?? 'bg-gray-400'
}
</script>
