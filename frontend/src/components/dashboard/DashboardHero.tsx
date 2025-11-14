import { useTheme } from '../../hooks/useTheme'
import { cn } from '../../lib/utils'

interface DashboardHeroProps {
  userName: string
  subtitle?: string
}

/**
 * DashboardHero displays welcome message and overview subtitle.
 * Highlights user's name with accent color based on theme.
 *
 * @param userName - User's first name to personalize greeting
 * @param subtitle - Optional subtitle text (defaults to sales overview)
 */
export function DashboardHero({
  userName,
  subtitle = "Here's your current multi-channel sales overview.",
}: DashboardHeroProps) {
  const { isDark } = useTheme()

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
