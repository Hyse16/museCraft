import type { User } from '@/types'

export function saveTokens(accessToken: string, refreshToken: string) {
  localStorage.setItem('accessToken', accessToken)
  localStorage.setItem('refreshToken', refreshToken)
}

export function clearTokens() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('user')
}

export function getAccessToken() {
  return localStorage.getItem('accessToken')
}

export function saveUser(user: User) {
  localStorage.setItem('user', JSON.stringify(user))
}

export function getUser(): User | null {
  const raw = localStorage.getItem('user')
  return raw ? JSON.parse(raw) : null
}

export function isLoggedIn() {
  return !!getAccessToken()
}
