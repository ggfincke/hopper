// src/features/dashboard/components/layout/DashboardSidebar.tsx
// Renders persistent navigation rail w/ branding & quick links
import { useTheme } from '../../../../hooks/useTheme'
import { Logo } from '../../../../components/ui/Logo'
import { cn } from '../../../../lib/utils'
import type { NavItemData } from '../../types'
import { NavItem } from '../leaf/NavItem'

interface DashboardSidebarProps {
  navItems: NavItemData[]
  className?: string
}

// * DashboardSidebar stacks logo, nav list & sticky spacing
export function DashboardSidebar({ navItems, className }: DashboardSidebarProps) {
  const { isDark } = useTheme()

  // flip background palette per theme
  const sidebarClasses = cn(
    'flex w-64 flex-col justify-between border-r px-6 py-8',
    isDark
      ? 'border-slate-900 bg-[#040815]/95 text-slate-200'
      : 'border-slate-200 bg-white text-slate-800',
    className
  )

  return (
    <aside className={sidebarClasses}>
      <div className="flex flex-col gap-6">
        <Logo />
        {/* nav links */}
        <nav className="flex flex-col gap-1.5">
          {navItems.map((item) => (
            <NavItem
              key={item.label}
              icon={item.icon}
              label={item.label}
              active={item.active}
            />
          ))}
        </nav>
      </div>
    </aside>
  )
}
