import { TrendingUp, TrendingDown } from 'lucide-react'
import { useTheme } from '../../../../hooks/useTheme'
import { cn } from '../../../../lib/utils'
import type { MetricSummary } from '../../types'

type MetricCardProps = MetricSummary & {
  className?: string
}

/**
 * MetricCard displays a key performance metric with optional change indicator.
 * Automatically adapts styling based on theme and highlight mode.
 *
 * @param highlight - If true, displays with gradient background
 */
export function MetricCard({
  title,
  label,
  value,
  change,
  positive = true,
  icon,
  highlight,
  gradientClass,
  className,
}: MetricCardProps) {
  const { isDark } = useTheme()

  const containerClasses = cn(
    'flex flex-col gap-2 rounded-2xl border p-4 shadow-[0_18px_45px_rgba(15,23,42,0.24)] min-w-0',
    highlight
      ? `${gradientClass ?? 'bg-[linear-gradient(135deg,_#4f46e5,_#22c55e)]'} text-slate-50 border-white/10`
      : isDark
        ? 'bg-slate-950/70 text-slate-100 border-slate-800/70'
        : 'bg-white text-slate-900 border-slate-200',
    className
  )

  const iconClasses = cn(
    'flex h-8 w-8 items-center justify-center rounded-full text-base',
    highlight
      ? 'bg-white/20 text-white'
      : isDark
        ? 'bg-slate-800 text-slate-100'
        : 'bg-slate-100 text-slate-900'
  )

  return (
    <div className={containerClasses}>
      <div className="flex items-center justify-between gap-2">
        <div>
          <div className="text-xs font-medium opacity-90">{title}</div>
          <div className="text-[11px] opacity-70">{label}</div>
        </div>
        {icon && <div className={iconClasses}>{icon}</div>}
      </div>
      <div className="text-2xl font-bold">{value}</div>
      {change && (
        <div className="flex items-center gap-1 text-[11px] font-semibold">
          <span className={positive ? 'text-emerald-300' : 'text-orange-400'}>
            {positive ? (
              <TrendingUp className="inline h-3 w-3" />
            ) : (
              <TrendingDown className="inline h-3 w-3" />
            )}{' '}
            {change}
          </span>
          <span className="opacity-70">From last month</span>
        </div>
      )}
    </div>
  )
}
