package handlers

import (
	"encoding/json"
	"net/http"
	"time"

	apitypes "github.com/ggfincke/hopper/services/marketplace/internal/types"
)

const (
	// integrationStatusHeader is surfaced so API consumers know the connector remains a stubbed placeholder.
	integrationStatusHeader = "X-Marketplace-Integration"
	// integrationStatusValue clearly communicates that live eBay/TCG linking is unfinished.
	integrationStatusValue = "stubbed-placeholder"
)

func markIntegrationStub(w http.ResponseWriter) {
	w.Header().Set(integrationStatusHeader, integrationStatusValue)
}

// Health returns an http.HandlerFunc that reports the service name and current status for health checks
func Health(serviceName string, version string) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		markIntegrationStub(w)
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_ = json.NewEncoder(w).Encode(apitypes.HealthResponse{
			Status:    "ok",
			Service:   serviceName,
			Timestamp: time.Now().UTC(),
			Version:   version,
		})
	}
}
