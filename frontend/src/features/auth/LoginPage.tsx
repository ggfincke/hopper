// src/features/auth/LoginPage.tsx
// Minimalist login screen that wires up the real auth endpoint
import { useState, type ChangeEvent, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Button } from '../../components/ui/Button'
import { Input } from '../../components/ui/Input'
import { Logo } from '../../components/ui/Logo'
import { cn } from '../../lib/utils'
import { useTheme } from '../../hooks/useTheme'
import { useAuth } from '../../hooks/useAuth'

// * LoginPage renders centered auth form w/ validation & error feedback
export function LoginPage() {
  const navigate = useNavigate()
  const { isDark } = useTheme()
  const { login } = useAuth()
  const [formValues, setFormValues] = useState({
    usernameOrEmail: '',
    password: '',
    rememberMe: true,
  })
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = event.target
    setFormValues((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }))
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError(null)
    setIsSubmitting(true)

    try {
      await login(formValues)
      navigate('/', { replace: true })
    } catch (submitError) {
      const message = submitError instanceof Error ? submitError.message : 'Unable to sign in'
      setError(message)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-transparent px-4 py-10">
      {/* centered auth card */}
      <div
        className={cn(
          'w-full max-w-md rounded-3xl border px-8 py-10 shadow-2xl shadow-indigo-500/10',
          isDark ? 'border-slate-800/80 bg-slate-900/90 text-white' : 'border-white bg-white/90'
        )}
      >
        {/* logo & welcome header */}
        <Logo size="lg" className="justify-center" />
        <div className="mt-6 text-center">
          <p className="text-sm font-medium uppercase tracking-[0.2em] text-indigo-500">
            Welcome back
          </p>
          <h1 className="mt-2 text-2xl font-semibold">Sign in to Hopper</h1>
          <p className={cn('mt-1 text-sm', isDark ? 'text-slate-400' : 'text-slate-500')}>
            Use your Hopper credentials to access the dashboard
          </p>
        </div>

        {/* credentials form */}
        <form className="mt-10 space-y-5" onSubmit={handleSubmit}>
          <div className="space-y-2">
            <label className="text-sm font-medium" htmlFor="usernameOrEmail">
              Username or email
            </label>
            <Input
              id="usernameOrEmail"
              name="usernameOrEmail"
              placeholder="garrett@hopper.app"
              value={formValues.usernameOrEmail}
              onChange={handleChange}
              required
              autoComplete="username"
            />
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium" htmlFor="password">
              Password
            </label>
            <Input
              id="password"
              type="password"
              name="password"
              placeholder="••••••••"
              value={formValues.password}
              onChange={handleChange}
              required
              autoComplete="current-password"
            />
          </div>

          <div className="flex items-center justify-between text-sm">
            <label className="inline-flex items-center gap-2">
              <input
                type="checkbox"
                name="rememberMe"
                checked={formValues.rememberMe}
                onChange={handleChange}
                className="h-4 w-4 rounded border-slate-400 text-indigo-600 focus:ring-indigo-500"
              />
              <span>Remember me</span>
            </label>
            <span className="text-indigo-500">Need help?</span>
          </div>

          {/* error message banner */}
          {error && (
            <div
              className={cn(
                'rounded-xl px-4 py-3 text-sm',
                isDark ? 'bg-red-500/10 text-red-200' : 'bg-red-50 text-red-600'
              )}
            >
              {error}
            </div>
          )}

          <Button type="submit" className="w-full" disabled={isSubmitting}>
            {isSubmitting ? 'Signing in…' : 'Sign in'}
          </Button>
        </form>

        {/* registration link footer */}
        <p className={cn('mt-8 text-center text-sm', isDark ? 'text-slate-400' : 'text-slate-600')}>
          Need an account?{' '}
          <Link to="/register" className="font-semibold text-indigo-500">
            Create one
          </Link>
        </p>
      </div>
    </div>
  )
}
