'use client'

import { useState } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import Link from 'next/link'
import { authApi } from '@/lib/api'
import { saveTokens, saveUser } from '@/lib/auth'
import { useAuth } from '@/context/AuthContext'
import type { AuthResponse, Role } from '@/types'

export default function SignupPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const { setUser } = useAuth()

  const [form, setForm] = useState({
    email: '',
    password: '',
    name: '',
    role: (searchParams.get('role') ?? 'SUBSCRIBER') as Role,
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const { data } = await authApi.signup(form)
      const auth: AuthResponse = data.data
      saveTokens(auth.accessToken, auth.refreshToken)
      saveUser(auth.user)
      setUser(auth.user)

      if (auth.user.role === 'CREATOR') router.push('/brands/generate')
      else router.push('/brands')
    } catch (err: any) {
      setError(err.response?.data?.message ?? '회원가입에 실패했습니다.')
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
          <h1 className="text-xl font-bold text-white mb-6">회원가입</h1>

          {/* 역할 선택 */}
          <div className="flex gap-2 mb-6">
            {(['CREATOR', 'SUBSCRIBER'] as Role[]).map((r) => (
              <button
                key={r}
                type="button"
                onClick={() => setForm({ ...form, role: r })}
                className={`flex-1 py-2.5 rounded-lg text-sm font-semibold border transition-colors ${
                  form.role === r
                    ? 'bg-primary/20 border-primary text-primary-light'
                    : 'bg-surface-card border-border text-gray-400 hover:border-gray-500'
                }`}
              >
                {r === 'CREATOR' ? '🎨 크리에이터' : '👤 구독자'}
              </button>
            ))}
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm text-gray-400 mb-1.5">이름</label>
              <input
                type="text"
                required
                className="input-base"
                placeholder="홍길동"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
              />
            </div>
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
              <label className="block text-sm text-gray-400 mb-1.5">비밀번호 (8자 이상)</label>
              <input
                type="password"
                required
                minLength={8}
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
              {loading ? '가입 중...' : '가입하기'}
            </button>
          </form>

          <p className="text-sm text-gray-400 text-center mt-5">
            이미 계정이 있으신가요?{' '}
            <Link href="/login" className="text-primary-light hover:underline">로그인</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
