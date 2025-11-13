package handlers

import (
	"encoding/json"
	"errors"
	"net/http"
	"strconv"
	"strings"

	apitypes "github.com/ggfincke/hopper/services/marketplace/internal/types"
)

func CreateOrder(store OrderStore) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		defer r.Body.Close()

		// TODO: Swap the stub store for real marketplace adapters when eBay/TCG APIs are available.
		key := strings.TrimSpace(r.Header.Get(idempotencyHeader))
		if key == "" {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", "Idempotency-Key header is required")
			return
		}

		var req apitypes.OrderRequest
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", "invalid JSON payload")
			return
		}

		if req.IdempotencyKey != "" && req.IdempotencyKey != key {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", "payload idempotencyKey must match header Idempotency-Key")
			return
		}

		if err := validateOrderRequest(req); err != nil {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", err.Error())
			return
		}

		resp, deduped, err := store.CreateOrder(key, req)
		switch {
		case err == nil:
			status := http.StatusCreated
			if deduped {
				status = http.StatusOK
			}
			markIntegrationStub(w)
			w.Header().Set("Content-Type", "application/json")
			w.Header().Set(idempotencyHeader, key)
			w.WriteHeader(status)
			_ = json.NewEncoder(w).Encode(resp)
		case errors.Is(err, ErrIdempotencyConflict):
			writeError(w, http.StatusConflict, "CONFLICT", "idempotency key already used for a different payload")
		case errors.Is(err, ErrListingNotFound):
			writeError(w, http.StatusNotFound, "NOT_FOUND", "referenced listing not found")
		default:
			writeError(w, http.StatusInternalServerError, "UNKNOWN", "failed to create order")
		}
	}
}

func GetOrder(store OrderStore) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimSpace(r.PathValue("id"))
		if id == "" {
			writeError(w, http.StatusBadRequest, "INVALID_REQUEST", "order id is required")
			return
		}

		resp, ok := store.GetOrder(id)
		if !ok {
			writeError(w, http.StatusNotFound, "NOT_FOUND", "order not found")
			return
		}

		markIntegrationStub(w)
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_ = json.NewEncoder(w).Encode(resp)
	}
}

func validateOrderRequest(req apitypes.OrderRequest) error {
	if strings.TrimSpace(req.Platform) == "" {
		return errors.New("platform is required")
	}
	if strings.TrimSpace(req.SellerAccountID) == "" {
		return errors.New("sellerAccountId is required")
	}
	if strings.TrimSpace(req.ListingID) == "" && strings.TrimSpace(req.SKU) == "" {
		return errors.New("either listingId or sku must be provided")
	}
	if strings.TrimSpace(req.Buyer.Name) == "" {
		return errors.New("buyer.name is required")
	}
	if err := validateAddress(req.Buyer.Address); err != nil {
		return err
	}
	if len(req.Items) == 0 {
		return errors.New("items must not be empty")
	}
	for idx, item := range req.Items {
		if strings.TrimSpace(item.SKU) == "" {
			return errors.New("items[" + strconv.Itoa(idx) + "].sku is required")
		}
		if item.Quantity <= 0 {
			return errors.New("items[" + strconv.Itoa(idx) + "].quantity must be greater than zero")
		}
		if err := validatePrice(item.Price, req.Platform); err != nil {
			return err
		}
	}
	return nil
}

func validateAddress(addr apitypes.AddressPayload) error {
	if strings.TrimSpace(addr.Line1) == "" {
		return errors.New("buyer.address.line1 is required")
	}
	if strings.TrimSpace(addr.City) == "" {
		return errors.New("buyer.address.city is required")
	}
	if strings.TrimSpace(addr.Region) == "" {
		return errors.New("buyer.address.region is required")
	}
	if strings.TrimSpace(addr.Postal) == "" {
		return errors.New("buyer.address.postal is required")
	}
	if strings.TrimSpace(addr.Country) == "" {
		return errors.New("buyer.address.country is required")
	}
	return nil
}
