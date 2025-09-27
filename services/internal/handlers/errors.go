package handlers

import (
	"encoding/json"
	"net/http"
)

type APIError struct {
	Code              string      `json:"code"`
	Message           string      `json:"message"`
	Details           interface{} `json:"details,omitempty"`
	RetryAfterSeconds int         `json:"retryAfterSeconds,omitempty"`
}

type errorEnvelope struct {
	Error APIError `json:"error"`
}

func writeError(w http.ResponseWriter, status int, code, message string, opts ...func(*APIError)) {
	apiErr := APIError{Code: code, Message: message}
	for _, opt := range opts {
		opt(&apiErr)
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(errorEnvelope{Error: apiErr})
}

func withRetryAfter(seconds int) func(*APIError) {
	return func(apiErr *APIError) {
		apiErr.RetryAfterSeconds = seconds
	}
}
