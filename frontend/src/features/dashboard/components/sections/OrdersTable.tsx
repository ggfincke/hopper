// src/features/dashboard/components/sections/OrdersTable.tsx
// Pageless table showing latest commerce orders & actions
import { MoreHorizontal } from 'lucide-react'
import { useTheme } from '../../../../hooks/useTheme'
import { cn } from '../../../../lib/utils'
import type { RecentOrder } from '../../types'
import { StatusBadge } from '../leaf/StatusBadge'
import { TableActionButtons } from '../leaf/TableActionButtons'

type OrdersTableProps = {
  orders: RecentOrder[]
}

// * OrdersTable renders recent order rows w/ action buttons
export function OrdersTable({ orders }: OrdersTableProps) {
  const { isDark } = useTheme()

  // card surface & border colors per theme
  const containerClasses = cn(
    'flex flex-col gap-3 rounded-2xl border p-4 shadow-[0_18px_36px_rgba(15,23,42,0.4)]',
    isDark
      ? 'bg-slate-950/70 border-slate-900 text-slate-100'
      : 'bg-white border-slate-200 text-slate-900'
  )

  // head styling for sticky look
  const tableHeadClasses = cn(
    'text-left text-[11px] uppercase tracking-wide',
    isDark ? 'bg-slate-950/50 text-slate-500' : 'bg-slate-50 text-slate-500'
  )

  const rowBackgrounds = isDark
    ? ['bg-slate-950/70', 'bg-slate-900/60']
    : ['bg-white', 'bg-slate-50']

  const primaryText = isDark ? 'text-slate-100' : 'text-slate-900'
  const secondaryText = isDark ? 'text-slate-300' : 'text-slate-700'
  const mutedText = isDark ? 'text-slate-400' : 'text-slate-500'

  return (
    <div className={containerClasses}>
      {/* toolbar w/ filters */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="text-sm font-semibold">Latest orders</div>
          <div className="mt-1 text-[11px] text-slate-400">
            Your most recent cross-platform orders.
          </div>
        </div>
        <TableActionButtons />
      </div>

      {/* scrollable table wrapper */}
      <div
        className={cn(
          'overflow-x-auto rounded-2xl border',
          isDark ? 'border-slate-900' : 'border-slate-200'
        )}
      >
        <table className="w-full border-collapse text-[13px]">
          <thead className={tableHeadClasses}>
            <tr>
              {[
                'Order ID',
                'Product',
                'Order date',
                'Price',
                'Payment',
                'Status',
                'Action',
              ].map((header) => (
                <th
                  key={header}
                  className={cn(
                    'whitespace-nowrap px-4 py-3 text-[11px] font-semibold',
                    isDark ? 'border-b border-slate-900' : 'border-b border-slate-200'
                  )}
                >
                  {header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {orders.map((order, idx) => (
              <tr key={order.id} className={rowBackgrounds[idx % 2]}>
                <td className={cn('px-4 py-3 font-semibold', primaryText)}>{order.id}</td>
                <td className={cn('px-4 py-3 font-medium', secondaryText)}>{order.product}</td>
                <td className={cn('px-4 py-3', mutedText)}>{order.date}</td>
                <td className={cn('px-4 py-3 font-semibold', primaryText)}>{order.price}</td>
                <td className={cn('px-4 py-3', mutedText)}>{order.payment}</td>
                <td className="px-4 py-3">
                  <StatusBadge status={order.status} tone={order.statusTone} />
                </td>
                <td
                  className={cn(
                    'px-4 py-3 text-center',
                    isDark ? 'text-slate-500' : 'text-slate-400'
                  )}
                >
                  <MoreHorizontal className="inline h-4 w-4" />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
