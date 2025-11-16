// src/hooks/useAuth.ts
// Convenience hook to consume AuthContext safely
import { useContext } from 'react'
import { AuthContext } from '../features/auth/AuthContext'

export function useAuth() {
  const context = useContext(AuthContext)

  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }

  return context
}
