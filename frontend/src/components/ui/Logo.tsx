import { cn } from '../../lib/utils'

interface LogoProps {
  size?: 'sm' | 'md' | 'lg'
  showText?: boolean
  className?: string
}

/**
 * Hopper brand logo with conic gradient icon.
 * Can be displayed with or without text label.
 *
 * @param size - Logo size: 'sm', 'md', 'lg'
 * @param showText - Whether to show "Hopper" text beside icon
 */
export function Logo({ size = 'md', showText = true, className }: LogoProps) {
  const sizeClasses = {
    sm: 'h-7 w-7 text-sm',
    md: 'h-9 w-9 text-lg',
    lg: 'h-12 w-12 text-2xl',
  }

  const textSizeClasses = {
    sm: 'text-base',
    md: 'text-lg',
    lg: 'text-xl',
  }

  return (
    <div className={cn('flex items-center gap-3', className)}>
      <div
        className={cn(
          'flex items-center justify-center rounded-full font-bold text-white',
          sizeClasses[size]
        )}
        style={{
          background:
            'conic-gradient(from 180deg at 50% 50%, #22c55e, #6366f1, #ec4899, #22c55e)',
        }}
      >
        H
      </div>
      {showText && <span className={cn('font-semibold', textSizeClasses[size])}>Hopper</span>}
    </div>
  )
}
