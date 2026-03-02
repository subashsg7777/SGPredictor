import { useState } from 'react'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').trim().replace(/\/+$/, '')
const apiUrl = (path) => `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`

export default function Login({ onLogin }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    if (!email.trim() || !password.trim()) {
      setError('Please fill in all fields')
      return
    }

    setLoading(true)
    try {
      const res = await fetch(apiUrl('/api/login'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email.trim(), password }),
      })

      const data = await res.json().catch(() => ({}))

      if (!res.ok || !data.decision) {
        setError(data.message || 'Login failed — check credentials')
        return
      }

      localStorage.setItem('token', data.token)
      onLogin()
    } catch {
      setError('Network error — is the backend running?')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="grid min-h-screen place-items-center bg-[radial-gradient(circle_at_20%_10%,rgba(9,187,122,0.18),transparent_38%),radial-gradient(circle_at_80%_90%,rgba(9,111,187,0.18),transparent_34%),linear-gradient(180deg,#081218_0%,#0f1b22_100%)] p-6">
      <section className="w-full max-w-[420px] rounded-2xl border border-[rgba(160,218,255,0.15)] bg-[rgba(8,20,27,0.88)] p-8 shadow-[0_24px_48px_rgba(0,0,0,0.35)]">
        {/* Brand */}
        <div className="mb-8 text-center">
          <p className="m-0 text-[0.72rem] font-semibold uppercase tracking-[0.18em] text-[#10c46d]">
            SG Predictor
          </p>
          <h1 className="mt-2 text-[1.65rem] font-bold leading-tight text-[#f4fbff]">
            Welcome back
          </h1>
          <p className="mt-1 text-[0.88rem] text-[#7ea3b5]">
            Sign in to access risk insights
          </p>
        </div>

        {/* Form */}
        <form className="grid gap-4" onSubmit={handleSubmit}>
          {/* Email */}
          <div className="grid gap-1.5">
            <label
              htmlFor="login-email"
              className="text-[0.8rem] font-medium text-[#bfd4de]"
            >
              Email
            </label>
            <input
              id="login-email"
              type="email"
              autoComplete="email"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="h-[44px] rounded-xl border border-[rgba(150,192,208,0.3)] bg-[#0c1f2b] px-3.5 text-[0.92rem] text-[#f5fbff] placeholder:text-[#4a6a7a] outline-none transition focus:border-[#10c46d] focus:ring-1 focus:ring-[#10c46d]/40"
            />
          </div>

          {/* Password */}
          <div className="grid gap-1.5">
            <label
              htmlFor="login-password"
              className="text-[0.8rem] font-medium text-[#bfd4de]"
            >
              Password
            </label>
            <input
              id="login-password"
              type="password"
              autoComplete="current-password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="h-[44px] rounded-xl border border-[rgba(150,192,208,0.3)] bg-[#0c1f2b] px-3.5 text-[0.92rem] text-[#f5fbff] placeholder:text-[#4a6a7a] outline-none transition focus:border-[#10c46d] focus:ring-1 focus:ring-[#10c46d]/40"
            />
          </div>

          {/* Error */}
          {error && (
            <p className="rounded-lg bg-red-500/10 px-3 py-2 text-[0.82rem] text-red-400">
              {error}
            </p>
          )}

          {/* Submit */}
          <button
            type="submit"
            disabled={loading}
            className="mt-2 h-[46px] cursor-pointer rounded-xl border-none bg-gradient-to-br from-[#10c46d] to-[#0ea95f] text-[0.95rem] font-bold text-[#042312] shadow-[0_4px_14px_rgba(16,196,109,0.3)] transition duration-150 hover:-translate-y-0.5 hover:shadow-[0_6px_20px_rgba(16,196,109,0.4)] hover:brightness-110 active:translate-y-0 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? 'Signing in…' : 'Sign In'}
          </button>
        </form>

        <p className="mt-6 text-center text-[0.76rem] text-[#506d7c]">
          Trade smarter — let the model handle the risk.
        </p>
      </section>
    </main>
  )
}
