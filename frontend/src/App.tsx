import { AppProviders } from './app/providers/AppProviders'
import { DashboardPage } from './features/dashboard'

/**
 * App root component.
 * Wraps application with ThemeProvider to enable global theme management.
 */
export default function App() {
  return (
    <AppProviders>
      <DashboardPage />
    </AppProviders>
  )
}
