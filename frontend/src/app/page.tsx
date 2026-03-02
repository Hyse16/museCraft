import Link from 'next/link'

export default function LandingPage() {
  return (
    <main className="min-h-screen bg-surface flex flex-col">
      {/* 헤더 */}
      <header className="border-b border-border px-6 py-4 flex items-center justify-between">
        <span className="text-xl font-bold bg-gradient-to-r from-primary-light to-accent bg-clip-text text-transparent">
          museCraft
        </span>
        <div className="flex gap-3">
          <Link href="/login" className="btn-secondary text-sm py-2 px-4">로그인</Link>
          <Link href="/signup" className="btn-primary text-sm py-2 px-4">시작하기</Link>
        </div>
      </header>

      {/* 히어로 */}
      <section className="flex-1 flex flex-col items-center justify-center text-center px-6 py-24">
        <div className="flex flex-wrap gap-2 justify-center mb-8">
          {['Spring Boot', 'Next.js 14', 'MySQL', 'Redis', 'Kafka', 'Claude API'].map((tag) => (
            <span key={tag} className="text-xs font-mono px-3 py-1 rounded-full border border-primary/40 text-primary-light bg-primary/10">
              {tag}
            </span>
          ))}
        </div>

        <h1 className="text-5xl md:text-6xl font-extrabold mb-6 leading-tight">
          <span className="bg-gradient-to-r from-white via-primary-light to-accent bg-clip-text text-transparent">
            AI로 브랜드를
          </span>
          <br />
          <span className="text-white">5분 만에 런칭하세요</span>
        </h1>

        <p className="text-lg text-gray-400 max-w-2xl mb-10 leading-relaxed">
          뉴스레터·디지털 상품·구독 멤버십으로 수익을 창출할 수 있는 AI 크리에이터 플랫폼.
          Substack의 구독 모델 + Canva의 AI 디자인을 하나로.
        </p>

        <div className="flex flex-col sm:flex-row gap-4">
          <Link href="/signup?role=CREATOR" className="btn-primary text-base px-8 py-3">
            크리에이터로 시작하기
          </Link>
          <Link href="/brands" className="btn-secondary text-base px-8 py-3">
            브랜드 탐색하기
          </Link>
        </div>
      </section>

      {/* 기능 카드 */}
      <section className="px-6 pb-24 max-w-5xl mx-auto w-full">
        <div className="grid md:grid-cols-3 gap-5">
          {features.map((f) => (
            <div key={f.title} className="card hover:-translate-y-1 transition-transform border-t-2" style={{ borderTopColor: f.color }}>
              <span className="text-3xl mb-3 block">{f.icon}</span>
              <h3 className="font-bold text-white mb-2">{f.title}</h3>
              <p className="text-sm text-gray-400 leading-relaxed">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>
    </main>
  )
}

const features = [
  { icon: '🤖', color: '#7c3aed', title: 'AI 브랜드 자동 생성', desc: '브랜드명 + 키워드 입력만으로 슬로건·스토리·컬러팔레트를 자동 생성' },
  { icon: '💎', color: '#06b6d4', title: '구독 티어 & 접근 제어', desc: 'FREE / BASIC / PREMIUM 3단계 티어. AOP 기반 콘텐츠 잠금/해제' },
  { icon: '📰', color: '#3fb950', title: '뉴스레터 발행 시스템', desc: 'Kafka 비동기 처리로 1만 명에게도 3분 내 이메일 발송 완료' },
  { icon: '📦', color: '#f97316', title: '디지털 상품 판매', desc: 'PDF, 템플릿 파일 판매. S3 Presigned URL로 미구독자 접근 차단' },
  { icon: '💳', color: '#f85149', title: '결제 & 자동 정산', desc: '포트원 결제 연동. Spring Batch로 월말 크리에이터 정산 자동 계산' },
  { icon: '📊', color: '#e3b341', title: '크리에이터 대시보드', desc: '구독자 수, 매출 추이, 콘텐츠 조회수 실시간 확인' },
]
