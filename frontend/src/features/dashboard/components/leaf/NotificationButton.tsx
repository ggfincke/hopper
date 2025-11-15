// src/features/dashboard/components/leaf/NotificationButton.tsx
// Accent button for notification center entry
import { Bell } from 'lucide-react'

// * NotificationButton leaves room for count badge & future actions
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
