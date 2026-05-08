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
            <p class="hidden text-xs text-slate-500 sm:block">Detalle de factura</p>
          </div>
        </RouterLink>
      </div>
    </nav>

    <main class="mx-auto max-w-7xl px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
      <RouterLink to="/" class="mb-6 inline-flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-white px-3 text-sm font-semibold text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50">
        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m15 19-7-7 7-7" />
        </svg>
        Volver
      </RouterLink>

      <div v-if="store.loading" class="flex items-center justify-center gap-3 rounded-lg border border-slate-200 bg-white py-24 text-sm font-medium text-slate-500 shadow-sm">
        <svg class="h-5 w-5 animate-spin text-slate-900" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-20" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
          <path class="opacity-90" fill="currentColor" d="M4 12a8 8 0 0 1 8-8v8z"/>
        </svg>
        Cargando factura...
      </div>

      <div v-else-if="store.error" class="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700 shadow-sm">
        {{ store.error }}
      </div>

      <template v-else-if="invoice">
        <section class="mb-6 overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
          <div class="grid gap-6 p-5 sm:p-6 lg:grid-cols-[1fr_auto]">
            <div class="min-w-0">
              <div class="mb-3 flex flex-wrap items-center gap-2">
                <span :class="statusClass(invoice.status)" class="inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold">
                  <span class="h-1.5 w-1.5 rounded-full" :class="statusDotClass(invoice.status)"></span>
                  {{ invoice.status }}
                </span>
                <span class="rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-600">{{ formatDate(invoice.createdAt) }}</span>
              </div>
              <h1 class="text-2xl font-bold tracking-normal text-slate-950 sm:text-4xl">{{ invoice.vendor ?? 'Sin proveedor' }}</h1>
              <p class="mt-2 max-w-3xl truncate text-sm text-slate-500">{{ invoice.fileName }}</p>
            </div>

            <div class="flex items-start justify-between gap-3 lg:justify-end">
              <div class="rounded-lg bg-slate-950 px-5 py-4 text-white shadow-sm">
                <p class="text-xs font-semibold uppercase text-slate-300">Total detectado</p>
                <p class="mt-1 text-2xl font-bold">{{ invoice.totalAmount != null ? `${invoice.currency} ${invoice.totalAmount}` : '—' }}</p>
              </div>
              <button
                @click="handleDelete"
                class="grid h-11 w-11 shrink-0 place-items-center rounded-lg border border-red-200 bg-red-50 text-red-700 transition hover:bg-red-100"
                title="Eliminar"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0 1 16.138 21H7.862a2 2 0 0 1-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v3M4 7h16" />
                </svg>
              </button>
            </div>
          </div>

          <div class="grid border-t border-slate-100 bg-slate-50/70 sm:grid-cols-2 lg:grid-cols-4">
            <div class="border-b border-slate-100 px-5 py-4 sm:border-r lg:border-b-0">
              <p class="text-xs font-semibold uppercase text-slate-500">Fecha de factura</p>
              <p class="mt-1 font-semibold text-slate-950">{{ invoice.invoiceDate ?? '—' }}</p>
            </div>
            <div class="border-b border-slate-100 px-5 py-4 lg:border-r lg:border-b-0">
              <p class="text-xs font-semibold uppercase text-slate-500">Moneda</p>
              <p class="mt-1 font-semibold text-slate-950">{{ invoice.currency ?? '—' }}</p>
            </div>
            <div class="border-b border-slate-100 px-5 py-4 sm:border-r sm:border-b-0">
              <p class="text-xs font-semibold uppercase text-slate-500">Líneas</p>
              <p class="mt-1 font-semibold text-slate-950">{{ invoice.lineItems?.length ?? 0 }}</p>
            </div>
            <div class="px-5 py-4">
              <p class="text-xs font-semibold uppercase text-slate-500">ID</p>
              <p class="mt-1 truncate font-mono text-xs text-slate-600">{{ invoice.id }}</p>
            </div>
          </div>
        </section>

        <section class="grid gap-6 lg:grid-cols-[minmax(0,1fr)_320px]">
          <div class="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
            <div class="flex items-center justify-between border-b border-slate-100 px-5 py-4 sm:px-6">
              <div>
                <h2 class="text-sm font-semibold text-slate-950">Líneas de detalle</h2>
                <p class="mt-1 text-xs text-slate-500">Conceptos extraídos del documento</p>
              </div>
              <span class="rounded-full bg-[#d7ff4f] px-3 py-1 text-xs font-bold text-slate-950">{{ invoice.lineItems?.length ?? 0 }}</span>
            </div>

            <div v-if="!invoice.lineItems?.length" class="grid place-items-center px-6 py-20 text-center">
              <div class="grid h-12 w-12 place-items-center rounded-lg bg-slate-100 text-slate-500">
                <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.6" d="M4 6h16M4 10h16M4 14h10" />
                </svg>
              </div>
              <p class="mt-3 text-sm font-semibold text-slate-700">Sin líneas de detalle</p>
            </div>

            <div v-else>
              <div class="hidden overflow-x-auto md:block">
                <table class="w-full text-sm">
                  <thead>
                    <tr class="border-b border-slate-100 bg-slate-50/80 text-left">
                      <th class="px-6 py-3 text-xs font-semibold uppercase text-slate-500">Descripción</th>
                      <th class="px-6 py-3 text-right text-xs font-semibold uppercase text-slate-500">Cantidad</th>
                      <th class="px-6 py-3 text-right text-xs font-semibold uppercase text-slate-500">Precio unitario</th>
                      <th class="px-6 py-3 text-right text-xs font-semibold uppercase text-slate-500">Total</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-slate-100">
                    <tr v-for="item in invoice.lineItems" :key="item.id" class="transition hover:bg-[#fbfcf6]">
                      <td class="px-6 py-4 font-medium text-slate-950">{{ item.description }}</td>
                      <td class="px-6 py-4 text-right text-slate-600">{{ item.quantity ?? '—' }}</td>
                      <td class="px-6 py-4 text-right text-slate-600">{{ item.unitPrice ?? '—' }}</td>
                      <td class="px-6 py-4 text-right font-semibold text-slate-950">{{ item.totalPrice ?? '—' }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div class="divide-y divide-slate-100 md:hidden">
                <article v-for="item in invoice.lineItems" :key="item.id" class="p-4">
                  <h3 class="font-semibold text-slate-950">{{ item.description }}</h3>
                  <dl class="mt-3 grid grid-cols-3 gap-3 text-sm">
                    <div>
                      <dt class="text-xs font-medium text-slate-500">Cant.</dt>
                      <dd class="mt-1 font-semibold">{{ item.quantity ?? '—' }}</dd>
                    </div>
                    <div>
                      <dt class="text-xs font-medium text-slate-500">Unitario</dt>
                      <dd class="mt-1 font-semibold">{{ item.unitPrice ?? '—' }}</dd>
                    </div>
                    <div>
                      <dt class="text-xs font-medium text-slate-500">Total</dt>
                      <dd class="mt-1 font-semibold">{{ item.totalPrice ?? '—' }}</dd>
                    </div>
                  </dl>
                </article>
              </div>
            </div>
          </div>

          <aside class="space-y-4">
            <div class="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
              <h2 class="text-sm font-semibold text-slate-950">Resumen</h2>
              <dl class="mt-4 space-y-3 text-sm">
                <div class="flex items-center justify-between gap-4">
                  <dt class="text-slate-500">Proveedor</dt>
                  <dd class="truncate font-semibold text-slate-950">{{ invoice.vendor ?? '—' }}</dd>
                </div>
                <div class="flex items-center justify-between gap-4">
                  <dt class="text-slate-500">Estado</dt>
                  <dd class="font-semibold text-slate-950">{{ invoice.status }}</dd>
                </div>
                <div class="flex items-center justify-between gap-4">
                  <dt class="text-slate-500">Subida</dt>
                  <dd class="font-semibold text-slate-950">{{ formatDate(invoice.createdAt) }}</dd>
                </div>
              </dl>
            </div>

            <div class="rounded-lg border border-slate-200 bg-[#12231f] p-5 text-white shadow-sm">
              <p class="text-xs font-semibold uppercase text-[#d7ff4f]">Archivo fuente</p>
              <p class="mt-2 break-words text-sm font-medium text-white/90">{{ invoice.fileName }}</p>
            </div>
          </aside>
        </section>
      </template>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useInvoiceStore } from '../stores/invoiceStore'

const route = useRoute()
const router = useRouter()
const store = useInvoiceStore()
const invoice = ref(null)

onMounted(async () => {
  invoice.value = await store.fetchInvoice(route.params.id)
})

async function handleDelete() {
  if (!window.confirm('¿Eliminar esta factura? Esta acción no se puede deshacer.')) return
  try {
    await store.deleteInvoice(route.params.id)
    router.push('/')
  } catch {
    alert('No se pudo eliminar la factura.')
  }
}

function formatDate(iso) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('es-ES', { dateStyle: 'medium', timeStyle: 'short' })
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
