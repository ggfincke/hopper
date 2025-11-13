package handlers

import (
	"encoding/json"
	"net/http"

	apitypes "github.com/ggfincke/hopper/services/marketplace/internal/types"
)

type errorEnvelope struct {
	Error apitypes.APIError `json:"error"`
}

func writeError(w http.ResponseWriter, status int, code, message string, opts ...func(*apitypes.APIError)) {
	apiErr := apitypes.APIError{Code: code, Message: message}
	for _, opt := range opts {
		opt(&apiErr)
	}

	markIntegrationStub(w)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(errorEnvelope{Error: apiErr})
}

func withRetryAfter(seconds int) func(*apitypes.APIError) {
	return func(apiErr *apitypes.APIError) {
		apiErr.RetryAfterSeconds = seconds
	}
}
