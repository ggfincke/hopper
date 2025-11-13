package dev.fincke.hopper.api.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Standard API error payload returned by {@link dev.fincke.hopper.api.error.GlobalExceptionHandler}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    Map<String, String> errors,
    String path
) {

    public static ApiErrorResponse of(HttpStatus status, String message, String path) {
        return of(status, message, path, null);
    }

    public static ApiErrorResponse of(HttpStatus status,
                                      String message,
                                      String path,
                                      Map<String, String> validationErrors) {

        Map<String, String> sanitizedErrors = sanitize(validationErrors);

        return new ApiErrorResponse(
            OffsetDateTime.now(ZoneOffset.UTC),
            status.value(),
            status.getReasonPhrase(),
            message,
            sanitizedErrors,
            path
        );
    }

    private static Map<String, String> sanitize(Map<String, String> validationErrors) {
        if (validationErrors == null || validationErrors.isEmpty()) {
            return null;
        }

        return Collections.unmodifiableMap(new LinkedHashMap<>(validationErrors));
    }
}
