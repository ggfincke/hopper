import { Bell } from 'lucide-react'

/**
 * NotificationButton displays notification bell icon.
 * Can be extended to show notification count badge.
 */
export function NotificationButton() {
  return (
    <button
      className="flex h-9 w-9 items-center justify-center rounded-full bg-amber-100 text-amber-700 transition-colors hover:bg-amber-200"
      aria-label="View notifications"
    >
      <Bell className="h-5 w-5" />
    </button>
  )
}
