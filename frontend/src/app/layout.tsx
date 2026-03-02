import type { Metadata } from 'next'
import './globals.css'
import { Providers } from './providers'

export const metadata: Metadata = {
  title: 'museCraft — AI 크리에이터 브랜드 플랫폼',
  description: 'AI와 함께 5분 만에 브랜드를 만들고 구독 수익을 창출하세요',
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko">
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  )
}
