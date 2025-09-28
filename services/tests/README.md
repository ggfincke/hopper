# Marketplace Connector Tests

This directory holds scaffolding for contract validation and developer-friendly HTTP collections.

## Sample Payload Coverage

The JSON samples under `../samples` capture the golden requests and responses for the stubbed marketplace connector. Automated tests load these fixtures and now validate them against `openapi.yaml` to ensure the DTO contract stays in lockstep with the spec.

## Bruno Collection

The `bruno/` folder packages ready-to-run requests for the current surface area. Bodies pull directly from the same sample payloads so manual demos and automated coverage stay aligned. Execute with the Bruno CLI, or import into the Bruno desktop app.

## Next Steps

- Layer in response assertions (e.g. status codes) for the Bruno requests once the stub behaves predictably.
- Add schema regression checks for new endpoints before they reach the Java adapter.
