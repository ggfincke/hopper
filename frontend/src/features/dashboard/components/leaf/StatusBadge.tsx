// src/features/dashboard/components/leaf/StatusBadge.tsx
// Displays status chip using Badge variants
import { Badge } from '../../../../components/ui/Badge'

interface StatusBadgeProps {
  status: string
  tone: 'indigo' | 'emerald'
}

// * StatusBadge forwards props to Badge & keeps tone mapping consistent
export function StatusBadge({ status, tone }: StatusBadgeProps) {
  return <Badge variant={tone}>{status}</Badge>
}
