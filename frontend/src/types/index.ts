// ── 공통 ─────────────────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

// ── 사용자/인증 ──────────────────────────────────────────
export type Role = 'ADMIN' | 'CREATOR' | 'SUBSCRIBER'

export interface User {
  id: number
  email: string
  name: string
  role: Role
  profileImageUrl: string | null
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: User
}

// ── 브랜드 ──────────────────────────────────────────────
export type BrandStatus = 'PENDING' | 'ACTIVE' | 'SUSPENDED'

export interface ColorPalette {
  primary: string
  secondary: string
  accent: string
}

export interface Brand {
  id: number
  creatorId: number
  name: string
  slogan: string
  brandStory: string
  colorPalette: ColorPalette
  logoUrl: string | null
  keywords: string
  status: BrandStatus
  createdAt: string
}

// ── 구독 ──────────────────────────────────────────────
export type SubscriptionTier = 'FREE' | 'BASIC' | 'PREMIUM'
export type SubscriptionStatus = 'ACTIVE' | 'EXPIRED' | 'CANCELLED'

export interface Subscription {
  id: number
  subscriberId: number
  brandId: number
  tier: SubscriptionTier
  status: SubscriptionStatus
  startedAt: string
  expiresAt: string
  priceKrw: number
}

// ── 정산 ──────────────────────────────────────────────
export interface Settlement {
  id: number
  creatorId: number
  brandId: number
  settlementMonth: string
  totalRevenue: number
  platformFee: number
  creatorAmount: number
  subscriberCount: number
  status: 'PENDING' | 'COMPLETED' | 'FAILED'
}
