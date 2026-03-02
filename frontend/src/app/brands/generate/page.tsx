'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import Header from '@/components/layout/Header'
import { brandApi } from '@/lib/api'
import type { Brand } from '@/types'

export default function BrandGeneratePage() {
  const router = useRouter()
  const [form, setForm] = useState({ name: '', keywords: '' })
  const [result, setResult] = useState<Brand | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  async function handleGenerate(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await brandApi.generate(form)
      setResult(data.data)
    } catch (err: any) {
      setError(err.response?.data?.message ?? '브랜드 생성에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <Header />
      <main className="max-w-2xl mx-auto px-4 py-12">
        <div className="mb-10">
          <h1 className="text-3xl font-bold text-white mb-2">AI 브랜드 생성</h1>
          <p className="text-gray-400">브랜드명과 키워드를 입력하면 AI가 슬로건, 브랜드 스토리, 컬러팔레트를 자동으로 만들어드립니다.</p>
        </div>

        {!result ? (
          <form onSubmit={handleGenerate} className="card space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">브랜드 이름</label>
              <input
                type="text"
                required
                className="input-base"
                placeholder="예: 모던무드"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">키워드</label>
              <input
                type="text"
                required
                className="input-base"
                placeholder="예: 미니멀, 감성, 20대 여성, 라이프스타일"
                value={form.keywords}
                onChange={(e) => setForm({ ...form, keywords: e.target.value })}
              />
              <p className="text-xs text-gray-500 mt-1.5">쉼표로 구분해서 입력해주세요</p>
            </div>

            {error && (
              <p className="text-red-400 text-sm bg-red-900/20 border border-red-800 rounded-lg px-3 py-2">{error}</p>
            )}

            <button type="submit" className="btn-primary w-full py-3" disabled={loading}>
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
                  </svg>
                  AI 브랜드 생성 중...
                </span>
              ) : '✨ AI 브랜드 생성하기'}
            </button>
          </form>
        ) : (
          <div className="space-y-5">
            {/* 결과 헤더 */}
            <div className="card" style={{ borderTopColor: result.colorPalette?.primary, borderTopWidth: 3 }}>
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h2 className="text-2xl font-bold text-white">{result.name}</h2>
                  <p className="text-primary-light mt-1 italic">"{result.slogan}"</p>
                </div>
                <span className={`text-xs px-2 py-1 rounded-full font-mono ${
                  result.status === 'ACTIVE' ? 'bg-green-900/40 text-green-400' : 'bg-yellow-900/40 text-yellow-400'
                }`}>
                  {result.status === 'ACTIVE' ? 'ACTIVE' : '승인 대기'}
                </span>
              </div>

              <p className="text-gray-300 text-sm leading-relaxed">{result.brandStory}</p>
            </div>

            {/* 컬러팔레트 */}
            {result.colorPalette && (
              <div className="card">
                <h3 className="text-sm font-semibold text-gray-400 mb-3">컬러 팔레트</h3>
                <div className="flex gap-4">
                  {Object.entries(result.colorPalette).map(([key, color]) => (
                    <div key={key} className="flex flex-col items-center gap-2">
                      <div className="w-12 h-12 rounded-lg border border-border" style={{ backgroundColor: color }} />
                      <span className="text-xs text-gray-400 font-mono">{color}</span>
                      <span className="text-xs text-gray-500">{key}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            <div className="flex gap-3">
              <button onClick={() => setResult(null)} className="btn-secondary flex-1">
                다시 생성
              </button>
              <button onClick={() => router.push('/dashboard')} className="btn-primary flex-1">
                대시보드로 이동
              </button>
            </div>
          </div>
        )}
      </main>
    </>
  )
}
