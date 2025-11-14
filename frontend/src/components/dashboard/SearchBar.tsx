import { Search, Mic } from 'lucide-react'
import { useTheme } from '../../hooks/useTheme'
import { Input } from '../ui/Input'
import { cn } from '../../lib/utils'

interface SearchBarProps {
  placeholder?: string
  className?: string
}

/**
 * SearchBar component with search icon, input field, and voice search button.
 * Themed styling adapts to light/dark mode via useTheme hook.
 *
 * @param placeholder - Input placeholder text
 */
export function SearchBar({
  placeholder = 'Search products, orders, customers...',
  className,
}: SearchBarProps) {
  const { isDark } = useTheme()

  const containerClasses = cn(
    'flex flex-1 max-w-xl items-center gap-3 rounded-full px-4 py-2 text-sm transition-shadow',
    isDark
      ? 'bg-slate-900/80 text-slate-100 shadow-[0_0_0_1px_rgba(15,23,42,0.85)]'
      : 'bg-white text-slate-800 shadow-[0_1px_0_rgba(148,163,184,0.35)]',
    className
  )

  const micButtonClasses = cn(
    'flex h-8 w-8 items-center justify-center rounded-full text-base transition-colors',
    isDark ? 'bg-slate-800 text-slate-200' : 'bg-indigo-50 text-slate-700'
  )

  return (
    <div className={containerClasses}>
      <Search className="h-4 w-4 opacity-70" />
      <Input variant="search" placeholder={placeholder} />
      <button className={micButtonClasses} aria-label="Voice search">
        <Mic className="h-4 w-4" />
      </button>
    </div>
  )
}
