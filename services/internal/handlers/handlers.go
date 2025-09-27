package handlers

import (
	"encoding/json"
	"net/http"
	"time"

	apitypes "github.com/ggfincke/hopper/services/marketplace/internal/types"
)

// Health returns an http.HandlerFunc that reports the service name and current status for health checks
func Health(serviceName string, version string) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
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
