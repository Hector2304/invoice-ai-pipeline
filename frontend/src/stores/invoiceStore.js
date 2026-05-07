import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useInvoiceStore = defineStore('invoices', () => {
  const invoices = ref([])
  const totalPages = ref(0)
  const currentPage = ref(0)
  const loading = ref(false)
  const error = ref(null)

  async function fetchInvoices(page = 0) {
    loading.value = true
    error.value = null
    try {
      const res = await fetch(`/api/invoices?page=${page}&size=20&sort=createdAt,desc`)
      if (!res.ok) throw new Error()
      const data = await res.json()
      invoices.value = data.content
      totalPages.value = data.totalPages
      currentPage.value = data.number
    } catch {
      error.value = 'Error al cargar facturas'
    } finally {
      loading.value = false
    }
  }

  async function fetchInvoice(id) {
    loading.value = true
    error.value = null
    try {
      const res = await fetch(`/api/invoices/${id}`)
      if (!res.ok) throw new Error()
      return await res.json()
    } catch {
      error.value = 'Error al cargar la factura'
      return null
    } finally {
      loading.value = false
    }
  }

  async function uploadInvoice(file) {
    const form = new FormData()
    form.append('file', file)
    const res = await fetch('/api/invoices', { method: 'POST', body: form })
    if (res.status === 409) {
      const data = await res.json()
      const err = new Error('duplicate')
      err.existingId = data.existingId
      throw err
    }
    if (!res.ok) throw new Error('Error al subir la factura')
    return await res.json()
  }

  async function deleteInvoice(id) {
    const res = await fetch(`/api/invoices/${id}`, { method: 'DELETE' })
    if (!res.ok) throw new Error('Error al eliminar la factura')
  }

  return { invoices, totalPages, currentPage, loading, error, fetchInvoices, fetchInvoice, uploadInvoice, deleteInvoice }
})
