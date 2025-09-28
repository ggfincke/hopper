package handlers

import (
	"encoding/json"
	"errors"
	"net/http"
	"strconv"
	"strings"

	apitypes "github.com/ggfincke/hopper/services/marketplace/internal/types"
)

const idempotencyHeader = "Idempotency-Key"

func CreateListing(store ListingStore) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		defer r.Body.Close()

		key := strings.TrimSpace(r.Header.Get(idempotencyHeader))
		if key == "" {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", "Idempotency-Key header is required")
			return
		}

		var req apitypes.ListingRequest
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", "invalid JSON payload")
			return
		}

		if err := validateListingRequest(req); err != nil {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", err.Error())
			return
		}

		resp, deduped, err := store.CreateListing(key, req)
		switch {
		case err == nil:
			status := http.StatusCreated
			if deduped {
				status = http.StatusOK
			}
			w.Header().Set("Content-Type", "application/json")
			w.Header().Set(idempotencyHeader, key)
			w.WriteHeader(status)
			_ = json.NewEncoder(w).Encode(resp)
		case errors.Is(err, ErrIdempotencyConflict):
			writeError(w, http.StatusConflict, "CONFLICT", "idempotency key already used for a different payload")
		default:
			writeError(w, http.StatusInternalServerError, "UNKNOWN", "failed to create listing")
		}
	}
}

func GetListing(store ListingStore) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimSpace(r.PathValue("id"))
		if id == "" {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", "listing id is required")
			return
		}

		resp, ok := store.GetListing(id)
		if !ok {
			writeError(w, http.StatusNotFound, "NOT_FOUND", "listing not found")
			return
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_ = json.NewEncoder(w).Encode(resp)
	}
}

func validateListingRequest(req apitypes.ListingRequest) error {
	if strings.TrimSpace(req.Platform) == "" {
		return errors.New("platform is required")
	}
	if strings.TrimSpace(req.SellerAccountID) == "" {
		return errors.New("sellerAccountId is required")
	}
	if strings.TrimSpace(req.SKU) == "" {
		return errors.New("sku is required")
	}
	if strings.TrimSpace(req.Title) == "" {
		return errors.New("title is required")
	}
	if err := validatePrice(req.Price, req.Platform); err != nil {
		return err
	}
	if req.Quantity <= 0 {
		return errors.New("quantity must be greater than zero")
	}
	for idx, media := range req.Media {
		if strings.TrimSpace(media.URL) == "" {
			return errors.New("media[" + strconv.Itoa(idx) + "].url is required")
		}
	}
	return nil
}

func validatePrice(pricePayload apitypes.PricePayload, platform string) error {
	if strings.TrimSpace(pricePayload.Amount) == "" {
		return errors.New("price.amount is required")
	}
	if strings.TrimSpace(pricePayload.Currency) == "" {
		return errors.New("price.currency is required")
	}

	amount, err := strconv.ParseFloat(pricePayload.Amount, 64)
	if err != nil {
		return errors.New("price.amount must be a valid decimal number")
	}

	if amount <= 0 {
		return errors.New("price.amount must be greater than zero")
	}

	validCurrencies := []string{"USD", "EUR", "GBP", "CAD", "AUD"}
	currencyValid := false
	for _, validCurrency := range validCurrencies {
		if strings.EqualFold(pricePayload.Currency, validCurrency) {
			currencyValid = true
			break
		}
	}
	if !currencyValid {
		return errors.New("price.currency '" + pricePayload.Currency + "' is not supported. Supported currencies: " + strings.Join(validCurrencies, ", "))
	}

	if strings.EqualFold(platform, "ebay") {
		if amount < 0.99 {
			return errors.New("price.amount must be at least 0.99 for eBay listings")
		}
	}

	return nil
}
