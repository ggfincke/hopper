import { SearchBar } from './SearchBar'
import { ThemeToggle } from './ThemeToggle'
import { NotificationButton } from './NotificationButton'
import { UserProfile } from './UserProfile'

interface DashboardHeaderProps {
  userName: string
  userEmail: string
  userInitials: string
}

/**
 * DashboardHeader contains search bar, theme toggle, notifications, and user profile.
 * Composes multiple smaller components into a cohesive header layout.
 *
 * @param userName - User's full name for profile display
 * @param userEmail - User's email for profile display
 * @param userInitials - User's initials for avatar
 */
export function DashboardHeader({ userName, userEmail, userInitials }: DashboardHeaderProps) {
  return (
    <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
      <SearchBar />
      <div className="flex items-center gap-3">
        <ThemeToggle />
        <NotificationButton />
        <UserProfile name={userName} email={userEmail} initials={userInitials} />
      </div>
    </div>
  )
}
