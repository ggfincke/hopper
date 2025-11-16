// src/app/providers/ThemeProvider.tsx
// Manages global dark/light mode & exposes controls via context
import { useEffect, useState, type ReactNode } from 'react'
import { ThemeContext } from './ThemeContext'

interface ThemeProviderProps {
  children: ReactNode
  defaultTheme?: boolean
}

const THEME_KEY = 'hopper.theme.isDark'

// read theme from localStorage w/ SSR safety & fallback
const readStoredTheme = (fallback: boolean): boolean => {
  if (typeof window === 'undefined') {
    return fallback
  }

  const stored = window.localStorage.getItem(THEME_KEY)
  return stored !== null ? stored === 'true' : fallback
}

// persist theme to localStorage & sync w/ HTML element
const applyTheme = (isDark: boolean) => {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.setItem(THEME_KEY, String(isDark))

  if (isDark) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

// * ThemeProvider wires children to ThemeContext value
export function ThemeProvider({ children, defaultTheme = true }: ThemeProviderProps) {
  const [isDark, setIsDark] = useState(() => readStoredTheme(defaultTheme))

  // sync HTML element when theme changes
  useEffect(() => {
    applyTheme(isDark)
  }, [isDark])

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
