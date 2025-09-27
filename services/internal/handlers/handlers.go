package handlers

import (
	"encoding/json"
	"net/http"
	"time"
)

// HealthResponse is returned by the /v1/health endpoint; kept minimal for the initial scaffold
type HealthResponse struct {
	Status    string    `json:"status"`
	Service   string    `json:"service"`
	Timestamp time.Time `json:"timestamp"`
	Version   string    `json:"version,omitempty"`
}

// Health returns an http.HandlerFunc that reports the service name and current status for health checks
func Health(serviceName string, version string) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_ = json.NewEncoder(w).Encode(HealthResponse{
			Status:    "ok",
			Service:   serviceName,
			Timestamp: time.Now().UTC(),
			Version:   version,
		})
	}
}
