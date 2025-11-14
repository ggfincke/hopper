import { useTheme } from '../../hooks/useTheme'
import { Avatar } from '../ui/Avatar'
import { cn } from '../../lib/utils'

interface UserProfileProps {
  name: string
  email: string
  initials: string
  className?: string
}

/**
 * UserProfile displays user avatar and information in the header.
 * Can be extended to include dropdown menu for account actions.
 *
 * @param name - User's full name
 * @param email - User's email address
 * @param initials - User's initials for avatar
 */
export function UserProfile({ name, email, initials, className }: UserProfileProps) {
  const { isDark } = useTheme()

  const containerClasses = cn(
    'flex items-center gap-3 rounded-full px-3 py-1 text-xs',
    isDark
      ? 'border border-indigo-700/70 bg-slate-900/70 text-white'
      : 'border border-slate-300 bg-white text-slate-700',
    className
  )

  const accentText = isDark ? 'text-slate-400' : 'text-slate-600'

  return (
    <div className={containerClasses}>
      <Avatar initials={initials} size="sm" />
      <div>
        <div className="text-xs font-semibold leading-tight">{name}</div>
        <div className={cn('text-[11px] leading-tight', accentText)}>{email}</div>
      </div>
    </div>
  )
}
