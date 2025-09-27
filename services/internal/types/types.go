package types

import "time"

type HealthResponse struct {
	Status    string    `json:"status"`
	Service   string    `json:"service"`
	Timestamp time.Time `json:"timestamp"`
	Version   string    `json:"version,omitempty"`
}

type Media struct {
	URL string `json:"url"`
}

type PricePayload struct {
	Amount   string `json:"amount"`
	Currency string `json:"currency"`
}

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

type ErrorDetail struct {
	Code    string `json:"code"`
	Message string `json:"message"`
}

type ListingResponse struct {
	ListingID  string        `json:"listingId"`
	ExternalID string        `json:"externalId,omitempty"`
	Status     string        `json:"status"`
	Errors     []ErrorDetail `json:"errors,omitempty"`
}

type BuyerPayload struct {
	Name    string         `json:"name"`
	Address AddressPayload `json:"address"`
}

type AddressPayload struct {
	Line1   string `json:"line1"`
	City    string `json:"city"`
	Region  string `json:"region"`
	Postal  string `json:"postal"`
	Country string `json:"country"`
}

type OrderItem struct {
	SKU      string       `json:"sku"`
	Quantity int          `json:"quantity"`
	Price    PricePayload `json:"price"`
}

type OrderRequest struct {
	Platform        string       `json:"platform"`
	SellerAccountID string       `json:"sellerAccountId"`
	ListingID       string       `json:"listingId,omitempty"`
	SKU             string       `json:"sku,omitempty"`
	Buyer           BuyerPayload `json:"buyer"`
	Items           []OrderItem  `json:"items"`
	IdempotencyKey  string       `json:"idempotencyKey,omitempty"`
}

type OrderResponse struct {
	OrderID    string        `json:"orderId"`
	ExternalID string        `json:"externalId,omitempty"`
	Status     string        `json:"status"`
	Errors     []ErrorDetail `json:"errors,omitempty"`
}

type APIError struct {
	Code              string      `json:"code"`
	Message           string      `json:"message"`
	Details           interface{} `json:"details,omitempty"`
	RetryAfterSeconds int         `json:"retryAfterSeconds,omitempty"`
}
