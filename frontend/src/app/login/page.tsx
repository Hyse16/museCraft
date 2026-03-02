'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { authApi } from '@/lib/api'
import { saveTokens, saveUser } from '@/lib/auth'
import { useAuth } from '@/context/AuthContext'
import type { AuthResponse } from '@/types'

export default function LoginPage() {
  const router = useRouter()
  const { setUser } = useAuth()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const { data } = await authApi.login(form)
      const auth: AuthResponse = data.data
      saveTokens(auth.accessToken, auth.refreshToken)
      saveUser(auth.user)
      setUser(auth.user)

      if (auth.user.role === 'CREATOR') router.push('/dashboard')
      else router.push('/brands')
    } catch (err: any) {
      setError(err.response?.data?.message ?? '로그인에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4 bg-surface">
      <div className="w-full max-w-md">
        <Link href="/" className="block text-center text-2xl font-bold bg-gradient-to-r from-primary-light to-accent bg-clip-text text-transparent mb-8">
          museCraft
        </Link>

        <div className="card">
          <h1 className="text-xl font-bold text-white mb-6">로그인</h1>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm text-gray-400 mb-1.5">이메일</label>
              <input
                type="email"
                required
                className="input-base"
                placeholder="you@example.com"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm text-gray-400 mb-1.5">비밀번호</label>
              <input
                type="password"
                required
                className="input-base"
                placeholder="••••••••"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
              />
            </div>

            {error && (
              <p className="text-red-400 text-sm bg-red-900/20 border border-red-800 rounded-lg px-3 py-2">
                {error}
              </p>
            )}

            <button type="submit" className="btn-primary w-full mt-2" disabled={loading}>
              {loading ? '로그인 중...' : '로그인'}
            </button>
          </form>

          <p className="text-sm text-gray-400 text-center mt-5">
            계정이 없으신가요?{' '}
            <Link href="/signup" className="text-primary-light hover:underline">회원가입</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
