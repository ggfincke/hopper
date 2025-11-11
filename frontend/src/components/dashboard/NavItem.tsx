type NavItemProps = {
  icon: string
  label: string
  active?: boolean
  isDark: boolean
}

export function NavItem({ icon, label, active, isDark }: NavItemProps) {
  const buttonClasses = [
    'flex w-full items-center gap-2 rounded-xl px-3 py-2 text-sm transition-colors',
    active
      ? isDark
        ? 'bg-indigo-500 text-white shadow-[0_10px_30px_rgba(79,70,229,0.45)]'
        : 'bg-slate-900 text-white'
      : isDark
        ? 'text-slate-300 hover:bg-white/5'
        : 'text-slate-700 hover:bg-slate-100',
    active ? 'font-semibold' : 'font-medium',
  ]

  const iconClasses = [
    'flex h-7 w-7 items-center justify-center rounded-full text-[15px]',
    active
      ? 'bg-white/15 text-white'
      : isDark
        ? 'bg-white/10 text-slate-300'
        : 'bg-slate-200 text-slate-700',
  ]

  return (
    <button className={buttonClasses.join(' ')}>
      <span className={iconClasses.join(' ')}>{icon}</span>
      <span>{label}</span>
    </button>
  )
}
