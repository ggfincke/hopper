import { type ReactNode } from 'react'
import { useTheme } from '../../hooks/useTheme'
import { cn } from '../../lib/utils'

interface DashboardLayoutProps {
  sidebar: ReactNode
  children: ReactNode
}

/**
 * DashboardLayout provides the main application layout structure.
 * Features a fixed sidebar and scrollable main content area with theme-aware background.
 *
 * @param sidebar - Sidebar component (typically DashboardSidebar)
 * @param children - Main content area components
 */
export function DashboardLayout({ sidebar, children }: DashboardLayoutProps) {
  const { isDark } = useTheme()

  const rootClasses = cn(
    'min-h-screen w-full font-sans',
    isDark
      ? 'bg-[radial-gradient(circle_at_top,_#050914,_#020617,_#01030a)] text-slate-100'
      : 'bg-gradient-to-br from-slate-100 via-white to-slate-200 text-slate-900'
  )

  return (
    <div className={rootClasses}>
      <div className="flex min-h-screen w-full overflow-hidden">
        {sidebar}
        <main className="flex flex-1 flex-col gap-6 overflow-x-hidden px-4 py-6 lg:px-10">
          {children}
        </main>
      </div>
    </div>
  )
}
