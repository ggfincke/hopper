import { createContext } from 'react'

export interface ThemeContextValue {
  isDark: boolean
  toggleTheme: () => void
  setTheme: (isDark: boolean) => void
}

export const ThemeContext = createContext<ThemeContextValue | undefined>(undefined)
