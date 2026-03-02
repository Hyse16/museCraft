'use client'

import { useQuery } from '@tanstack/react-query'
import Header from '@/components/layout/Header'
import { brandApi, subscriptionApi } from '@/lib/api'
import { useAuth } from '@/context/AuthContext'
import { useState } from 'react'
import type { SubscriptionTier } from '@/types'

const TIER_INFO: Record<SubscriptionTier, { label: string; price: string; features: string[] }> = {
  FREE:    { label: '무료',      price: '₩0',      features: ['기본 브랜드 소개 열람', '공개 콘텐츠 접근'] },
  BASIC:   { label: '베이직',    price: '₩9,900/월', features: ['뉴스레터 수신', '일반 콘텐츠 접근', 'FREE 포함'] },
  PREMIUM: { label: '프리미엄',  price: '₩19,900/월', features: ['모든 콘텐츠 접근', '디지털 상품 다운로드', 'BASIC 포함'] },
}

export default function BrandDetailPage({ params }: { params: { brandId: string } }) {
  const { user } = useAuth()
  const brandId = Number(params.brandId)
  const [subscribing, setSubscribing] = useState(false)

  const { data: brandData } = useQuery({
    queryKey: ['brand', brandId],
    queryFn: () => brandApi.getById(brandId).then((r) => r.data.data),
  })
  const { data: countData } = useQuery({
    queryKey: ['brand-count', brandId],
    queryFn: () => subscriptionApi.getCount(brandId).then((r) => r.data.data),
  })

  const brand = brandData
  const subscriberCount = countData ?? 0

  async function handleSubscribe(tier: SubscriptionTier) {
    if (!user) { window.location.href = '/login'; return }
    setSubscribing(true)
    try {
      await subscriptionApi.subscribe({ brandId, tier })
      alert(`${TIER_INFO[tier].label} 구독이 완료되었습니다!`)
    } catch (e: any) {
      alert(e.response?.data?.message ?? '구독에 실패했습니다.')
    } finally {
      setSubscribing(false)
    }
  }

  if (!brand) {
    return (
      <>
        <Header />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="animate-spin w-8 h-8 border-2 border-primary border-t-transparent rounded-full" />
        </div>
      </>
    )
  }

  return (
    <>
      <Header />
      <main className="max-w-4xl mx-auto px-4 py-10">
        {/* 브랜드 히어로 */}
        <div className="card mb-8" style={{ borderTopColor: brand.colorPalette?.primary, borderTopWidth: 3 }}>
          <div className="flex items-start gap-6">
            <div
              className="w-20 h-20 rounded-2xl flex items-center justify-center text-3xl font-bold text-white flex-shrink-0"
              style={{ backgroundColor: brand.colorPalette?.primary ?? '#7c3aed' }}
            >
              {brand.name[0]}
            </div>
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-white mb-1">{brand.name}</h1>
              <p className="text-primary-light italic mb-3">"{brand.slogan}"</p>
              <div className="flex gap-4 text-sm text-gray-400">
                <span>👥 구독자 {subscriberCount.toLocaleString()}명</span>
                <span>🏷 {brand.keywords}</span>
              </div>
            </div>
          </div>

          <div className="mt-6 pt-5 border-t border-border">
            <h2 className="text-sm font-semibold text-gray-400 mb-2">브랜드 스토리</h2>
            <p className="text-gray-300 leading-relaxed">{brand.brandStory}</p>
          </div>

          {brand.colorPalette && (
            <div className="mt-5">
              <h2 className="text-sm font-semibold text-gray-400 mb-2">컬러 팔레트</h2>
              <div className="flex gap-3">
                {Object.entries(brand.colorPalette).map(([k, v]) => (
                  <div key={k} className="flex items-center gap-2">
                    <div className="w-6 h-6 rounded border border-border" style={{ backgroundColor: v }} />
                    <span className="text-xs font-mono text-gray-500">{v}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* 구독 티어 */}
        <h2 className="text-xl font-bold text-white mb-4">구독 플랜</h2>
        <div className="grid md:grid-cols-3 gap-4">
          {(Object.keys(TIER_INFO) as SubscriptionTier[]).map((tier) => {
            const info = TIER_INFO[tier]
            const isPremium = tier === 'PREMIUM'
            return (
              <div key={tier} className={`card relative ${isPremium ? 'border-primary' : ''}`}>
                {isPremium && (
                  <span className="absolute -top-3 left-1/2 -translate-x-1/2 text-xs bg-primary text-white px-3 py-1 rounded-full font-semibold">
                    인기
                  </span>
                )}
                <h3 className="font-bold text-white text-lg mb-1">{info.label}</h3>
                <p className="text-primary-light font-mono text-xl mb-4">{info.price}</p>
                <ul className="space-y-2 mb-6">
                  {info.features.map((f) => (
                    <li key={f} className="text-sm text-gray-400 flex items-center gap-2">
                      <span className="text-green-400">✓</span> {f}
                    </li>
                  ))}
                </ul>
                <button
                  onClick={() => handleSubscribe(tier)}
                  disabled={subscribing}
                  className={isPremium ? 'btn-primary w-full' : 'btn-secondary w-full'}
                >
                  {tier === 'FREE' ? '무료 시작' : `${info.label} 구독`}
                </button>
              </div>
            )
          })}
        </div>
      </main>
    </>
  )
}
