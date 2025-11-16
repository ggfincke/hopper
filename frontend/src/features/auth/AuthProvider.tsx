// src/features/auth/AuthProvider.tsx
// Provides authenticated user state & helpers to the app tree
import { type ReactNode, useCallback, useEffect, useMemo, useState } from 'react'
import { AuthContext } from './AuthContext'
import { fetchCurrentUser, loginRequest, logoutRequest, registerRequest } from './api'
import type { AuthenticatedUser, AuthResponse, LoginPayload, RegisterPayload } from './types'

const TOKEN_STORAGE_KEY = 'hopper.auth.accessToken'

// retrieve token from localStorage w/ SSR safety guard
const readStoredToken = () => {
  if (typeof window === 'undefined') {
    return null
  }

  return window.localStorage.getItem(TOKEN_STORAGE_KEY)
}

// save or clear token from localStorage w/ SSR safety guard
const persistToken = (value: string | null) => {
  if (typeof window === 'undefined') {
    return
  }

  if (value) {
    window.localStorage.setItem(TOKEN_STORAGE_KEY, value)
  } else {
    window.localStorage.removeItem(TOKEN_STORAGE_KEY)
  }
}

interface AuthProviderProps {
  children: ReactNode
}

const mapAuthResponseUser = (authResponse: AuthResponse): AuthenticatedUser | null => {
  const payload = authResponse.user

  if (!payload) {
    return null
  }

  const timestamp = new Date().toISOString()
  return {
    id: payload.id,
    username: payload.username,
    email: payload.email,
    enabled: payload.enabled,
    accountLocked: payload.accountLocked,
    failedLoginAttempts: 0,
    roles: payload.roles,
    createdAt: timestamp,
    updatedAt: timestamp,
  }
}

// * AuthProvider manages auth state & exposes login/register/logout actions
export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<AuthenticatedUser | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  // populate user state from auth response payload without extra network round-trip
  const hydrateSession = useCallback((authResponse: AuthResponse) => {
    persistToken(authResponse.accessToken)
    setToken(authResponse.accessToken)
    const nextUser = mapAuthResponseUser(authResponse)
    setUser(nextUser)
  }, [])

  // restore session from localStorage on mount
  useEffect(() => {
    let isMounted = true

    async function bootstrap() {
      const storedToken = readStoredToken()

      if (!storedToken) {
        if (isMounted) {
          setIsLoading(false)
        }
        return
      }

      try {
        const currentUser = await fetchCurrentUser(storedToken)
        if (isMounted) {
          setToken(storedToken)
          setUser(currentUser)
        }
      } catch (error) {
        // clear invalid token on validation failure
        console.error('Failed to restore auth session:', error)
        persistToken(null)
        if (isMounted) {
          setToken(null)
          setUser(null)
        }
      } finally {
        if (isMounted) {
          setIsLoading(false)
        }
      }
    }

    bootstrap()

    return () => {
      isMounted = false
    }
  }, [])

  const login = useCallback(
    async (payload: LoginPayload) => {
      const authResponse = await loginRequest(payload)
      hydrateSession(authResponse)
    },
    [hydrateSession]
  )

  const register = useCallback(
    async (payload: RegisterPayload) => {
      const authResponse = await registerRequest(payload)
      hydrateSession(authResponse)
    },
    [hydrateSession]
  )

  const logout = useCallback(async () => {
    try {
      await logoutRequest(token)
    } finally {
      persistToken(null)
      setToken(null)
      setUser(null)
    }
  }, [token])

  const value = useMemo(
    () => ({
      user,
      token,
      isLoading,
      login,
      register,
      logout,
    }),
    [isLoading, login, logout, register, token, user]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
