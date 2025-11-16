// src/features/dashboard/index.tsx
// Entry point for customer dashboard experience & layout wiring
import { useEffect, useRef, useState } from 'react'
import { AppLayout } from '../../app/layout/AppLayout'
import { Page, PageContent, PageHeader, PageSection } from '../../components/layout/Page'
import { useAuth } from '../../hooks/useAuth'
import { DashboardHeader } from './components/layout/DashboardHeader'
import { DashboardSidebar } from './components/layout/DashboardSidebar'
import { DashboardHero } from './components/sections/DashboardHero'
import { MetricsGrid } from './components/sections/MetricsGrid'
import { OrdersTable } from './components/sections/OrdersTable'
import { SalesChartCard } from './components/sections/SalesChartCard'
import { TopProductsCard } from './components/sections/TopProductsCard'
import {
  METRIC_SUMMARIES,
  NAV_ITEMS,
  RECENT_ORDERS,
  SALES_PERFORMANCE,
  TOP_PRODUCTS,
} from './data/mockData'

// * DashboardPage wires layout, sidebar & all content sections
export function DashboardPage() {
  const { user, logout } = useAuth()
  const [isSigningOut, setIsSigningOut] = useState(false)
  const isMountedRef = useRef(true)

  useEffect(() => {
    return () => {
      isMountedRef.current = false
    }
  }, [])

  const userName = user?.username ?? 'Hopper teammate'
  const userEmail = user?.email ?? 'no-email@hopper.app'
  const userInitials = deriveInitials(user?.username, user?.email)

  const handleSignOut = async () => {
    if (isSigningOut) {
      return
    }

    setIsSigningOut(true)
    try {
      await logout()
    } catch (error) {
      console.error('Unable to sign out', error)
    } finally {
      if (isMountedRef.current) {
        setIsSigningOut(false)
      }
    }
  }

  // layout shell w/ sidebar nav
  return (
    <AppLayout sidebar={<DashboardSidebar navItems={NAV_ITEMS} />}>
      <Page>
        {/* hero header row */}
        <PageHeader
          actions={
            <DashboardHeader
              userName={userName}
              userEmail={userEmail}
              userInitials={userInitials}
              onSignOut={handleSignOut}
              signingOut={isSigningOut}
            />
          }
        >
          <DashboardHero userName={userName} />
        </PageHeader>

        <PageContent>
          {/* summary metrics */}
          <PageSection>
            <MetricsGrid metrics={METRIC_SUMMARIES} />
          </PageSection>

          {/* charts & products split */}
          <PageSection className="grid grid-cols-1 gap-4 lg:grid-cols-[1.35fr_1fr]">
            <SalesChartCard data={SALES_PERFORMANCE} />
            <TopProductsCard products={TOP_PRODUCTS} />
          </PageSection>

          {/* recent orders table */}
          <PageSection>
            <OrdersTable orders={RECENT_ORDERS} />
          </PageSection>
        </PageContent>
      </Page>
    </AppLayout>
  )
}

// extract 2-letter initials from username or email for avatar display
function deriveInitials(username?: string | null, email?: string | null) {
  const source = username?.trim() || email?.trim() || ''

  if (!source) {
    return 'HO'
  }

  const parts = source.split(/\s+/).filter(Boolean)

  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase()
  }

  if (source.includes('@')) {
    return source
      .split('@')[0]
      .slice(0, 2)
      .toUpperCase()
  }

  return source.slice(0, 2).toUpperCase()
}
