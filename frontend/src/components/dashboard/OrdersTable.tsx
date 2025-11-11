import type { RecentOrder } from '../../lib/dashboardData'

type OrdersTableProps = {
  isDark: boolean
  orders: RecentOrder[]
}

export function OrdersTable({ isDark, orders }: OrdersTableProps) {
  const containerClasses = [
    'flex flex-col gap-3 rounded-2xl border p-4 shadow-[0_18px_36px_rgba(15,23,42,0.4)]',
    isDark
      ? 'bg-slate-950/70 border-slate-900 text-slate-100'
      : 'bg-white border-slate-200 text-slate-900',
  ]

  const tableHeadClasses = [
    'text-left text-[11px] uppercase tracking-wide',
    isDark ? 'bg-slate-950/50 text-slate-500' : 'bg-slate-50 text-slate-500',
  ]

  const rowBackgrounds = isDark
    ? ['bg-slate-950/70', 'bg-slate-900/60']
    : ['bg-white', 'bg-slate-50']

  const primaryText = isDark ? 'text-slate-100' : 'text-slate-900'
  const secondaryText = isDark ? 'text-slate-300' : 'text-slate-700'
  const mutedText = isDark ? 'text-slate-400' : 'text-slate-500'

  const statusToneToClasses = {
    indigo: isDark
      ? 'bg-indigo-500/20 text-indigo-300'
      : 'bg-indigo-100 text-indigo-600',
    emerald: isDark
      ? 'bg-emerald-500/20 text-emerald-300'
      : 'bg-emerald-100 text-emerald-600',
  } as const

  return (
    <div className={containerClasses.join(' ')}>
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="text-sm font-semibold">Latest orders</div>
          <div className="mt-1 text-[11px] text-slate-400">
            Your most recent cross-platform orders.
          </div>
        </div>
        <div className="flex items-center gap-2 text-[11px]">
          <button
            className={[
              'rounded-full px-3 py-1 font-semibold transition-colors',
              isDark
                ? 'border border-slate-500/60 bg-transparent text-slate-200 hover:border-slate-300/60'
                : 'border border-slate-300 bg-white text-slate-700 hover:border-slate-400',
            ].join(' ')}
          >
            Customize
          </button>
          <button
            className={[
              'rounded-full px-3 py-1 font-semibold transition-colors',
              isDark
                ? 'border border-indigo-500/70 text-slate-100 hover:border-indigo-400'
                : 'border border-slate-300 bg-indigo-50 text-slate-800 hover:border-indigo-300',
            ].join(' ')}
          >
            Filter
          </button>
          <button
            className="rounded-full bg-[linear-gradient(135deg,_#6366f1,_#4f46e5,_#1d4ed8)] px-3 py-1 font-semibold text-white shadow-[0_10px_30px_rgba(79,70,229,0.45)]"
          >
            Export
          </button>
        </div>
      </div>

      <div
        className={[
          'overflow-x-auto rounded-2xl border',
          isDark ? 'border-slate-900' : 'border-slate-200',
        ].join(' ')}
      >
        <table className="w-full border-collapse text-[13px]">
          <thead className={tableHeadClasses.join(' ')}>
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
                  className={[
                    'whitespace-nowrap px-4 py-3 text-[11px] font-semibold',
                    isDark ? 'border-b border-slate-900' : 'border-b border-slate-200',
                  ].join(' ')}
                >
                  {header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {orders.map((order, idx) => (
              <tr
                key={order.id}
                className={rowBackgrounds[idx % 2]}
              >
                <td className={`px-4 py-3 font-semibold ${primaryText}`}>
                  {order.id}
                </td>
                <td className={`px-4 py-3 font-medium ${secondaryText}`}>
                  {order.product}
                </td>
                <td className={`px-4 py-3 ${mutedText}`}>{order.date}</td>
                <td className={`px-4 py-3 font-semibold ${primaryText}`}>
                  {order.price}
                </td>
                <td className={`px-4 py-3 ${mutedText}`}>{order.payment}</td>
                <td className="px-4 py-3">
                  <span
                    className={[
                      'inline-flex items-center rounded-full px-3 py-1 text-[11px] font-semibold',
                      statusToneToClasses[order.statusTone],
                    ].join(' ')}
                  >
                    {order.status}
                  </span>
                </td>
                <td
                  className={`px-4 py-3 text-center text-xl ${
                    isDark ? 'text-slate-500' : 'text-slate-400'
                  }`}
                >
                  ···
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
