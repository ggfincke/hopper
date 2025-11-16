// src/App.tsx
// Root shell that wires providers, routing & auth flow
import { type ReactNode } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AppProviders } from './app/providers/AppProviders'
import { DashboardPage } from './features/dashboard'
import { LoginPage } from './features/auth/LoginPage'
import { RegisterPage } from './features/auth/RegisterPage'
import { useAuth } from './hooks/useAuth'
import { useTheme } from './hooks/useTheme'
import { cn } from './lib/utils'

// * App root component w/ providers
export default function App() {
  return (
    <AppProviders>
      <BrowserRouter>
        <AppRouter />
      </BrowserRouter>
    </AppProviders>
  )
}

function AppRouter() {
  const { isLoading, user } = useAuth()

  if (isLoading) {
    return <AuthLoader />
  }

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage />} />
      <Route path="/register" element={user ? <Navigate to="/" replace /> : <RegisterPage />} />
      <Route
        path="/*"
        element={
          <RequireAuth>
            <DashboardPage />
          </RequireAuth>
        }
      />
    </Routes>
  )
}

function RequireAuth({ children }: { children: ReactNode }) {
  const { user } = useAuth()

  if (!user) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

function AuthLoader() {
  const { isDark } = useTheme()

  return (
    <div
      className={cn(
        'flex min-h-screen w-full items-center justify-center bg-transparent px-4',
        isDark ? 'text-white' : 'text-slate-600'
      )}
    >
      <div className="space-y-2 text-center">
        <div className="h-3 w-3 animate-ping rounded-full bg-indigo-500 mx-auto" />
        <p className="text-sm font-medium tracking-wide text-indigo-500">Loading your workspace…</p>
      </div>
    </div>
  )
}
