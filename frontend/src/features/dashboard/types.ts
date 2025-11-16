// src/features/dashboard/types.ts
// Shared dashboard data contracts to keep mock data + UI aligned

// sidebar navigation item config w/ icon & active state
export type NavItemData = {
  icon: string
  label: string
  active?: boolean
}

// KPI card data w/ trend indicator & optional gradient styling
export type MetricSummary = {
  title: string
  label: string
  value: string
  change?: string
  positive?: boolean
  icon?: string
  highlight?: boolean
  gradientClass?: string
}

// monthly revenue & order volume data point for chart plotting
export type SalesPoint = {
  month: string
  revenue: number
  orders: number
}

// top product row data w/ sales metrics & inventory level
export type ProductPerformance = {
  name: string
  sales: string
  stock: string
  icon: string
  accentClass: string
}

// order row data w/ payment details & status badge config
export type RecentOrder = {
  id: string
  product: string
  date: string
  price: string
  payment: string
  status: string
  statusTone: 'indigo' | 'emerald'
}
