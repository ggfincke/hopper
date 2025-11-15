// src/components/ui/Avatar.tsx
// Circle avatar showing initials w/ gradient fill
import { cn } from '../../lib/utils'

// allow gradient & sizing overrides
interface AvatarProps {
  initials: string
  size?: 'sm' | 'md' | 'lg'
  gradient?: string
  className?: string
}

// * Avatar renders initials chip for quick identity cues
export function Avatar({ initials, size = 'md', gradient, className }: AvatarProps) {
  const sizeClasses = {
    sm: 'h-8 w-8 text-sm',
    md: 'h-10 w-10 text-base',
    lg: 'h-12 w-12 text-lg',
  }

  const defaultGradient = 'linear-gradient(135deg, #fb7185, #f97316, #22c55e)'

  return (
    <div
      className={cn(
        'flex items-center justify-center rounded-full font-bold text-white',
        sizeClasses[size],
        className
      )}
      style={{ background: gradient || defaultGradient }}
    >
      {initials.toUpperCase().slice(0, 2)}
    </div>
  )
}
