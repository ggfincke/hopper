import { useContext } from 'react'
import { ThemeContext } from '../context/ThemeContext'

/**
 * Custom hook to access theme state and controls from anywhere in the component tree.
 * Must be used within a ThemeProvider.
 *
 * @returns Theme context containing isDark, toggleTheme, and setTheme
 * @throws Error if used outside ThemeProvider
 *
 * @example
 * function MyComponent() {
 *   const { isDark, toggleTheme } = useTheme()
 *   return <button onClick={toggleTheme}>{isDark ? 'Light' : 'Dark'}</button>
 * }
 */
export function useTheme() {
  const context = useContext(ThemeContext)

  if (context === undefined) {
    throw new Error('useTheme must be used within a ThemeProvider')
  }

  return context
}
