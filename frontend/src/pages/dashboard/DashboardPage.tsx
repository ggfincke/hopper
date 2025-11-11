import { useState } from 'react'
import { MetricCard } from '../../components/dashboard/MetricCard'
import { NavItem } from '../../components/dashboard/NavItem'
import { OrdersTable } from '../../components/dashboard/OrdersTable'
import { SalesChartCard } from '../../components/dashboard/SalesChartCard'
import { TopProductsCard } from '../../components/dashboard/TopProductsCard'
import {
  METRIC_SUMMARIES,
  NAV_ITEMS,
  RECENT_ORDERS,
  SALES_PERFORMANCE,
  TOP_PRODUCTS,
} from '../../lib/dashboardData'

export function DashboardPage() {
  const [isDark, setIsDark] = useState(true)

  const rootClasses = [
    'min-h-screen w-full font-sans',
    isDark
      ? 'bg-[radial-gradient(circle_at_top,_#050914,_#020617,_#01030a)] text-slate-100'
      : 'bg-gradient-to-br from-slate-100 via-white to-slate-200 text-slate-900',
  ]

  const sidebarClasses = [
    'flex w-64 flex-col justify-between border-r px-6 py-8',
    isDark
      ? 'border-slate-900 bg-[#040815]/95 text-slate-200'
      : 'border-slate-200 bg-white text-slate-800',
  ]

  const searchClasses = [
    'flex flex-1 max-w-xl items-center gap-3 rounded-full px-4 py-2 text-sm transition-shadow',
    isDark
      ? 'bg-slate-900/80 text-slate-100 shadow-[0_0_0_1px_rgba(15,23,42,0.85)]'
      : 'bg-white text-slate-800 shadow-[0_1px_0_rgba(148,163,184,0.35)]',
  ]

  const micButtonClasses = [
    'flex h-8 w-8 items-center justify-center rounded-full text-base transition-colors',
    isDark ? 'bg-slate-800 text-slate-200' : 'bg-indigo-50 text-slate-700',
  ]

  const profileClasses = [
    'flex items-center gap-3 rounded-full px-3 py-1 text-xs',
    isDark
      ? 'border border-indigo-700/70 bg-slate-900/70 text-white'
      : 'border border-slate-300 bg-white text-slate-700',
  ]

  const accentText = isDark ? 'text-slate-400' : 'text-slate-600'

  return (
    <div className={rootClasses.join(' ')}>
      <div className="flex min-h-screen w-full overflow-hidden">
        <aside className={sidebarClasses.join(' ')}>
          <div className="flex flex-col gap-6">
            <div className="flex items-center gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-full bg-[conic-gradient(from_180deg_at_50%_50%,_#22c55e,_#6366f1,_#ec4899,_#22c55e)] text-lg font-bold text-white">
                H
              </div>
              <span className="text-lg font-semibold">Hopper</span>
            </div>

            <nav className="flex flex-col gap-1.5">
              {NAV_ITEMS.map((item) => (
                <NavItem
                  key={item.label}
                  icon={item.icon}
                  label={item.label}
                  active={item.active}
                  isDark={isDark}
                />
              ))}
            </nav>
          </div>
        </aside>

        <main className="flex flex-1 flex-col gap-6 overflow-x-hidden px-4 py-6 lg:px-10">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div className={searchClasses.join(' ')}>
              <span className="text-lg opacity-70">🔍</span>
              <input
                placeholder="Search products, orders, customers..."
                className={[
                  'w-full bg-transparent text-sm focus:outline-none',
                  isDark
                    ? 'placeholder:text-slate-500 text-slate-100'
                    : 'placeholder:text-slate-500 text-slate-900',
                ].join(' ')}
              />
              <button className={micButtonClasses.join(' ')}>🎙</button>
            </div>

            <div className="flex items-center gap-3">
              <button
                onClick={() => setIsDark((prev) => !prev)}
                className={[
                  'flex h-9 w-9 items-center justify-center rounded-full text-lg transition-colors',
                  isDark ? 'bg-slate-100 text-slate-900' : 'bg-slate-900 text-slate-100',
                ].join(' ')}
              >
                {isDark ? '☀️' : '🌙'}
              </button>
              <button className="flex h-9 w-9 items-center justify-center rounded-full bg-amber-100 text-lg">
                🔔
              </button>
              <div className={profileClasses.join(' ')}>
                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-[linear-gradient(135deg,_#fb7185,_#f97316,_#22c55e)] text-sm font-bold text-white">
                  G
                </div>
                <div>
                  <div className="text-xs font-semibold leading-tight">Garrett Fincke</div>
                  <div className={`${accentText} text-[11px] leading-tight`}>
                    garrett@hopper.app
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-3">
            <div>
              <h1 className="text-2xl font-bold">
                Welcome back, <span className="text-indigo-400">Garrett</span>!
              </h1>
              <p className={`mt-1 text-xs ${accentText}`}>
                Here's your current multi-channel sales overview.
              </p>
            </div>

            <div className="grid grid-cols-1 gap-4 md:grid-cols-8">
              {METRIC_SUMMARIES.map((metric, idx) => (
                <MetricCard
                  key={metric.title}
                  {...metric}
                  isDark={isDark}
                  className={idx === 0 ? 'col-span-full md:col-span-4' : 'col-span-full md:col-span-2'}
                />
              ))}
            </div>
          </div>

          <div className="grid grid-cols-1 gap-4 lg:grid-cols-[1.35fr_1fr]">
            <SalesChartCard isDark={isDark} data={SALES_PERFORMANCE} />
            <TopProductsCard isDark={isDark} products={TOP_PRODUCTS} />
          </div>

          <OrdersTable isDark={isDark} orders={RECENT_ORDERS} />
        </main>
      </div>
    </div>
  )
}
