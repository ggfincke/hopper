import type { ProductPerformance } from '../../lib/dashboardData'

type TopProductsCardProps = {
  products: ProductPerformance[]
  isDark: boolean
}

export function TopProductsCard({ products, isDark }: TopProductsCardProps) {
  const containerClasses = [
    'flex flex-col gap-3 rounded-2xl border p-4 shadow-[0_16px_40px_rgba(15,23,42,0.7)]',
    isDark
      ? 'border-slate-900/80 bg-slate-950/80 text-slate-100'
      : 'border-slate-200 bg-white text-slate-900',
  ]

  const descriptionClass = isDark ? 'text-slate-400' : 'text-slate-500'
  const buttonClasses = [
    'rounded-full border px-3 py-1 text-[11px] font-medium transition-colors',
    isDark
      ? 'border-slate-500/60 bg-slate-900/80 text-slate-100 hover:border-slate-200/70'
      : 'border-slate-200 bg-white text-slate-700 hover:border-slate-400',
  ]

  return (
    <div className={containerClasses.join(' ')}>
      <div className="flex items-center justify-between">
        <div>
          <div className="text-sm font-semibold">
            Top selling products
          </div>
          <div className={`mt-1 text-[11px] ${descriptionClass}`}>
            Quick view of your highest performers today.
          </div>
        </div>
        <button className={buttonClasses.join(' ')}>
          See all
        </button>
      </div>

      <div className="flex flex-col gap-3">
        {products.map((product, idx) => (
          <div
            key={product.name}
            className={[
              'flex items-center justify-between gap-4 rounded-xl border px-3 py-2',
              idx === 0
                ? isDark
                  ? 'border-slate-600/40 bg-[linear-gradient(120deg,_rgba(56,189,248,0.18),_rgba(129,140,248,0.14))]'
                  : 'border-indigo-200 bg-[linear-gradient(120deg,_rgba(129,140,248,0.18),_rgba(56,189,248,0.14))]'
                : isDark
                  ? 'border-slate-900/60 bg-slate-900/80'
                  : 'border-slate-200 bg-slate-50',
            ].join(' ')}
          >
            <div className="flex items-center gap-3">
              <div
                className={[
                  'flex h-10 w-10 items-center justify-center rounded-xl text-lg',
                  product.accentClass,
                ].join(' ')}
              >
                {product.icon}
              </div>
              <div>
                <div className="text-sm font-semibold">{product.name}</div>
                <div
                  className={[
                    'flex gap-3 text-[11px]',
                    isDark ? 'text-slate-300' : 'text-slate-500',
                  ].join(' ')}
                >
                  <span>{product.sales}</span>
                  <span className="text-slate-500">{product.stock}</span>
                </div>
              </div>
            </div>
            <span
              className={[
                'text-[11px] font-semibold',
                isDark ? 'text-emerald-200' : 'text-emerald-600',
              ].join(' ')}
            >
              ● Available
            </span>
          </div>
        ))}
      </div>
    </div>
  )
}
