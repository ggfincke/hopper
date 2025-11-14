import { Badge } from '../ui/Badge'

interface StatusBadgeProps {
  status: string
  tone: 'indigo' | 'emerald'
}

/**
 * StatusBadge displays order status with appropriate color coding.
 * Maps status tone to Badge variant for consistent styling.
 *
 * @param status - Status text to display (e.g., "Processing", "Completed")
 * @param tone - Color tone: 'indigo' for processing, 'emerald' for completed
 */
export function StatusBadge({ status, tone }: StatusBadgeProps) {
  return <Badge variant={tone}>{status}</Badge>
}
