// src/features/dashboard/components/leaf/ThemeToggle.tsx
// Icon button that flips color mode & syncs w/ context
import { Sun, Moon } from 'lucide-react'
import { useTheme } from '../../../../hooks/useTheme'
import { cn } from '../../../../lib/utils'

// * ThemeToggle invokes ThemeContext & swaps icons
export function ThemeToggle() {
  const { isDark, toggleTheme } = useTheme()

  return (
    <button
      onClick={toggleTheme}
      className={cn(
        'flex h-9 w-9 items-center justify-center rounded-full transition-colors',
        isDark ? 'bg-slate-100 text-slate-900' : 'bg-slate-900 text-slate-100'
      )}
      aria-label={`Switch to ${isDark ? 'light' : 'dark'} mode`}
    >
      {isDark ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
    </button>
  )
}
