// src/components/layout/Page.tsx
// Shared dashboard page primitives for consistent spacing & copy blocks
import { type ReactNode } from 'react'
import { cn } from '../../lib/utils'
import { useTheme } from '../../hooks/useTheme'

// standard layout container props
type PageProps = {
  children: ReactNode
  className?: string
}

// * Page centers the content column & enforces max width
export function Page({ children, className }: PageProps) {
  return <div className={cn('mx-auto flex w-full max-w-7xl flex-col gap-6', className)}>{children}</div>
}

// header block props for title/description/actions
type PageHeaderProps = {
  title?: ReactNode
  description?: ReactNode
  actions?: ReactNode
  children?: ReactNode
  className?: string
}

// * PageHeader aligns hero content & optional action cluster
export function PageHeader({ title, description, actions, children, className }: PageHeaderProps) {
  const { isDark } = useTheme()

  return (
    <header className={cn('flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between', className)}>
      <div className="flex-1 space-y-1.5">
        {title &&
          (typeof title === 'string' ? (
            <h1 className={cn('text-2xl font-bold tracking-tight', isDark ? 'text-white' : 'text-slate-900')}>
              {title}
            </h1>
          ) : (
            title
          ))}
        {description &&
          (typeof description === 'string' ? (
            <p className={cn('text-sm', isDark ? 'text-slate-400' : 'text-slate-500')}>{description}</p>
          ) : (
            description
          ))}
        {children}
      </div>
      {actions ? <div className="shrink-0">{actions}</div> : null}
    </header>
  )
}

// page body props for grid/list children
type PageContentProps = {
  children: ReactNode
  className?: string
}

// * PageContent stacks sections & accepts overrides
export function PageContent({ children, className }: PageContentProps) {
  return <div className={cn('flex flex-col gap-6', className)}>{children}</div>
}

// section block props for groupings
type PageSectionProps = {
  title?: ReactNode
  description?: ReactNode
  children: ReactNode
  className?: string
}

// * PageSection adds headings & subcopy for each grouping
export function PageSection({ title, description, children, className }: PageSectionProps) {
  const { isDark } = useTheme()

  return (
    <section className={cn('space-y-3', className)}>
      {(title || description) && (
        <div className="space-y-1">
          {title &&
            (typeof title === 'string' ? (
              <h2 className={cn('text-sm font-semibold uppercase tracking-wide', isDark ? 'text-slate-400' : 'text-slate-500')}>
                {title}
              </h2>
            ) : (
              title
            ))}
          {description &&
            (typeof description === 'string' ? (
              <p className={cn('text-sm', isDark ? 'text-slate-400' : 'text-slate-500')}>{description}</p>
            ) : (
              description
            ))}
        </div>
      )}
      {children}
    </section>
  )
}
