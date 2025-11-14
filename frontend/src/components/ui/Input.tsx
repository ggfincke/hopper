import { type InputHTMLAttributes, forwardRef } from 'react'
import { useTheme } from '../../hooks/useTheme'
import { cn } from '../../lib/utils'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  variant?: 'default' | 'search'
}

/**
 * Reusable Input component with theme-aware styling.
 * Supports standard text input and search-specific styling.
 *
 * @param variant - Input style: 'default' or 'search'
 */
export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ variant = 'default', className, ...props }, ref) => {
    const { isDark } = useTheme()

    const baseClasses = 'w-full bg-transparent text-sm transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-500'

    const variantClasses = {
      default: isDark
        ? 'rounded-lg border border-slate-700 bg-slate-900 px-4 py-2 text-slate-100 placeholder:text-slate-500'
        : 'rounded-lg border border-slate-300 bg-white px-4 py-2 text-slate-900 placeholder:text-slate-500',
      search: isDark
        ? 'text-slate-100 placeholder:text-slate-500'
        : 'text-slate-900 placeholder:text-slate-500',
    }

    return (
      <input
        ref={ref}
        className={cn(baseClasses, variantClasses[variant], className)}
        {...props}
      />
    )
  }
)

Input.displayName = 'Input'
