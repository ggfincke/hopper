// src/features/dashboard/components/leaf/UserProfile.tsx
// User profile chip w/ hover/focus dropdown for quick actions
import { type FocusEvent, useEffect, useRef, useState } from 'react'
import { LogOut, Moon, Settings, Sparkles, Sun } from 'lucide-react'
import { useTheme } from '../../../../hooks/useTheme'
import { Avatar } from '../../../../components/ui/Avatar'
import { cn } from '../../../../lib/utils'

// allow slight hover forgiveness before hiding
const CLOSE_MENU_DELAY = 100

interface UserProfileProps {
  name: string
  email: string
  initials: string
  className?: string
  onSignOut?: () => void | Promise<void>
  signingOut?: boolean
}

// * UserProfile shows avatar summary & reveals dropdown actions
export function UserProfile({ name, email, initials, className, onSignOut, signingOut }: UserProfileProps) {
  const { isDark, toggleTheme } = useTheme()
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const closeTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  // open dropdown immediately
  const openMenu = () => {
    if (closeTimeoutRef.current) {
      clearTimeout(closeTimeoutRef.current)
    }
    setIsMenuOpen(true)
  }

  // delay close so focus can move inside menu
  const scheduleClose = () => {
    if (closeTimeoutRef.current) {
      clearTimeout(closeTimeoutRef.current)
    }
    closeTimeoutRef.current = setTimeout(() => setIsMenuOpen(false), CLOSE_MENU_DELAY)
  }

  // close when focus leaves wrapper
  const handleBlur = (event: FocusEvent<HTMLDivElement>) => {
    if (!event.currentTarget.contains(event.relatedTarget)) {
      scheduleClose()
    }
  }

  // cleanup on unmount
  useEffect(
    () => () => {
      if (closeTimeoutRef.current) {
        clearTimeout(closeTimeoutRef.current)
      }
    },
    []
  )

  const profileClasses = cn(
    'flex items-center gap-3 rounded-full px-3 py-1 text-xs transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500/50',
    isDark
      ? 'border border-indigo-700/70 bg-slate-900/70 text-white'
      : 'border border-slate-300 bg-white text-slate-700',
    className
  )

  const accentText = isDark ? 'text-slate-400' : 'text-slate-600'
  // floating menu container styles
  const dropdownClasses = cn(
    'absolute right-0 top-full z-20 mt-2 w-60 rounded-2xl border p-2 text-sm shadow-xl transition-all duration-150',
    isDark
      ? 'border-slate-800/70 bg-slate-950/95 text-white shadow-black/40'
      : 'border-slate-200 bg-white text-slate-700 shadow-slate-900/5'
  )

  // button styling shared by dropdown actions
  const menuItemClasses = cn(
    'flex w-full items-center justify-between rounded-xl px-3 py-2 text-left text-xs font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500/40 disabled:cursor-not-allowed disabled:opacity-50',
    isDark ? 'hover:bg-white/5' : 'hover:bg-slate-50'
  )

  const handleSignOut = () => {
    setIsMenuOpen(false)
    onSignOut?.()
  }

  return (
    <div
      className="relative inline-flex"
      onMouseEnter={openMenu}
      onMouseLeave={scheduleClose}
      onFocus={openMenu}
      onBlur={handleBlur}
    >
      <div
        tabIndex={0}
        role="button"
        aria-haspopup="menu"
        aria-expanded={isMenuOpen}
        className={cn(profileClasses, 'cursor-pointer select-none')}
      >
        <Avatar initials={initials} size="sm" />
        <div>
          <div className="text-xs font-semibold leading-tight">{name}</div>
          <div className={cn('text-[11px] leading-tight', accentText)}>{email}</div>
        </div>
      </div>

      <div
        onMouseEnter={openMenu}
        onMouseLeave={scheduleClose}
        className={cn(
          dropdownClasses,
          isMenuOpen
            ? 'pointer-events-auto visible translate-y-0 opacity-100'
            : 'pointer-events-none invisible translate-y-2 opacity-0'
        )}
      >
        <button type="button" onClick={toggleTheme} className={menuItemClasses}>
          <div className="flex items-center gap-3">
            {isDark ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
            <div>
              <p className="text-xs font-semibold">Night mode</p>
              <p className={cn('text-[11px] font-normal', accentText)}>Switch between light and dark</p>
            </div>
          </div>
          <span className="text-[10px] font-semibold uppercase tracking-wide text-indigo-500">
            {isDark ? 'On' : 'Off'}
          </span>
        </button>

        <button type="button" className={menuItemClasses}>
          <div className="flex items-center gap-3">
            <Settings className="h-4 w-4" />
            <div>
              <p className="text-xs font-semibold">Settings</p>
              <p className={cn('text-[11px] font-normal', accentText)}>Account & security preferences</p>
            </div>
          </div>
        </button>

        <button type="button" className={menuItemClasses}>
          <div className="flex items-center gap-3">
            <Sparkles className="h-4 w-4" />
            <div>
              <p className="text-xs font-semibold">App updates</p>
              <p className={cn('text-[11px] font-normal', accentText)}>See what&apos;s new in Hopper</p>
            </div>
          </div>
        </button>

        <div className={cn('mt-2 border-t pt-2', isDark ? 'border-white/10' : 'border-slate-100')}>
          <button type="button" onClick={handleSignOut} className={menuItemClasses} disabled={signingOut}>
            <div className="flex items-center gap-3">
              <LogOut className="h-4 w-4" />
              <div>
                <p className="text-xs font-semibold">{signingOut ? 'Signing out…' : 'Sign out'}</p>
                <p className={cn('text-[11px] font-normal', accentText)}>Exit the dashboard safely</p>
              </div>
            </div>
          </button>
        </div>
      </div>
    </div>
  )
}
