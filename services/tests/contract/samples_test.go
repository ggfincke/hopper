package contract

import (
	"bytes"
	"context"
	"encoding/json"
	"os"
	"path/filepath"
	"runtime"
	"strings"
	"testing"

	"github.com/getkin/kin-openapi/openapi3"
	apitypes "github.com/ggfincke/hopper/services/marketplace/internal/types"
)

var (
	baseDir     string
	samplesRoot string
	openAPIDoc  *openapi3.T
)

func init() {
	_, filename, _, ok := runtime.Caller(0)
	if !ok {
		panic("unable to resolve current filename for samples path")
	}
	baseDir = filepath.Clean(filepath.Join(filepath.Dir(filename), "..", ".."))
	samplesRoot = filepath.Join(baseDir, "samples")
	loader := openapi3.NewLoader()
	var err error
	openAPIDoc, err = loader.LoadFromFile(filepath.Join(baseDir, "openapi.yaml"))
	if err != nil {
		panic(err)
	}
	if err := openAPIDoc.Validate(context.Background()); err != nil {
		panic(err)
	}
}

type errorEnvelope struct {
	Error apitypes.APIError `json:"error"`
}

func TestSamplePayloadsDecode(t *testing.T) {
	cases := []struct {
		name      string
		paths     [][]string
		newTarget func() any
		schemaRef string
	}{
		{
			name:      "listing create request",
			paths:     [][]string{{"listings", "create-success-request.json"}},
			newTarget: func() any { return &apitypes.ListingRequest{} },
			schemaRef: "#/components/schemas/ListingRequest",
		},
		{
			name: "listing response pending variants",
			paths: [][]string{
				{"listings", "create-success-response.json"},
				{"listings", "create-idempotent-response.json"},
				{"listings", "get-pending-response.json"},
			},
			newTarget: func() any { return &apitypes.ListingResponse{} },
			schemaRef: "#/components/schemas/ListingResponse",
		},
		{
			name:      "listing validation error",
			paths:     [][]string{{"listings", "create-invalid-response.json"}},
			newTarget: func() any { return &errorEnvelope{} },
			schemaRef: "#/components/schemas/ErrorEnvelope",
		},
		{
			name:      "listing get active response",
			paths:     [][]string{{"listings", "get-active-response.json"}},
			newTarget: func() any { return &apitypes.ListingResponse{} },
			schemaRef: "#/components/schemas/ListingResponse",
		},
		{
			name:      "order create request",
			paths:     [][]string{{"orders", "create-success-request.json"}},
			newTarget: func() any { return &apitypes.OrderRequest{} },
			schemaRef: "#/components/schemas/OrderRequest",
		},
		{
			name: "order response pending variants",
			paths: [][]string{
				{"orders", "create-success-response.json"},
				{"orders", "create-idempotent-response.json"},
				{"orders", "get-pending-response.json"},
			},
			newTarget: func() any { return &apitypes.OrderResponse{} },
			schemaRef: "#/components/schemas/OrderResponse",
		},
		{
			name:      "order listing missing",
			paths:     [][]string{{"orders", "create-not-found-response.json"}},
			newTarget: func() any { return &errorEnvelope{} },
			schemaRef: "#/components/schemas/ErrorEnvelope",
		},
		{
			name:      "order idempotency conflict",
			paths:     [][]string{{"orders", "create-conflict-response.json"}},
			newTarget: func() any { return &errorEnvelope{} },
			schemaRef: "#/components/schemas/ErrorEnvelope",
		},
		{
			name:      "order get confirmed response",
			paths:     [][]string{{"orders", "get-confirmed-response.json"}},
			newTarget: func() any { return &apitypes.OrderResponse{} },
			schemaRef: "#/components/schemas/OrderResponse",
		},
		{
			name:      "health response",
			paths:     [][]string{{"health", "get-health-response.json"}},
			newTarget: func() any { return &apitypes.HealthResponse{} },
			schemaRef: "#/components/schemas/HealthResponse",
		},
	}

	for _, tc := range cases {
		tc := tc
		t.Run(tc.name, func(t *testing.T) {
			for _, pathParts := range tc.paths {
				pathParts := pathParts
				t.Run(strings.Join(pathParts, "/"), func(t *testing.T) {
					t.Parallel()
					data := readSample(t, pathParts...)
					target := tc.newTarget()
					if err := json.Unmarshal(data, target); err != nil {
						t.Fatalf("failed to decode %v: %v", pathParts, err)
					}
					validateAgainstSchema(t, tc.schemaRef, data)
				})
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

func validateAgainstSchema(t *testing.T, schemaRef string, data []byte) {
	t.Helper()
	const prefix = "#/components/schemas/"
	if !strings.HasPrefix(schemaRef, prefix) {
		t.Fatalf("unsupported schema ref %s", schemaRef)
	}
	name := strings.TrimPrefix(schemaRef, prefix)
	ref, ok := openAPIDoc.Components.Schemas[name]
	if !ok || ref == nil || ref.Value == nil {
		t.Fatalf("schema %s not found in OpenAPI document", name)
	}
	var payload any
	decoder := json.NewDecoder(bytes.NewReader(data))
	decoder.UseNumber()
	if err := decoder.Decode(&payload); err != nil {
		t.Fatalf("failed to decode payload for schema validation: %v", err)
	}
	if err := ref.Value.VisitJSON(payload); err != nil {
		t.Fatalf("schema validation failed for %s: %v", schemaRef, err)
	}
}
