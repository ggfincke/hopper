export type NavItemData = {
  icon: string
  label: string
  active?: boolean
}

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

export type SalesPoint = {
  month: string
  revenue: number
  orders: number
}

export type ProductPerformance = {
  name: string
  sales: string
  stock: string
  icon: string
  accentClass: string
}

export type RecentOrder = {
  id: string
  product: string
  date: string
  price: string
  payment: string
  status: string
  statusTone: 'indigo' | 'emerald'
}
