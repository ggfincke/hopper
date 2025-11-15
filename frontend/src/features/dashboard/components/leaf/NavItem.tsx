// src/features/dashboard/components/leaf/NavItem.tsx
// Sidebar nav button w/ icon & label
import { useTheme } from '../../../../hooks/useTheme'
import { cn } from '../../../../lib/utils'

type NavItemProps = {
  icon: string
  label: string
  active?: boolean
}

// * NavItem highlights current section & theme syncs background
export function NavItem({ icon, label, active }: NavItemProps) {
  const { isDark } = useTheme()

  const buttonClasses = cn(
    'flex w-full items-center gap-2 rounded-xl px-3 py-2 text-sm transition-colors',
    active
      ? isDark
        ? 'bg-indigo-500 text-white shadow-[0_10px_30px_rgba(79,70,229,0.45)]'
        : 'bg-slate-900 text-white'
      : isDark
        ? 'text-slate-300 hover:bg-white/5'
        : 'text-slate-700 hover:bg-slate-100',
    active ? 'font-semibold' : 'font-medium'
  )

  const iconClasses = cn(
    'flex h-7 w-7 items-center justify-center rounded-full text-[15px]',
    active
      ? 'bg-white/15 text-white'
      : isDark
        ? 'bg-white/10 text-slate-300'
        : 'bg-slate-200 text-slate-700'
  )

  return (
    <button className={buttonClasses}>
      <span className={iconClasses}>{icon}</span>
      <span>{label}</span>
    </button>
  )
}
