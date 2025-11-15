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

/**
 * DashboardPage orchestrates the main dashboard view.
 * Composes layout, sidebar, header, and content components.
 * All theme management handled by ThemeContext - no props drilling!
 */
export function DashboardPage() {
  return (
    <AppLayout sidebar={<DashboardSidebar navItems={NAV_ITEMS} />}>
      <Page>
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
          <PageSection>
            <MetricsGrid metrics={METRIC_SUMMARIES} />
          </PageSection>

          <PageSection className="grid grid-cols-1 gap-4 lg:grid-cols-[1.35fr_1fr]">
            <SalesChartCard data={SALES_PERFORMANCE} />
            <TopProductsCard products={TOP_PRODUCTS} />
          </PageSection>

          <PageSection>
            <OrdersTable orders={RECENT_ORDERS} />
          </PageSection>
        </PageContent>
      </Page>
    </AppLayout>
  )
}
