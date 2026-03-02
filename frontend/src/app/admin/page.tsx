'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useEffect } from 'react'
import { useRouter } from 'next/navigation'
import Header from '@/components/layout/Header'
import { adminApi } from '@/lib/api'
import { useAuth } from '@/context/AuthContext'
import type { Brand } from '@/types'

export default function AdminPage() {
  const router = useRouter()
  const { user, isLoading: authLoading } = useAuth()
  const queryClient = useQueryClient()

  useEffect(() => {
    if (!authLoading && (!user || user.role !== 'ADMIN')) {
      router.push('/login')
    }
  }, [user, authLoading, router])

  const { data: pendingBrands = [], isLoading } = useQuery<Brand[]>({
    queryKey: ['admin-pending-brands'],
    queryFn: () => adminApi.getPendingBrands().then((r) => r.data.data),
    enabled: !!user && user.role === 'ADMIN',
  })

  const { data: activeBrandsData } = useQuery({
    queryKey: ['admin-active-brands'],
    queryFn: () => adminApi.getAllBrands().then((r) => r.data.data),
    enabled: !!user && user.role === 'ADMIN',
  })

  const activeBrands: Brand[] = activeBrandsData?.content ?? []

  const activateMutation = useMutation({
    mutationFn: (brandId: number) => adminApi.activateBrand(brandId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-pending-brands'] })
      queryClient.invalidateQueries({ queryKey: ['admin-active-brands'] })
    },
  })

  if (authLoading) return null

  const stats = [
    { label: '승인 대기 브랜드', value: pendingBrands.length, color: '#f97316' },
    { label: '활성 브랜드', value: activeBrands.length, color: '#3fb950' },
    { label: '전체 브랜드', value: pendingBrands.length + activeBrands.length, color: '#7c3aed' },
  ]

  return (
    <>
      <Header />
      <main className="max-w-5xl mx-auto px-4 py-10">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-white">어드민 대시보드</h1>
          <p className="text-gray-400 text-sm mt-1">플랫폼 운영 관리 패널</p>
        </div>

        {/* 통계 카드 */}
        <div className="grid grid-cols-3 gap-4 mb-10">
          {stats.map((s) => (
            <div key={s.label} className="card" style={{ borderLeftColor: s.color, borderLeftWidth: 3 }}>
              <p className="text-xs text-gray-500 mb-1">{s.label}</p>
              <p className="text-2xl font-bold text-white">{s.value}</p>
            </div>
          ))}
        </div>

        {/* 승인 대기 브랜드 */}
        <section className="mb-10">
          <h2 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-yellow-400 inline-block animate-pulse" />
            승인 대기 브랜드
            {pendingBrands.length > 0 && (
              <span className="text-xs bg-yellow-900/40 text-yellow-400 px-2 py-0.5 rounded-full font-mono">
                {pendingBrands.length}건
              </span>
            )}
          </h2>

          {isLoading ? (
            <div className="space-y-3">
              {[...Array(3)].map((_, i) => (
                <div key={i} className="card animate-pulse h-24 bg-surface-card" />
              ))}
            </div>
          ) : pendingBrands.length === 0 ? (
            <div className="card text-center py-10 text-gray-500">
              <p className="text-3xl mb-2">✅</p>
              <p>대기 중인 브랜드가 없습니다.</p>
            </div>
          ) : (
            <div className="space-y-3">
              {pendingBrands.map((brand) => (
                <PendingBrandRow
                  key={brand.id}
                  brand={brand}
                  onActivate={() => activateMutation.mutate(brand.id)}
                  isPending={activateMutation.isPending && activateMutation.variables === brand.id}
                />
              ))}
            </div>
          )}
        </section>

        {/* 활성 브랜드 목록 */}
        <section>
          <h2 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-green-400 inline-block" />
            활성 브랜드 목록
          </h2>

          {activeBrands.length === 0 ? (
            <div className="card text-center py-10 text-gray-500">활성 브랜드가 없습니다.</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-gray-500 text-left">
                    <th className="pb-3 pr-4 font-medium">ID</th>
                    <th className="pb-3 pr-4 font-medium">브랜드명</th>
                    <th className="pb-3 pr-4 font-medium">슬로건</th>
                    <th className="pb-3 pr-4 font-medium">키워드</th>
                    <th className="pb-3 font-medium">상태</th>
                  </tr>
                </thead>
                <tbody>
                  {activeBrands.map((brand) => (
                    <tr key={brand.id} className="border-b border-border/50 hover:bg-surface-card transition-colors">
                      <td className="py-3 pr-4 text-gray-500 font-mono">#{brand.id}</td>
                      <td className="py-3 pr-4">
                        <div className="flex items-center gap-2">
                          <div
                            className="w-7 h-7 rounded-lg flex items-center justify-center text-xs font-bold text-white"
                            style={{ backgroundColor: brand.colorPalette?.primary ?? '#7c3aed' }}
                          >
                            {brand.name[0]}
                          </div>
                          <span className="text-white font-medium">{brand.name}</span>
                        </div>
                      </td>
                      <td className="py-3 pr-4 text-primary-light italic text-xs max-w-[180px] truncate">
                        "{brand.slogan}"
                      </td>
                      <td className="py-3 pr-4 text-gray-400 text-xs">{brand.keywords}</td>
                      <td className="py-3">
                        <span className="text-xs bg-green-900/40 text-green-400 px-2 py-0.5 rounded-full font-mono">
                          ACTIVE
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
    </>
  )
}

function PendingBrandRow({
  brand,
  onActivate,
  isPending,
}: {
  brand: Brand
  onActivate: () => void
  isPending: boolean
}) {
  return (
    <div className="card flex items-center gap-4" style={{ borderLeftColor: '#f97316', borderLeftWidth: 3 }}>
      <div
        className="w-12 h-12 rounded-xl flex items-center justify-center text-xl font-bold text-white flex-shrink-0"
        style={{ backgroundColor: brand.colorPalette?.primary ?? '#7c3aed' }}
      >
        {brand.name[0]}
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2 mb-0.5">
          <span className="font-semibold text-white">{brand.name}</span>
          <span className="text-xs bg-yellow-900/40 text-yellow-400 px-2 py-0.5 rounded-full font-mono">PENDING</span>
        </div>
        <p className="text-primary-light text-sm italic mb-1">"{brand.slogan}"</p>
        <p className="text-gray-400 text-xs line-clamp-1">{brand.brandStory}</p>
        <div className="flex gap-2 mt-1">
          {brand.colorPalette &&
            Object.values(brand.colorPalette).map((c) => (
              <div key={c} className="w-4 h-4 rounded-full border border-border" style={{ backgroundColor: c }} />
            ))}
          <span className="text-xs text-gray-500 ml-1">{brand.keywords}</span>
        </div>
      </div>

      <button
        onClick={onActivate}
        disabled={isPending}
        className="btn-primary text-sm flex-shrink-0 disabled:opacity-50"
      >
        {isPending ? '승인 중...' : '✓ 승인'}
      </button>
    </div>
  )
}
