# Hopper API

Minimal reference for current endpoints. All responses are JSON. Base URL defaults to `http://localhost:8080`.

- GET `/healthz`
  - 200 OK: `{ "status": "ok", "service": "hopper", "version": "<semver>" }`

- GET `/api/products`
  - 200 OK: `[{ id, sku, name, description, price, quantity }]`

- GET `/api/platforms`
  - 200 OK: `[{ id, name, platformType }]`

- GET `/api/listings`
  - 200 OK: `[{ id, productId, platformId, externalListingId, status, price, quantityListed }]`

Notes
- Pagination, filtering, and mutations are not yet implemented.
- IDs are UUID strings.
- Money fields are decimals.
