// src/hooks/useTheme.ts
// Custom hook to read ThemeContext & guard for provider usage
import { useContext } from 'react'
import { ThemeContext } from '../app/providers/ThemeContext'

// * Access shared theme state & controls
export function useTheme() {
  const context = useContext(ThemeContext)

  // ensure hook stays inside ThemeProvider boundary
  if (context === undefined) {
    throw new Error('useTheme must be used within a ThemeProvider')
  }

  return context
}
