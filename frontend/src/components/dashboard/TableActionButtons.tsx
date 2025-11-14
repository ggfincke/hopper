import { useTheme } from '../../hooks/useTheme'
import { cn } from '../../lib/utils'

/**
 * TableActionButtons displays action buttons for the orders table.
 * Includes Customize, Filter, and Export options with theme-aware styling.
 */
export function TableActionButtons() {
  const { isDark } = useTheme()

  const customizeClasses = cn(
    'rounded-full px-3 py-1 text-[11px] font-semibold transition-colors',
    isDark
      ? 'border border-slate-500/60 bg-transparent text-slate-200 hover:border-slate-300/60'
      : 'border border-slate-300 bg-white text-slate-700 hover:border-slate-400'
  )

  const filterClasses = cn(
    'rounded-full px-3 py-1 text-[11px] font-semibold transition-colors',
    isDark
      ? 'border border-indigo-500/70 text-slate-100 hover:border-indigo-400'
      : 'border border-slate-300 bg-indigo-50 text-slate-800 hover:border-indigo-300'
  )

  return (
    <div className="flex items-center gap-2">
      <button className={customizeClasses}>Customize</button>
      <button className={filterClasses}>Filter</button>
      <button className="rounded-full bg-[linear-gradient(135deg,_#6366f1,_#4f46e5,_#1d4ed8)] px-3 py-1 text-[11px] font-semibold text-white shadow-[0_10px_30px_rgba(79,70,229,0.45)]">
        Export
      </button>
    </div>
  )
}
