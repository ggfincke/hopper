// src/app/providers/ThemeContext.ts
// Defines shape for theme control state & creates context shell
import { createContext } from 'react'

// * ThemeContextValue defines dark mode state & toggle actions for useTheme
export interface ThemeContextValue {
  isDark: boolean
  toggleTheme: () => void
  setTheme: (isDark: boolean) => void
}

// expose undefined default to assert provider usage
export const ThemeContext = createContext<ThemeContextValue | undefined>(undefined)
