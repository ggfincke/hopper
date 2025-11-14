import { MetricCard } from './MetricCard'
import type { MetricSummary } from '../../lib/dashboardData'

interface MetricsGridProps {
  metrics: MetricSummary[]
}

/**
 * MetricsGrid displays key performance metrics in a responsive grid layout.
 * First metric spans full width on mobile, half on desktop.
 *
 * @param metrics - Array of metric summaries to display
 */
export function MetricsGrid({ metrics }: MetricsGridProps) {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-8">
      {metrics.map((metric, idx) => (
        <MetricCard
          key={metric.title}
          {...metric}
          className={idx === 0 ? 'col-span-full md:col-span-4' : 'col-span-full md:col-span-2'}
        />
      ))}
    </div>
  )
}
