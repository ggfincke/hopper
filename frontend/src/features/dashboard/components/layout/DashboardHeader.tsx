// src/features/dashboard/components/layout/DashboardHeader.tsx
// Header row aligning search & profile actions
import { SearchBar } from '../leaf/SearchBar'
import { UserProfile } from '../leaf/UserProfile'

interface DashboardHeaderProps {
  userName: string
  userEmail: string
  userInitials: string
  onSignOut?: () => void | Promise<void>
  signingOut?: boolean
}

// * DashboardHeader pairs search entry & account menu
export function DashboardHeader({
  userName,
  userEmail,
  userInitials,
  onSignOut,
  signingOut,
}: DashboardHeaderProps) {
  return (
    <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
      <SearchBar />
      <UserProfile
        name={userName}
        email={userEmail}
        initials={userInitials}
        onSignOut={onSignOut}
        signingOut={signingOut}
      />
    </div>
  )
}
