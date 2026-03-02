'use client'

import { useQuery } from '@tanstack/react-query'
import Header from '@/components/layout/Header'
import { brandApi, subscriptionApi } from '@/lib/api'
import { useAuth } from '@/context/AuthContext'
import Link from 'next/link'
import type { Brand } from '@/types'

export default function BrandsPage() {
  const { user } = useAuth()

  const { data, isLoading } = useQuery({
    queryKey: ['brands'],
    queryFn: () => brandApi.getAll(0).then((r) => r.data.data),
  })

  const brands: Brand[] = data?.content ?? []

  async function handleSubscribe(brandId: number) {
    if (!user) { window.location.href = '/login'; return }
    try {
      await subscriptionApi.subscribe({ brandId, tier: 'FREE' })
      alert('무료 구독이 완료되었습니다!')
    } catch (e: any) {
      alert(e.response?.data?.message ?? '구독에 실패했습니다.')
    }
  }

  return (
    <>
      <Header />
      <main className="max-w-6xl mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-2xl font-bold text-white">브랜드 탐색</h1>
          {user?.role === 'CREATOR' && (
            <Link href="/brands/generate" className="btn-primary text-sm">
              + 브랜드 만들기
            </Link>
          )}
        </div>

        {isLoading ? (
          <div className="grid md:grid-cols-3 gap-5">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="card animate-pulse h-48 bg-surface-card" />
            ))}
          </div>
        ) : brands.length === 0 ? (
          <div className="text-center py-24 text-gray-500">
            <p className="text-4xl mb-4">🎨</p>
            <p>아직 활성화된 브랜드가 없습니다.</p>
            {user?.role === 'CREATOR' && (
              <Link href="/brands/generate" className="btn-primary inline-block mt-4">
                첫 번째 브랜드 만들기
              </Link>
            )}
          </div>
        ) : (
          <div className="grid md:grid-cols-3 gap-5">
            {brands.map((brand) => (
              <BrandCard key={brand.id} brand={brand} onSubscribe={handleSubscribe} />
            ))}
          </div>
        )}
      </main>
    </>
  )
}

function BrandCard({ brand, onSubscribe }: { brand: Brand; onSubscribe: (id: number) => void }) {
  return (
    <div
      className="card hover:-translate-y-1 transition-transform cursor-pointer group"
      style={{ borderTopColor: brand.colorPalette?.primary, borderTopWidth: 2 }}
    >
      <Link href={`/brands/${brand.id}`}>
        <h3 className="font-bold text-white text-lg mb-1 group-hover:text-primary-light transition-colors">
          {brand.name}
        </h3>
        <p className="text-primary-light text-sm italic mb-3">"{brand.slogan}"</p>
        <p className="text-gray-400 text-sm leading-relaxed line-clamp-3">{brand.brandStory}</p>
      </Link>

      <div className="mt-4 flex gap-2">
        {brand.colorPalette && Object.values(brand.colorPalette).map((c) => (
          <div key={c} className="w-5 h-5 rounded-full border border-border" style={{ backgroundColor: c }} />
        ))}
        <button
          onClick={() => onSubscribe(brand.id)}
          className="ml-auto text-xs text-primary-light hover:underline"
        >
          무료 구독
        </button>
      </div>
    </div>
  )
}
