'use client'

import { useQuery } from '@tanstack/react-query'
import { useEffect } from 'react'
import { useRouter } from 'next/navigation'
import Header from '@/components/layout/Header'
import { brandApi, subscriptionApi } from '@/lib/api'
import { useAuth } from '@/context/AuthContext'
import Link from 'next/link'
import type { Brand, Subscription } from '@/types'

const TIER_PRICE: Record<string, string> = {
  FREE: '₩0', BASIC: '₩9,900', PREMIUM: '₩19,900',
}

export default function DashboardPage() {
  const router = useRouter()
  const { user, isLoading: authLoading } = useAuth()

  useEffect(() => {
    if (!authLoading && (!user || user.role !== 'CREATOR')) {
      router.push('/login')
    }
  }, [user, authLoading, router])

  const { data: brandData } = useQuery<Brand>({
    queryKey: ['my-brand'],
    queryFn: () => brandApi.getMy().then((r) => r.data.data),
    enabled: !!user,
  })

  const { data: subsData } = useQuery<Subscription[]>({
    queryKey: ['my-subscriptions'],
    queryFn: () => subscriptionApi.getMy().then((r) => r.data.data),
    enabled: !!user,
  })

  const { data: subCount } = useQuery<number>({
    queryKey: ['sub-count', brandData?.id],
    queryFn: () => subscriptionApi.getCount(brandData!.id).then((r) => r.data.data),
    enabled: !!brandData,
  })

  if (authLoading) return null

  const activeSubscriptions = (subsData ?? []).filter((s) => s.status === 'ACTIVE')
  const estimatedRevenue = activeSubscriptions.reduce((sum, s) => {
    return sum + (s.tier === 'PREMIUM' ? 19900 : s.tier === 'BASIC' ? 9900 : 0)
  }, 0)

  return (
    <>
      <Header />
      <main className="max-w-5xl mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-white">크리에이터 대시보드</h1>
            <p className="text-gray-400 text-sm mt-1">안녕하세요, {user?.name}님</p>
          </div>
          {!brandData && (
            <Link href="/brands/generate" className="btn-primary">
              + 브랜드 만들기
            </Link>
          )}
        </div>

        {/* 통계 카드 */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          {[
            { label: '총 구독자', value: (subCount ?? 0).toLocaleString() + '명', color: '#7c3aed' },
            { label: '활성 구독', value: activeSubscriptions.length + '명', color: '#06b6d4' },
            { label: '이번달 예상 수익', value: '₩' + estimatedRevenue.toLocaleString(), color: '#3fb950' },
            { label: '브랜드 상태', value: brandData?.status ?? '미생성', color: '#f97316' },
          ].map((stat) => (
            <div key={stat.label} className="card" style={{ borderLeftColor: stat.color, borderLeftWidth: 3 }}>
              <p className="text-xs text-gray-500 mb-1">{stat.label}</p>
              <p className="text-xl font-bold text-white">{stat.value}</p>
            </div>
          ))}
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          {/* 내 브랜드 */}
          <div className="card">
            <h2 className="font-bold text-white mb-4">내 브랜드</h2>
            {brandData ? (
              <div>
                <div className="flex items-center gap-3 mb-3">
                  <div
                    className="w-12 h-12 rounded-xl flex items-center justify-center text-xl font-bold text-white"
                    style={{ backgroundColor: brandData.colorPalette?.primary ?? '#7c3aed' }}
                  >
                    {brandData.name[0]}
                  </div>
                  <div>
                    <p className="font-semibold text-white">{brandData.name}</p>
                    <p className="text-xs text-primary-light italic">"{brandData.slogan}"</p>
                  </div>
                </div>
                <div className="flex gap-2">
                  {brandData.colorPalette && Object.values(brandData.colorPalette).map((c) => (
                    <div key={c} className="w-5 h-5 rounded-full" style={{ backgroundColor: c }} />
                  ))}
                </div>
                <Link href={`/brands/${brandData.id}`} className="btn-secondary w-full mt-4 block text-center text-sm">
                  브랜드 페이지 보기
                </Link>
              </div>
            ) : (
              <div className="text-center py-8">
                <p className="text-gray-500 text-sm mb-4">아직 브랜드가 없습니다.</p>
                <Link href="/brands/generate" className="btn-primary text-sm">
                  AI로 브랜드 만들기
                </Link>
              </div>
            )}
          </div>

          {/* 구독 현황 */}
          <div className="card">
            <h2 className="font-bold text-white mb-4">구독 현황</h2>
            {activeSubscriptions.length === 0 ? (
              <p className="text-gray-500 text-sm text-center py-8">아직 구독자가 없습니다.</p>
            ) : (
              <div className="space-y-2">
                {(['PREMIUM', 'BASIC', 'FREE'] as const).map((tier) => {
                  const count = activeSubscriptions.filter((s) => s.tier === tier).length
                  if (count === 0) return null
                  return (
                    <div key={tier} className="flex items-center justify-between py-2 border-b border-border">
                      <div className="flex items-center gap-2">
                        <span className={`w-2 h-2 rounded-full ${
                          tier === 'PREMIUM' ? 'bg-primary' : tier === 'BASIC' ? 'bg-accent' : 'bg-gray-500'
                        }`} />
                        <span className="text-sm text-white">{tier}</span>
                        <span className="text-xs text-gray-500">{TIER_PRICE[tier]}</span>
                      </div>
                      <span className="text-sm font-semibold text-white">{count}명</span>
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        </div>
      </main>
    </>
  )
}
