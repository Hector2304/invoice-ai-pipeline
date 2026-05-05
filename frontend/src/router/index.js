import { createRouter, createWebHistory } from 'vue-router'
import InvoiceList from '../views/InvoiceList.vue'
import InvoiceDetail from '../views/InvoiceDetail.vue'

const routes = [
  { path: '/', component: InvoiceList },
  { path: '/invoices/:id', component: InvoiceDetail }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
