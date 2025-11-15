// src/app/providers/AppProviders.tsx
// Wraps feature tree w/ ThemeProvider defaulted to dark
import { type ReactNode } from 'react'
import { ThemeProvider } from './ThemeProvider'

// children slot for provider composition
type AppProvidersProps = {
  children: ReactNode
}

// * App level provider bridge w/ theme defaults
export function AppProviders({ children }: AppProvidersProps) {
  return <ThemeProvider defaultTheme>{children}</ThemeProvider>
}
