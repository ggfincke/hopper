// src/app/providers/ThemeProvider.tsx
// Manages global dark/light mode & exposes controls via context
import { useState, type ReactNode } from 'react'
import { ThemeContext } from './ThemeContext'

interface ThemeProviderProps {
  children: ReactNode
  defaultTheme?: boolean
}

// * ThemeProvider wires children to ThemeContext value
export function ThemeProvider({ children, defaultTheme = true }: ThemeProviderProps) {
  const [isDark, setIsDark] = useState(defaultTheme)

  // flip theme flag
  const toggleTheme = () => setIsDark((prev) => !prev)
  // force theme flag from callers
  const setTheme = (dark: boolean) => setIsDark(dark)

  return (
    <ThemeContext.Provider value={{ isDark, toggleTheme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  )
}
