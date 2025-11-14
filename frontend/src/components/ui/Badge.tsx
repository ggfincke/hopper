import { type ReactNode } from 'react'
import { useTheme } from '../../hooks/useTheme'
import { cn } from '../../lib/utils'

interface BadgeProps {
  children: ReactNode
  variant?: 'default' | 'success' | 'warning' | 'error' | 'info' | 'indigo' | 'emerald'
  className?: string
}

/**
 * Badge component for status indicators, labels, and tags.
 * Automatically adapts styling based on theme (light/dark mode).
 *
 * @param variant - Color scheme: 'default', 'success', 'warning', 'error', 'info', 'indigo', 'emerald'
 */
export function Badge({ children, variant = 'default', className }: BadgeProps) {
  const { isDark } = useTheme()

  const baseClasses = 'inline-flex items-center justify-center rounded-full px-3 py-1 text-xs font-medium'

  const variantClasses = {
    default: isDark
      ? 'bg-slate-800 text-slate-200'
      : 'bg-slate-100 text-slate-700',
    success: isDark
      ? 'bg-emerald-900/50 text-emerald-300'
      : 'bg-emerald-100 text-emerald-700',
    warning: isDark
      ? 'bg-amber-900/50 text-amber-300'
      : 'bg-amber-100 text-amber-700',
    error: isDark
      ? 'bg-red-900/50 text-red-300'
      : 'bg-red-100 text-red-700',
    info: isDark
      ? 'bg-blue-900/50 text-blue-300'
      : 'bg-blue-100 text-blue-700',
    indigo: isDark
      ? 'bg-indigo-900/50 text-indigo-300'
      : 'bg-indigo-100 text-indigo-700',
    emerald: isDark
      ? 'bg-emerald-900/50 text-emerald-300'
      : 'bg-emerald-100 text-emerald-700',
  }

  return (
    <span className={cn(baseClasses, variantClasses[variant], className)}>
      {children}
    </span>
  )
}
