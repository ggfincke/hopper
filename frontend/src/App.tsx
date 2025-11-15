// src/App.tsx
// Root shell that wires providers & dashboard entry
import { AppProviders } from './app/providers/AppProviders'
import { DashboardPage } from './features/dashboard'

// * App root component w/ providers
export default function App() {
  return (
    <AppProviders>
      <DashboardPage />
    </AppProviders>
  )
}
