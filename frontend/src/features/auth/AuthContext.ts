// src/features/auth/AuthContext.ts
// Context shell describing authentication state contract
import { createContext } from 'react'
import type { AuthenticatedUser, LoginPayload, RegisterPayload } from './types'

// * AuthContextValue defines shape of auth state & actions consumed by useAuth
export interface AuthContextValue {
  user: AuthenticatedUser | null
  token: string | null
  isLoading: boolean
  login: (payload: LoginPayload) => Promise<void>
  register: (payload: RegisterPayload) => Promise<void>
  logout: () => Promise<void>
}

// expose undefined default to enforce provider boundary in useAuth
export const AuthContext = createContext<AuthContextValue | undefined>(undefined)
