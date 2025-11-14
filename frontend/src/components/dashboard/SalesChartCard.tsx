import { useTheme } from '../../hooks/useTheme'
import { cn } from '../../lib/utils'
import type { SalesPoint } from '../../lib/dashboardData'

type SalesChartCardProps = {
  data: SalesPoint[]
}

/**
 * SalesChartCard displays sales performance over time with a bar chart.
 * Shows revenue and orders side-by-side for trend comparison.
 *
 * @param data - Array of sales data points by month
 */
export function SalesChartCard({ data }: SalesChartCardProps) {
  const { isDark } = useTheme()

  const containerClasses = cn(
    'flex flex-col gap-3 rounded-2xl border p-4 shadow-[0_16px_40px_rgba(15,23,42,0.35)]',
    isDark
      ? 'bg-slate-950/70 border-slate-900/80 text-slate-100'
      : 'bg-white border-slate-200 text-slate-900'
  )

  const chartSurfaceClasses = cn(
    'mt-2 flex min-h-[190px] flex-col justify-between rounded-2xl p-4',
    isDark
      ? 'bg-[radial-gradient(circle_at_top,_#020617,_#0b1120,_#020617)]'
      : 'bg-[radial-gradient(circle_at_top,_#eef2ff,_#e0f2fe,_#f9fafb)]'
  )

  const descriptionClass = isDark ? 'text-slate-400' : 'text-slate-500'
  const legendTextClass = isDark ? 'text-slate-200' : 'text-slate-700'
  const monthTextClass = isDark ? 'text-slate-400' : 'text-slate-500'

  return (
    <div className={containerClasses}>
      <div className="flex items-center justify-between gap-3 text-sm">
        <div>
          <div className="text-sm font-semibold">Sales Overtime</div>
          <div className={cn('mt-1 text-[11px]', descriptionClass)}>
            Revenue vs. orders across all marketplaces.
          </div>
        </div>
        <div className={cn('flex items-center gap-3 text-[11px]', legendTextClass)}>
          <span className="inline-flex items-center gap-1">
            <span className="inline-block h-2 w-2 rounded-full bg-indigo-500" />
            Revenue
          </span>
          <span className="inline-flex items-center gap-1">
            <span className="inline-block h-2 w-2 rounded-full bg-orange-400" />
            Orders
          </span>
        </div>
      </div>

      <div className={chartSurfaceClasses}>
        <div className="flex flex-1 items-end gap-2">
          {data.map(({ month, revenue, orders }) => (
            <div key={month} className="flex flex-1 items-end gap-1">
              <div
                className="flex-1 rounded-full bg-[linear-gradient(180deg,_#4f46e5,_#22c55e_55%,_#0ea5e9)] opacity-80"
                style={{ height: `${Math.min(revenue, 100)}%` }}
              />
              <div
                className="flex-1 rounded-full bg-[linear-gradient(180deg,_#f97316,_#ec4899)] opacity-80"
                style={{ height: `${Math.min(orders, 100)}%` }}
              />
            </div>
          ))}
        </div>
        <div className={cn('mt-2 flex justify-between text-[11px]', monthTextClass)}>
          {data.map(({ month }) => (
            <span key={month}>{month}</span>
          ))}
        </div>
      </div>
    </div>
  )
}
