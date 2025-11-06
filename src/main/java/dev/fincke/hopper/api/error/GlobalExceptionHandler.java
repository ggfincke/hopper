package dev.fincke.hopper.api.error;

import dev.fincke.hopper.order.order.exception.OrderValidationException;
import dev.fincke.hopper.user.exception.AccountLockedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts application and framework exceptions into structured JSON responses for clients.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --- Domain-specific handlers -------------------------------------------------------------

    // Handles all exceptions implementing NotFoundException marker interface
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(RuntimeException ex, HttpServletRequest request) {
        logger.debug("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Handles all exceptions implementing ConflictException marker interface
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(RuntimeException ex, HttpServletRequest request) {
        logger.debug("Conflict detected: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // Handles all exceptions implementing BadRequestException marker interface
    @ExceptionHandler({
        BadRequestException.class,
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        logger.debug("Bad request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountLocked(AccountLockedException ex,
                                                                HttpServletRequest request) {
        logger.debug("Account locked: {}", ex.getMessage());
        return buildResponse(HttpStatus.LOCKED, ex.getMessage(), request);
    }

    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleOrderValidation(OrderValidationException ex,
                                                                  HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (ex.getField() != null) {
            errors.put(ex.getField(), String.join("; ", ex.getValidationErrors()));
        } else if (ex.getValidationErrors() != null && !ex.getValidationErrors().isEmpty()) {
            for (int i = 0; i < ex.getValidationErrors().size(); i++) {
                errors.put("error_" + (i + 1), ex.getValidationErrors().get(i));
            }
        }

        String message = ex.hasMultipleErrors() ? "Order validation failed" : ex.getMessage();
        logger.debug("Order validation error: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, message, request, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                      HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        logger.debug("Constraint violation: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                         HttpServletRequest request) {
        logger.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return buildResponse(HttpStatus.CONFLICT, "Request violates database constraints", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                               HttpServletRequest request) {
        logger.warn("Access denied: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Access is denied", request);
    }

    // Handles all exceptions extending ServerErrorException base class
    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleServerError(RuntimeException ex,
                                                              HttpServletRequest request) {
        logger.error("Server error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandledExceptions(Exception ex,
                                                                      HttpServletRequest request) {
        logger.error("Unhandled exception", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
    }

    // --- Framework-level overrides -----------------------------------------------------------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors().forEach(objectError ->
            errors.put(objectError.getObjectName(), objectError.getDefaultMessage())
        );

        logger.debug("Bean validation failed: {}", ex.getMessage());
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Validation failed",
            resolvePath(request), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        logger.debug("Request payload unreadable: {}", ex.getMessage());
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Malformed JSON request",
            resolvePath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(@NonNull TypeMismatchException ex,
                                                        @NonNull HttpHeaders headers,
                                                        @NonNull HttpStatusCode status,
                                                        @NonNull WebRequest request) {
        String message = "Invalid parameter value";
        if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            String parameterName = mismatch.getName();
            Class<?> requiredType = mismatch.getRequiredType();
            if (parameterName != null && requiredType != null) {
                message = "Parameter '%s' must be of type %s".formatted(
                    parameterName,
                    requiredType.getSimpleName()
                );
            }
        }

        logger.debug("Parameter type mismatch: {}", ex.getMessage());
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, message, resolvePath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        @NonNull MissingServletRequestParameterException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        String message = "Missing required parameter '%s'".formatted(ex.getParameterName());
        logger.debug(message);
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, message, resolvePath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(@NonNull MissingServletRequestPartException ex,
                                                                     @NonNull HttpHeaders headers,
                                                                     @NonNull HttpStatusCode status,
                                                                     @NonNull WebRequest request) {
        String message = "Missing required request part '%s'".formatted(ex.getRequestPartName());
        logger.debug(message);
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, message, resolvePath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        @NonNull HttpRequestMethodNotSupportedException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        String message = "HTTP method %s is not supported for this endpoint".formatted(ex.getMethod());
        logger.debug(message);
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, message, resolvePath(request));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(@NonNull HttpMediaTypeNotSupportedException ex,
                                                                     @NonNull HttpHeaders headers,
                                                                     @NonNull HttpStatusCode status,
                                                                     @NonNull WebRequest request) {
        String supported = ex.getSupportedMediaTypes().stream()
            .map(MediaType::toString)
            .reduce((a, b) -> a + ", " + b)
            .orElse("supported media types");
        String message = "Content type %s is not supported. Supported: %s"
            .formatted(ex.getContentType(), supported);

        logger.debug(message);
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message,
            resolvePath(request));
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
        @NonNull HttpMediaTypeNotAcceptableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        String message = "Requested media type is not acceptable";
        logger.debug(message);
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.NOT_ACCEPTABLE, message,
            resolvePath(request));
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(@NonNull NoHandlerFoundException ex,
                                                                   @NonNull HttpHeaders headers,
                                                                   @NonNull HttpStatusCode status,
                                                                   @NonNull WebRequest request) {
        String message = "No handler found for %s %s".formatted(ex.getHttpMethod(), ex.getRequestURL());
        logger.debug(message);
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.NOT_FOUND, message, resolvePath(request));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // --- Helper methods ----------------------------------------------------------------------

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status,
                                                           String message,
                                                           @Nullable HttpServletRequest request) {
        return buildResponse(status, message, request, null);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status,
                                                           String message,
                                                           @Nullable HttpServletRequest request,
                                                           @Nullable Map<String, String> errors) {
        String path = request != null ? request.getRequestURI() : "";
        ApiErrorResponse body = ApiErrorResponse.of(status, message, path, errors);
        return ResponseEntity.status(status).body(body);
    }

    private String resolvePath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }
}
