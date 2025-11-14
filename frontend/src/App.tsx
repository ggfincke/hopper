import { ThemeProvider } from './context/ThemeProvider'
import { DashboardPage } from './pages/dashboard/DashboardPage'

/**
 * App root component.
 * Wraps application with ThemeProvider to enable global theme management.
 */
export default function App() {
  return (
    <ThemeProvider defaultTheme={true}>
      <DashboardPage />
    </ThemeProvider>
  )
}
