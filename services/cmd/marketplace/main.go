package main

import (
	"log"
	"net/http"
	"os"

	"github.com/ggfincke/hopper/services/marketplace/internal/handlers"
)

const (
	defaultPort    = "8080"
	serviceName    = "marketplace-connector"
	serviceVersion = "v0.0.1"
)

func main() {
	port := os.Getenv("PORT")
	if port == "" {
		port = defaultPort
	}

	store := handlers.NewStubStore()

	mux := http.NewServeMux()
	mux.Handle("/v1/health", handlers.Health(serviceName, serviceVersion))
	mux.Handle("POST /v1/listings", handlers.CreateListing(store))
	mux.Handle("GET /v1/listings/{id}", handlers.GetListing(store))
	mux.Handle("POST /v1/orders", handlers.CreateOrder(store))
	mux.Handle("GET /v1/orders/{id}", handlers.GetOrder(store))

	addr := ":" + port
	log.Printf("starting %s on %s", serviceName, addr)

	if err := http.ListenAndServe(addr, mux); err != nil && err != http.ErrServerClosed {
		log.Fatalf("server stopped: %v", err)
	}
}
