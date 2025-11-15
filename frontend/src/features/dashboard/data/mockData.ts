import type {
  MetricSummary,
  NavItemData,
  ProductPerformance,
  RecentOrder,
  SalesPoint,
} from '../types'

export const NAV_ITEMS: NavItemData[] = [
  { icon: '▣', label: 'Overview', active: true },
  { icon: '⬚', label: 'Products' },
  { icon: '👥', label: 'Customers' },
  { icon: '🧾', label: 'Orders' },
  { icon: '📦', label: 'Shipment' },
  { icon: '⚙︎', label: 'Store Settings' },
  { icon: '∞', label: 'Platform Links' },
  { icon: '💬', label: 'Feedback' },
  { icon: '?', label: 'Help & Support' },
]

export const METRIC_SUMMARIES: MetricSummary[] = [
  {
    title: 'AVG. Order Value',
    label: 'Across all connected platforms',
    value: '$77.21',
    change: '+3.16%',
    positive: true,
    icon: '💳',
    highlight: true,
    gradientClass: 'bg-[linear-gradient(135deg,_#4f46e5,_#22c55e)]',
  },
  {
    title: 'Total Orders',
    label: 'Last 30 days',
    value: '2,107',
    change: '-1.18%',
    positive: false,
    icon: '🧾',
  },
  {
    title: 'Lifetime Value',
    label: 'Per active customer',
    value: '$653',
    change: '+2.24%',
    positive: true,
    icon: '✨',
  },
]

export const SALES_PERFORMANCE: SalesPoint[] = [
  { month: 'Jun', revenue: 40, orders: 65 },
  { month: 'Jul', revenue: 55, orders: 45 },
  { month: 'Aug', revenue: 47, orders: 60 },
  { month: 'Sep', revenue: 60, orders: 40 },
  { month: 'Oct', revenue: 72, orders: 52 },
  { month: 'Nov', revenue: 64, orders: 56 },
  { month: 'Dec', revenue: 80, orders: 35 },
]

export const TOP_PRODUCTS: ProductPerformance[] = [
  {
    name: 'Red Tape Sports Shoes',
    sales: '12,429 sales',
    stock: '135 in stock',
    icon: '👟',
    accentClass: 'bg-gradient-to-br from-emerald-400 to-emerald-500 text-white',
  },
  {
    name: 'Fastrack FS1 Pro Smartwatch',
    sales: '7,543 sales',
    stock: '79 in stock',
    icon: '⌚',
    accentClass: 'bg-gradient-to-br from-indigo-400 to-indigo-500 text-white',
  },
  {
    name: "Leriya Fashion Men's Shirt",
    sales: '7,222 sales',
    stock: '465 in stock',
    icon: '👕',
    accentClass: 'bg-gradient-to-br from-orange-400 to-pink-500 text-white',
  },
]

export const RECENT_ORDERS: RecentOrder[] = [
  {
    id: '#2456JL',
    product: 'Nike Sportswear',
    date: 'Jan 12, 12:23 pm',
    price: '$134.00',
    payment: 'Transfer',
    status: 'Processing',
    statusTone: 'indigo',
  },
  {
    id: '#5435DF',
    product: 'Acqua di Parma',
    date: 'May 01, 01:13 pm',
    price: '$23.00',
    payment: 'Credit card',
    status: 'Completed',
    statusTone: 'emerald',
  },
  {
    id: '#9876XC',
    product: 'Allen Solly',
    date: 'Sep 20, 09:08 am',
    price: '$441.00',
    payment: 'Transfer',
    status: 'Completed',
    statusTone: 'emerald',
  },
]
