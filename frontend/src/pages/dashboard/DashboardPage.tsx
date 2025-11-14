import { DashboardLayout } from '../../layouts/dashboard/DashboardLayout'
import { DashboardSidebar } from '../../components/dashboard/DashboardSidebar'
import { DashboardHeader } from '../../components/dashboard/DashboardHeader'
import { DashboardHero } from '../../components/dashboard/DashboardHero'
import { MetricsGrid } from '../../components/dashboard/MetricsGrid'
import { OrdersTable } from '../../components/dashboard/OrdersTable'
import { SalesChartCard } from '../../components/dashboard/SalesChartCard'
import { TopProductsCard } from '../../components/dashboard/TopProductsCard'
import {
  METRIC_SUMMARIES,
  NAV_ITEMS,
  RECENT_ORDERS,
  SALES_PERFORMANCE,
  TOP_PRODUCTS,
} from '../../lib/dashboardData'

/**
 * DashboardPage orchestrates the main dashboard view.
 * Composes layout, sidebar, header, and content components.
 * All theme management handled by ThemeContext - no props drilling!
 */
export function DashboardPage() {
  return (
    <DashboardLayout sidebar={<DashboardSidebar navItems={NAV_ITEMS} />}>
      <DashboardHeader
        userName="Garrett Fincke"
        userEmail="garrett@hopper.app"
        userInitials="GF"
      />

      <div className="space-y-3">
        <DashboardHero userName="Garrett" />
        <MetricsGrid metrics={METRIC_SUMMARIES} />
      </div>

      <div className="grid grid-cols-1 gap-4 lg:grid-cols-[1.35fr_1fr]">
        <SalesChartCard data={SALES_PERFORMANCE} />
        <TopProductsCard products={TOP_PRODUCTS} />
      </div>

      <OrdersTable orders={RECENT_ORDERS} />
    </DashboardLayout>
  )
}
