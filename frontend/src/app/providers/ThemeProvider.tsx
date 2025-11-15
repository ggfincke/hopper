import { useState, type ReactNode } from 'react'
import { ThemeContext } from './ThemeContext'

interface ThemeProviderProps {
  children: ReactNode
  defaultTheme?: boolean
}

/**
 * ThemeProvider manages global theme state (light/dark mode) for the application.
 * Eliminates props drilling by making theme accessible via useTheme hook.
 *
 * @example
 * <ThemeProvider defaultTheme={true}>
 *   <App />
 * </ThemeProvider>
 */
export function ThemeProvider({ children, defaultTheme = true }: ThemeProviderProps) {
  const [isDark, setIsDark] = useState(defaultTheme)

  const toggleTheme = () => setIsDark((prev) => !prev)
  const setTheme = (dark: boolean) => setIsDark(dark)

  return (
    <ThemeContext.Provider value={{ isDark, toggleTheme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  )
}
