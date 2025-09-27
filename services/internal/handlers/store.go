package handlers

import (
	"errors"
	"fmt"
	"reflect"
	"strings"
	"sync"
	"time"
)

var (
	ErrIdempotencyConflict = errors.New("idempotency payload mismatch")
	ErrListingNotFound     = errors.New("listing not found")
)

type ListingStore interface {
	CreateListing(key string, req ListingRequest) (ListingResponse, bool, error)
	GetListing(id string) (ListingResponse, bool)
}

type OrderStore interface {
	CreateOrder(key string, req OrderRequest) (OrderResponse, bool, error)
	GetOrder(id string) (OrderResponse, bool)
}

type StubStore struct {
	mu           sync.RWMutex
	listings     map[string]listingRecord
	listingByKey map[string]string
	listingBySKU map[string]string
	orders       map[string]orderRecord
	orderByKey   map[string]string
}

type listingRecord struct {
	Request   ListingRequest
	Response  ListingResponse
	CreatedAt time.Time
}

type orderRecord struct {
	Request   OrderRequest
	Response  OrderResponse
	CreatedAt time.Time
}

func NewStubStore() *StubStore {
	return &StubStore{
		listings:     make(map[string]listingRecord),
		listingByKey: make(map[string]string),
		listingBySKU: make(map[string]string),
		orders:       make(map[string]orderRecord),
		orderByKey:   make(map[string]string),
	}
}

func (s *StubStore) CreateListing(key string, req ListingRequest) (ListingResponse, bool, error) {
	s.mu.Lock()
	defer s.mu.Unlock()

	if id, ok := s.listingByKey[key]; ok {
		record := s.listings[id]
		if !reflect.DeepEqual(record.Request, req) {
			return ListingResponse{}, false, ErrIdempotencyConflict
		}
		return record.Response, true, nil
	}

	listingID := fmt.Sprintf("lst-%s-%d", strings.ToLower(req.Platform), time.Now().UnixNano())
	response := ListingResponse{
		ListingID:  listingID,
		ExternalID: fmt.Sprintf("ext-%s", strings.ToUpper(req.SKU)),
		Status:     "PENDING",
	}

	record := listingRecord{
		Request:   req,
		Response:  response,
		CreatedAt: time.Now(),
	}

	s.listings[listingID] = record
	s.listingByKey[key] = listingID
	s.listingBySKU[strings.ToLower(req.SKU)] = listingID

	return response, false, nil
}

func (s *StubStore) GetListing(id string) (ListingResponse, bool) {
	s.mu.Lock()
	defer s.mu.Unlock()

	record, ok := s.listings[id]
	if !ok {
		return ListingResponse{}, false
	}

	if time.Since(record.CreatedAt) > 2*time.Second && record.Response.Status != "ACTIVE" {
		record.Response.Status = "ACTIVE"
		s.listings[id] = record
	}

	return record.Response, true
}

func (s *StubStore) CreateOrder(key string, req OrderRequest) (OrderResponse, bool, error) {
	s.mu.Lock()
	defer s.mu.Unlock()

	if id, ok := s.orderByKey[key]; ok {
		record := s.orders[id]
		if !reflect.DeepEqual(record.Request, req) {
			return OrderResponse{}, false, ErrIdempotencyConflict
		}
		return record.Response, true, nil
	}

	if listingID := req.ListingID; listingID != "" {
		if _, ok := s.listings[listingID]; !ok {
			return OrderResponse{}, false, ErrListingNotFound
		}
	} else if sku := strings.ToLower(req.SKU); sku != "" {
		if _, ok := s.listingBySKU[sku]; !ok {
			return OrderResponse{}, false, ErrListingNotFound
		}
	}

	orderID := fmt.Sprintf("ord-%s-%d", strings.ToLower(req.Platform), time.Now().UnixNano())
	response := OrderResponse{
		OrderID:    orderID,
		ExternalID: fmt.Sprintf("ext-%s", strings.ToUpper(orderID)),
		Status:     "PENDING",
	}

	record := orderRecord{
		Request:   req,
		Response:  response,
		CreatedAt: time.Now(),
	}

	s.orders[orderID] = record
	s.orderByKey[key] = orderID

	return response, false, nil
}

func (s *StubStore) GetOrder(id string) (OrderResponse, bool) {
	s.mu.Lock()
	defer s.mu.Unlock()

	record, ok := s.orders[id]
	if !ok {
		return OrderResponse{}, false
	}

	if time.Since(record.CreatedAt) > 2*time.Second && record.Response.Status != "CONFIRMED" {
		record.Response.Status = "CONFIRMED"
		s.orders[id] = record
	}

	return record.Response, true
}
