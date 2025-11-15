// src/lib/utils.ts
// Utility helpers shared across UI & theme logic
import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

// merge Tailwind class lists & resolve conflicts
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}
