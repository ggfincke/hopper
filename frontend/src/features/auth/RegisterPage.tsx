// src/features/auth/RegisterPage.tsx
// Account creation screen that calls the real registration endpoint
import { useState, type ChangeEvent, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Button } from '../../components/ui/Button'
import { Input } from '../../components/ui/Input'
import { Logo } from '../../components/ui/Logo'
import { cn } from '../../lib/utils'
import { useTheme } from '../../hooks/useTheme'
import { useAuth } from '../../hooks/useAuth'

// * RegisterPage renders account creation form w/ validation & error feedback
export function RegisterPage() {
  const navigate = useNavigate()
  const { isDark } = useTheme()
  const { register } = useAuth()
  const [formValues, setFormValues] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
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

    if (formValues.password !== formValues.confirmPassword) {
      setError('Passwords do not match')
      return
    }

    setIsSubmitting(true)
    try {
      await register({
        username: formValues.username,
        email: formValues.email,
        password: formValues.password,
        rememberMe: formValues.rememberMe,
      })
      navigate('/', { replace: true })
    } catch (submitError) {
      const message = submitError instanceof Error ? submitError.message : 'Unable to create your account'
      setError(message)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-transparent px-4 py-10">
      <div
        className={cn(
          'w-full max-w-xl rounded-3xl border px-8 py-10 shadow-2xl shadow-indigo-500/10',
          isDark ? 'border-slate-800/80 bg-slate-900/90 text-white' : 'border-white bg-white/90'
        )}
      >
        <Logo size="lg" className="justify-center" />
        <div className="mt-6 text-center">
          <p className="text-sm font-medium uppercase tracking-[0.2em] text-indigo-500">Join Hopper</p>
          <h1 className="mt-2 text-2xl font-semibold">Create your account</h1>
          <p className={cn('mt-1 text-sm', isDark ? 'text-slate-400' : 'text-slate-500')}>
            Tell us a bit about yourself to get started
          </p>
        </div>

        <form className="mt-10 space-y-5" onSubmit={handleSubmit}>
          <div className="space-y-2">
            <label className="text-sm font-medium" htmlFor="username">
              Username
            </label>
            <Input
              id="username"
              name="username"
              placeholder="garrett"
              value={formValues.username}
              onChange={handleChange}
              required
              autoComplete="username"
            />
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium" htmlFor="email">
              Email
            </label>
            <Input
              id="email"
              type="email"
              name="email"
              placeholder="garrett@hopper.app"
              value={formValues.email}
              onChange={handleChange}
              required
              autoComplete="email"
            />
          </div>

          <div className="grid gap-5 lg:grid-cols-2">
            <div className="space-y-2">
              <label className="text-sm font-medium" htmlFor="password">
                Password
              </label>
              <Input
                id="password"
                type="password"
                name="password"
                placeholder="At least 8 characters"
                value={formValues.password}
                onChange={handleChange}
                required
                autoComplete="new-password"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium" htmlFor="confirmPassword">
                Confirm password
              </label>
              <Input
                id="confirmPassword"
                type="password"
                name="confirmPassword"
                placeholder="Re-enter password"
                value={formValues.confirmPassword}
                onChange={handleChange}
                required
                autoComplete="new-password"
              />
            </div>
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
              <span>Keep me signed in</span>
            </label>
            <Link to="/login" className="font-medium text-indigo-500">
              Back to sign in
            </Link>
          </div>

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
            {isSubmitting ? 'Creating account…' : 'Create account'}
          </Button>
        </form>

        <p className={cn('mt-8 text-center text-sm', isDark ? 'text-slate-400' : 'text-slate-600')}>
          Already have an account?{' '}
          <Link to="/login" className="font-semibold text-indigo-500">
            Sign in instead
          </Link>
        </p>
      </div>
    </div>
  )
}
