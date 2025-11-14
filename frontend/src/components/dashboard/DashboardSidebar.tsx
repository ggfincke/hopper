import { useTheme } from '../../hooks/useTheme'
import { Logo } from '../ui/Logo'
import { NavItem } from './NavItem'
import { cn } from '../../lib/utils'
import type { NavItemData } from '../../lib/dashboardData'

interface DashboardSidebarProps {
  navItems: NavItemData[]
  className?: string
}

/**
 * DashboardSidebar displays brand logo and navigation menu.
 * Automatically adapts styling based on theme (light/dark mode).
 *
 * @param navItems - Array of navigation items with icon, label, and active state
 */
export function DashboardSidebar({ navItems, className }: DashboardSidebarProps) {
  const { isDark } = useTheme()

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
