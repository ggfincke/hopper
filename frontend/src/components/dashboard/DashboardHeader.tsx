import { SearchBar } from './SearchBar'
import { UserProfile } from './UserProfile'

interface DashboardHeaderProps {
  userName: string
  userEmail: string
  userInitials: string
}

/**
 * DashboardHeader contains search bar and user profile quick menu.
 * Composes the top-level controls for the dashboard layout.
 *
 * @param userName - User's full name for profile display
 * @param userEmail - User's email for profile display
 * @param userInitials - User's initials for avatar
 */
export function DashboardHeader({ userName, userEmail, userInitials }: DashboardHeaderProps) {
  return (
    <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
      <SearchBar />
      <UserProfile name={userName} email={userEmail} initials={userInitials} />
    </div>
  )
}
