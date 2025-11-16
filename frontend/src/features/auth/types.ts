// src/features/auth/types.ts
// Shared auth contracts for API responses & context state
type UUID = string

// full user profile & security metadata from /api/auth/me endpoint
export interface AuthenticatedUser {
  id: UUID
  username: string
  email: string | null
  enabled: boolean
  accountLocked: boolean
  failedLoginAttempts: number
  roles: string[]
  createdAt: string
  updatedAt: string
}

// auth tokens & embedded user snapshot from login/register responses
export interface AuthResponse {
  accessToken: string
  refreshToken: string | null
  tokenType: string
  expiresIn: number
  refreshExpiresIn: number
  user: {
    id: UUID
    username: string
    email: string | null
    roles: string[]
    enabled: boolean
    accountLocked: boolean
    lastLogin: string
  }
}

// credentials & preferences submitted by login form
export interface LoginPayload {
  usernameOrEmail: string
  password: string
  rememberMe: boolean
}

// new user details & preferences for account creation
export interface RegisterPayload {
  username: string
  email: string
  password: string
  rememberMe: boolean
}
