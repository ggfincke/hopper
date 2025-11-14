import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Merges Tailwind CSS classes intelligently, handling conflicts and conditional classes.
 * Uses clsx for conditional class handling and tailwind-merge to resolve Tailwind conflicts.
 *
 * @example
 * cn('px-2 py-1', isDark && 'bg-slate-900', { 'text-white': isActive })
 * // Returns merged class string with Tailwind conflicts resolved
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}
