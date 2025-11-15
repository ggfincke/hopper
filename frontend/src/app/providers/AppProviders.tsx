import { type ReactNode } from 'react'
import { ThemeProvider } from './ThemeProvider'

type AppProvidersProps = {
  children: ReactNode
}

export function AppProviders({ children }: AppProvidersProps) {
  return <ThemeProvider defaultTheme>{children}</ThemeProvider>
}
