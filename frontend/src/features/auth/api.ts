// src/features/auth/api.ts
// Thin wrappers around backend auth endpoints
import type { AuthenticatedUser, AuthResponse, LoginPayload, RegisterPayload } from './types'

const AUTH_BASE_PATH = '/api/auth'

// grab error message from API if possible
async function buildError(response: Response, fallbackMessage: string) {
  try {
    const data = await response.json()
    const apiMessage = data?.message || data?.error || data?.details
    return apiMessage ? new Error(apiMessage) : new Error(fallbackMessage)
  } catch (error) {
    console.error('Failed to parse error response:', error)
    return new Error(fallbackMessage)
  }
}

// authenticate user credentials & retrieve access token w/ user data
export async function loginRequest(payload: LoginPayload): Promise<AuthResponse> {
  const response = await fetch(`${AUTH_BASE_PATH}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    throw await buildError(response, 'Unable to log in with those credentials')
  }

  return (await response.json()) as AuthResponse
}

// create new user account & immediately authenticate w/ access token
export async function registerRequest(payload: RegisterPayload): Promise<AuthResponse> {
  const response = await fetch(`${AUTH_BASE_PATH}/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    throw await buildError(response, 'Unable to create your account right now')
  }

  return (await response.json()) as AuthResponse
}

// fetch full user profile & metadata using access token
export async function fetchCurrentUser(token: string): Promise<AuthenticatedUser> {
  const response = await fetch(`${AUTH_BASE_PATH}/me`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    credentials: 'include',
  })

  if (!response.ok) {
    throw await buildError(response, 'Unable to load the current user')
  }

  return (await response.json()) as AuthenticatedUser
}

// invalidate current session & clear server-side auth state
export async function logoutRequest(token?: string | null): Promise<void> {
  const headers: Record<string, string> = {}
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(`${AUTH_BASE_PATH}/logout`, {
    method: 'POST',
    headers,
    credentials: 'include',
  })

  if (!response.ok) {
    throw await buildError(response, 'Unable to sign out right now')
  }
}
