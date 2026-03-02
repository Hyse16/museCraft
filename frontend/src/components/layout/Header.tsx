'use client'

import Link from 'next/link'
import { useAuth } from '@/context/AuthContext'
import { authApi } from '@/lib/api'

export default function Header() {
  const { user, logout } = useAuth()

  async function handleLogout() {
    try { await authApi.logout() } catch {}
    logout()
  }

  return (
    <header className="sticky top-0 z-50 border-b border-border bg-surface/80 backdrop-blur-md px-6 py-4">
      <div className="max-w-6xl mx-auto flex items-center justify-between">
        <Link href="/" className="text-xl font-bold bg-gradient-to-r from-primary-light to-accent bg-clip-text text-transparent">
          museCraft
        </Link>

        <nav className="hidden md:flex items-center gap-6 text-sm text-gray-400">
          <Link href="/brands" className="hover:text-white transition-colors">브랜드 탐색</Link>
          {user?.role === 'CREATOR' && (
            <Link href="/dashboard" className="hover:text-white transition-colors">대시보드</Link>
          )}
          {user?.role === 'ADMIN' && (
            <Link href="/admin" className="hover:text-white transition-colors text-yellow-400">⚙ 어드민</Link>
          )}
        </nav>

        <div className="flex items-center gap-3">
          {user ? (
            <>
              <span className="text-sm text-gray-400 hidden sm:block">{user.name}</span>
              <button onClick={handleLogout} className="btn-secondary text-sm py-2 px-4">
                로그아웃
              </button>
            </>
          ) : (
            <>
              <Link href="/login" className="btn-secondary text-sm py-2 px-4">로그인</Link>
              <Link href="/signup" className="btn-primary text-sm py-2 px-4">시작하기</Link>
            </>
          )}
        </div>
      </div>
    </header>
  )
}
