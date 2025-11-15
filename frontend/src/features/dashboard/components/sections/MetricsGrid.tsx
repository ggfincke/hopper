// src/features/dashboard/components/sections/MetricsGrid.tsx
// Responsive grid showing KPI cards w/ highlight span
import type { MetricSummary } from '../../types'
import { MetricCard } from '../leaf/MetricCard'

interface MetricsGridProps {
  metrics: MetricSummary[]
}

// * MetricsGrid renders cards & stretches first metric on larger screens
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
