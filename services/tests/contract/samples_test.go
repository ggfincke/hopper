package contract

import (
	"encoding/json"
	"os"
	"path/filepath"
	"runtime"
	"testing"

	apitypes "github.com/ggfincke/hopper/services/marketplace/internal/types"
)

var samplesRoot = func() string {
	_, filename, _, ok := runtime.Caller(0)
	if !ok {
		panic("unable to resolve current filename for samples path")
	}
	return filepath.Join(filepath.Dir(filename), "..", "..", "samples")
}()

type errorEnvelope struct {
	Error apitypes.APIError `json:"error"`
}

func TestSamplePayloadsDecode(t *testing.T) {
	cases := []struct {
		name      string
		pathParts []string
		newTarget func() any
	}{
		{
			name:      "listing create request",
			pathParts: []string{"listings", "create-success-request.json"},
			newTarget: func() any { return &apitypes.ListingRequest{} },
		},
		{
			name:      "listing create response",
			pathParts: []string{"listings", "create-success-response.json"},
			newTarget: func() any { return &apitypes.ListingResponse{} },
		},
		{
			name:      "listing idempotent replay",
			pathParts: []string{"listings", "create-idempotent-response.json"},
			newTarget: func() any { return &apitypes.ListingResponse{} },
		},
		{
			name:      "listing validation error",
			pathParts: []string{"listings", "create-invalid-response.json"},
			newTarget: func() any { return &errorEnvelope{} },
		},
		{
			name:      "listing get pending response",
			pathParts: []string{"listings", "get-pending-response.json"},
			newTarget: func() any { return &apitypes.ListingResponse{} },
		},
		{
			name:      "listing get active response",
			pathParts: []string{"listings", "get-active-response.json"},
			newTarget: func() any { return &apitypes.ListingResponse{} },
		},
		{
			name:      "order create request",
			pathParts: []string{"orders", "create-success-request.json"},
			newTarget: func() any { return &apitypes.OrderRequest{} },
		},
		{
			name:      "order create response",
			pathParts: []string{"orders", "create-success-response.json"},
			newTarget: func() any { return &apitypes.OrderResponse{} },
		},
		{
			name:      "order idempotent replay",
			pathParts: []string{"orders", "create-idempotent-response.json"},
			newTarget: func() any { return &apitypes.OrderResponse{} },
		},
		{
			name:      "order listing missing",
			pathParts: []string{"orders", "create-not-found-response.json"},
			newTarget: func() any { return &errorEnvelope{} },
		},
		{
			name:      "order idempotency conflict",
			pathParts: []string{"orders", "create-conflict-response.json"},
			newTarget: func() any { return &errorEnvelope{} },
		},
		{
			name:      "order get pending response",
			pathParts: []string{"orders", "get-pending-response.json"},
			newTarget: func() any { return &apitypes.OrderResponse{} },
		},
		{
			name:      "order get confirmed response",
			pathParts: []string{"orders", "get-confirmed-response.json"},
			newTarget: func() any { return &apitypes.OrderResponse{} },
		},
		{
			name:      "health response",
			pathParts: []string{"health", "get-health-response.json"},
			newTarget: func() any { return &apitypes.HealthResponse{} },
		},
	}

	for _, tc := range cases {
		tc := tc
		t.Run(tc.name, func(t *testing.T) {
			t.Parallel()
			data := readSample(t, tc.pathParts...)
			target := tc.newTarget()
			if err := json.Unmarshal(data, target); err != nil {
				t.Fatalf("failed to decode %v: %v", tc.pathParts, err)
			}
		})
	}
}

func readSample(t *testing.T, pathParts ...string) []byte {
	t.Helper()
	path := filepath.Join(append([]string{samplesRoot}, pathParts...)...)
	data, err := os.ReadFile(path)
	if err != nil {
		t.Fatalf("failed to read sample %s: %v", path, err)
	}
	return data
}
