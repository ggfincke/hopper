// src/features/dashboard/components/sections/DashboardHero.tsx
// Welcome hero messaging block for dashboard top
import { useTheme } from '../../../../hooks/useTheme'
import { cn } from '../../../../lib/utils'

interface DashboardHeroProps {
  userName: string
  subtitle?: string
}

// * DashboardHero greets user & shows context line
export function DashboardHero({
  userName,
  subtitle = "Here's your current multi-channel sales overview.",
}: DashboardHeroProps) {
  const { isDark } = useTheme()

  // pick accent tone per theme
  const accentText = isDark ? 'text-slate-400' : 'text-slate-600'

  return (
    <div>
      <h1 className="text-2xl font-bold">
        Welcome back, <span className="text-indigo-400">{userName}</span>!
      </h1>
      <p className={cn('mt-1 text-xs', accentText)}>{subtitle}</p>
    </div>
  )
}
