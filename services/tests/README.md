# Marketplace Connector Tests

This directory holds scaffolding for contract validation and future Postman collections.

## Sample Payload Coverage

The JSON samples under `../samples` capture the golden requests and responses for the stubbed marketplace connector. Automated tests load these fixtures to make sure our sample catalog keeps pace with handler DTOs.

## Next Steps

- Extend the `contract` package with schema assertions or wire it to OpenAPI validation as the contract stabilises.
- Generate a Postman or Bruno collection that references the same sample JSON to guarantee parity between manual demos and automated checks.
