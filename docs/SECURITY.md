# Security & Secrets

Scope (early stage)
- No auth on APIs yet (dev-only). Do not expose publicly.

Secrets management
- Platform credentials stored in DB (see `Platform_Credentials`).
- For local dev, use dummy values; never commit real secrets.
- Plan: externalize secrets via env vars or a vault for prod.

Data protection
- Avoid storing plaintext tokens long-term; consider encryption-at-rest for `credential_value`.
- Restrict DB access by role; separate app and admin users.

Operational controls
- Enable HTTPS/terminating proxy in prod.
- Add authentication/authorization before enabling mutations.
- Add audit logging for credential changes.
