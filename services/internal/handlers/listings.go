package handlers

import (
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"strconv"
	"strings"
)


// Media captures optional listing media entries
type Media struct {
	URL string `json:"url"`
}

// PricePayload represents the price structure expected from the Java client
type PricePayload struct {
	Amount   string `json:"amount"`
	Currency string `json:"currency"`
}

// ListingRequest matches the shared contract for creating listings through the connector
type ListingRequest struct {
	Platform        string       `json:"platform"`
	SellerAccountID string       `json:"sellerAccountId"`
	SKU             string       `json:"sku"`
	Title           string       `json:"title"`
	Description     string       `json:"description,omitempty"`
	Price           PricePayload `json:"price"`
	Quantity        int          `json:"quantity"`
	Media           []Media      `json:"media,omitempty"`
}

// ListingResponse is a stubbed representation of the connector response for listing creation
type ListingResponse struct {
	ListingID  string `json:"listingId"`
	ExternalID string `json:"externalId,omitempty"`
	Status     string `json:"status"`
}

// CreateListing returns a handler that accepts the full listing payload and responds with stubbed data
func CreateListing() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		defer r.Body.Close()

		var req ListingRequest
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			respondListingError(w, http.StatusBadRequest, "invalid JSON payload")
			return
		}

		if err := validateListingRequest(req); err != nil {
			respondListingError(w, http.StatusBadRequest, err.Error())
			return
		}

		resp := ListingResponse{
			ListingID: fmt.Sprintf("stub-%s-%s", strings.ToLower(req.Platform), strings.ToLower(req.SKU)),
			Status:    "PENDING",
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusCreated)
		_ = json.NewEncoder(w).Encode(resp)
	}
}

// Validate the listing request payload
func validateListingRequest(req ListingRequest) error {
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
			return fmt.Errorf("media[%d].url is required", idx)
		}
	}
	return nil
}

func validatePrice(pricePayload PricePayload, platform string) error {
	// Validate that price fields are not empty
	if strings.TrimSpace(pricePayload.Amount) == "" {
		return errors.New("price.amount is required")
	}
	if strings.TrimSpace(pricePayload.Currency) == "" {
		return errors.New("price.currency is required")
	}

	// Parse the amount as a float64 for validation
	amount, err := strconv.ParseFloat(pricePayload.Amount, 64)
	if err != nil {
		return errors.New("price.amount must be a valid decimal number")
	}

	if amount <= 0 {
		return errors.New("price.amount must be greater than zero")
	}

	// Validate currency (support common currencies)
	validCurrencies := []string{"USD", "EUR", "GBP", "CAD", "AUD"}
	currencyValid := false
	for _, validCurrency := range validCurrencies {
		if strings.EqualFold(pricePayload.Currency, validCurrency) {
			currencyValid = true
			break
		}
	}
	if !currencyValid {
		return fmt.Errorf("price.currency '%s' is not supported. Supported currencies: %s", 
			pricePayload.Currency, strings.Join(validCurrencies, ", "))
	}

	// Platform-specific validations
	if strings.EqualFold(platform, "ebay") && amount < 0.99 {
		return errors.New("price.amount must be at least 0.99 for eBay listings")
	}

	return nil
}

func respondListingError(w http.ResponseWriter, status int, message string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(map[string]string{"error": message})
}
