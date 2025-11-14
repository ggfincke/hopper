import { type ButtonHTMLAttributes, type ReactNode } from 'react'
import { useTheme } from '../../hooks/useTheme'
import { cn } from '../../lib/utils'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode
  variant?: 'primary' | 'secondary' | 'ghost' | 'icon'
  size?: 'sm' | 'md' | 'lg'
}

/**
 * Reusable Button component with theme-aware styling and multiple variants.
 * Automatically adapts colors based on theme context (light/dark mode).
 *
 * @param variant - Visual style: 'primary' (filled), 'secondary' (outlined), 'ghost' (minimal), 'icon' (circular)
 * @param size - Button size: 'sm', 'md', 'lg'
 */
export function Button({
  children,
  variant = 'primary',
  size = 'md',
  className,
  ...props
}: ButtonProps) {
  const { isDark } = useTheme()

  const baseClasses = 'inline-flex items-center justify-center font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed'

  const variantClasses = {
    primary: isDark
      ? 'bg-indigo-600 text-white hover:bg-indigo-700 active:bg-indigo-800'
      : 'bg-indigo-600 text-white hover:bg-indigo-700 active:bg-indigo-800',
    secondary: isDark
      ? 'border border-slate-700 bg-slate-900/70 text-slate-200 hover:bg-slate-800'
      : 'border border-slate-300 bg-white text-slate-700 hover:bg-slate-50',
    ghost: isDark
      ? 'text-slate-300 hover:bg-slate-800 hover:text-slate-100'
      : 'text-slate-700 hover:bg-slate-100 hover:text-slate-900',
    icon: isDark
      ? 'rounded-full bg-slate-900/80 text-slate-200 hover:bg-slate-800'
      : 'rounded-full bg-white text-slate-700 hover:bg-slate-50',
  }

  const sizeClasses = {
    sm: variant === 'icon' ? 'h-8 w-8 text-sm' : 'px-3 py-1.5 text-sm rounded-md',
    md: variant === 'icon' ? 'h-9 w-9 text-base' : 'px-4 py-2 text-sm rounded-lg',
    lg: variant === 'icon' ? 'h-10 w-10 text-lg' : 'px-6 py-3 text-base rounded-lg',
  }

  return (
    <button
      className={cn(baseClasses, variantClasses[variant], sizeClasses[size], className)}
      {...props}
    >
      {children}
    </button>
  )
}
