import { type ReactNode } from 'react'
import { cn } from '../../lib/utils'

type PageProps = {
  children: ReactNode
  className?: string
}

export function Page({ children, className }: PageProps) {
  return <div className={cn('mx-auto flex w-full max-w-7xl flex-col gap-6', className)}>{children}</div>
}

type PageHeaderProps = {
  title?: ReactNode
  description?: ReactNode
  actions?: ReactNode
  children?: ReactNode
  className?: string
}

export function PageHeader({ title, description, actions, children, className }: PageHeaderProps) {
  return (
    <header className={cn('flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between', className)}>
      <div className="flex-1 space-y-1.5">
        {title &&
          (typeof title === 'string' ? (
            <h1 className="text-2xl font-bold tracking-tight text-slate-900">{title}</h1>
          ) : (
            title
          ))}
        {description &&
          (typeof description === 'string' ? (
            <p className="text-sm text-slate-500">{description}</p>
          ) : (
            description
          ))}
        {children}
      </div>
      {actions ? <div className="shrink-0">{actions}</div> : null}
    </header>
  )
}

type PageContentProps = {
  children: ReactNode
  className?: string
}

export function PageContent({ children, className }: PageContentProps) {
  return <div className={cn('flex flex-col gap-6', className)}>{children}</div>
}

type PageSectionProps = {
  title?: ReactNode
  description?: ReactNode
  children: ReactNode
  className?: string
}

export function PageSection({ title, description, children, className }: PageSectionProps) {
  return (
    <section className={cn('space-y-3', className)}>
      {(title || description) && (
        <div className="space-y-1">
          {title &&
            (typeof title === 'string' ? (
              <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">{title}</h2>
            ) : (
              title
            ))}
          {description &&
            (typeof description === 'string' ? (
              <p className="text-sm text-slate-500">{description}</p>
            ) : (
              description
            ))}
        </div>
      )}
      {children}
    </section>
  )
}
