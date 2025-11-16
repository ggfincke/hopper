// src/app/layout/AppLayout.tsx
// Provides shell layout w/ themed background & sidebar slot
import { type ReactNode } from 'react'
import { cn } from '../../lib/utils'

interface AppLayoutProps {
  sidebar: ReactNode
  children: ReactNode
}

// * Layout wrapper that pairs sidebar & scrollable content
export function AppLayout({ sidebar, children }: AppLayoutProps) {
  const rootClasses = cn('min-h-screen w-full bg-transparent font-sans text-slate-900 dark:text-slate-100')

  return (
    <div className={rootClasses}>
      <div className="flex min-h-screen w-full overflow-hidden">
        {sidebar}
        {/* main content column */}
        <main className="flex flex-1 flex-col gap-6 overflow-x-hidden px-4 py-6 lg:px-10">
          {children}
        </main>
      </div>
    </div>
  )
}
