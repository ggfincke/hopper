package handlers

import (
	"encoding/json"
	"errors"
	"net/http"
	"strings"
)

// ListingRequest captures the minimal fields needed to create a listing in the stub implementation
type ListingRequest struct {
	SKU string `json:"sku"`
}

// ListingResponse is a stubbed version of the contract we expect from the marketplace connector
type ListingResponse struct {
	ListingID string `json:"listingId"`
	Status    string `json:"status"`
}

// CreateListing returns a handler that accepts a minimal request and returns a canned response
func CreateListing() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var req ListingRequest
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			respondListingError(w, http.StatusBadRequest, "invalid JSON payload")
			return
		}

		if err := validateListingRequest(req); err != nil {
			respondListingError(w, http.StatusBadRequest, err.Error())
			return
		}

		// For now we just echo back a deterministic ID to prove wiring works
		resp := ListingResponse{
			ListingID: "stub-" + strings.ToLower(req.SKU),
			Status:    "PENDING",
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusCreated)
		_ = json.NewEncoder(w).Encode(resp)
	}
}

func validateListingRequest(req ListingRequest) error {
	if strings.TrimSpace(req.SKU) == "" {
		return errors.New("sku is required")
	}
	return nil
}

func respondListingError(w http.ResponseWriter, status int, message string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(map[string]string{"error": message})
}
