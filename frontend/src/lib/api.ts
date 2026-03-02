import axios from 'axios'

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080'

export const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

// 요청 인터셉터: accessToken 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 응답 인터셉터: 401 시 refreshToken으로 재발급
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const { data } = await axios.post(`${BASE_URL}/api/auth/refresh`, { refreshToken })
          const newToken = data.data.accessToken
          localStorage.setItem('accessToken', newToken)
          localStorage.setItem('refreshToken', data.data.refreshToken)
          original.headers.Authorization = `Bearer ${newToken}`
          return api(original)
        } catch {
          localStorage.clear()
          window.location.href = '/login'
        }
      }
    }
    return Promise.reject(error)
  }
)

// ── Auth API ─────────────────────────────────────────────
export const authApi = {
  signup: (body: { email: string; password: string; name: string; role: string }) =>
    api.post('/api/auth/signup', body),
  login: (body: { email: string; password: string }) =>
    api.post('/api/auth/login', body),
  logout: () => api.post('/api/auth/logout'),
}

// ── User API ─────────────────────────────────────────────
export const userApi = {
  me: () => api.get('/api/users/me'),
}

// ── Brand API ─────────────────────────────────────────────
export const brandApi = {
  generate: (body: { name: string; keywords: string }) =>
    api.post('/api/brands/generate', body),
  getMy: () => api.get('/api/brands/my'),
  getById: (id: number) => api.get(`/api/brands/${id}`),
  getAll: (page = 0) => api.get(`/api/brands?page=${page}&size=20`),
  activate: (id: number) => api.patch(`/api/brands/${id}/activate`),
}

// ── Subscription API ──────────────────────────────────────
export const subscriptionApi = {
  subscribe: (body: { brandId: number; tier: string; impUid?: string }) =>
    api.post('/api/subscriptions', body),
  cancel: (id: number) => api.delete(`/api/subscriptions/${id}`),
  getMy: () => api.get('/api/subscriptions/my'),
  getCount: (brandId: number) => api.get(`/api/subscriptions/brands/${brandId}/count`),
}

// ── Admin API ──────────────────────────────────────────────
export const adminApi = {
  getPendingBrands: () => api.get('/api/brands/admin/pending'),
  activateBrand: (id: number) => api.patch(`/api/brands/${id}/activate`),
  getAllBrands: (page = 0) => api.get(`/api/brands?page=${page}&size=100`),
}
