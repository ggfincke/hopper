// src/features/dashboard/index.tsx
// Entry point for customer dashboard experience & layout wiring
import { AppLayout } from '../../app/layout/AppLayout'
import { Page, PageContent, PageHeader, PageSection } from '../../components/layout/Page'
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
  // layout shell w/ sidebar nav
  return (
    <AppLayout sidebar={<DashboardSidebar navItems={NAV_ITEMS} />}>
      <Page>
        {/* hero header row */}
        <PageHeader
          actions={
            <DashboardHeader
              userName="Garrett Fincke"
              userEmail="garrett@hopper.app"
              userInitials="GF"
            />
          }
        >
          <DashboardHero userName="Garrett" />
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
