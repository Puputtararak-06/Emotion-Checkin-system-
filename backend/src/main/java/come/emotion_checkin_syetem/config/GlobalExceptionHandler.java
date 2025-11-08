package come.emotion_checkin_syetem.config;

import come.emotion_checkin_syetem.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 📍 LOCATION: src/main/java/com/emotion/checkin/config/GlobalExceptionHandler.java
 * 
 * 🚨 GLOBAL EXCEPTION HANDLER - Centralized error handling
 * 
 * ✅ Purpose:
 * - Handle all exceptions in one place
 * - Return consistent error format
 * - Log errors
 * - No need try-catch in every controller
 * 
 * 🎯 Handled Exceptions:
 * - RuntimeException (general errors)
 * - MethodArgumentNotValidException (validation errors)
 * - IllegalArgumentException (invalid arguments)
 * - NullPointerException (null errors)
 * - Exception (catch-all)
 * 
 * 📦 Response Format:
 * {
 *   "success": false,
 *   "message": "Error message",
 *   "data": {...}  // Optional error details
 * }
 * 
 * 🐛 DEBUG CHECKLIST:
 * ✅ @RestControllerAdvice annotation
 * ✅ @ExceptionHandler for each exception type
 * ✅ Consistent ApiResponse format
 * ✅ Logging
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 🚨 HANDLE VALIDATION ERRORS
     * 
     * Triggered by @Valid annotation in controllers
     * 
     * Example:
     * {
     *   "success": false,
     *   "message": "Validation failed",
     *   "data": {
     *     "email": "Invalid email format",
     *     "password": "Password must be at least 8 characters"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("❌ Validation failed: {}", errors);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Validation failed", errors));
    }
    
    /**
     * 🚨 HANDLE ILLEGAL ARGUMENT EXCEPTION
     * 
     * Example: Invalid enum value, negative number, etc.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(
        IllegalArgumentException ex
    ) {
        log.error("❌ Illegal argument: {}", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    /**
     * 🚨 HANDLE NULL POINTER EXCEPTION
     * 
     * Usually indicates a bug in the code
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse> handleNullPointer(
        NullPointerException ex
    ) {
        log.error("❌ Null pointer exception", ex);
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Internal server error: null value encountered"));
    }
    
    /**
     * 🚨 HANDLE RUNTIME EXCEPTION (General)
     * 
     * Catches most custom exceptions thrown by services
     * 
     * Examples:
     * - "User not found"
     * - "Email already registered"
     * - "Already checked-in today"
     * - "Access denied"
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(
        RuntimeException ex
    ) {
        log.error("❌ Runtime exception: {}", ex.getMessage());
        
        // Determine HTTP status based on message
        HttpStatus status = determineHttpStatus(ex.getMessage());
        
        return ResponseEntity
            .status(status)
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    /**
     * 🚨 HANDLE ALL OTHER EXCEPTIONS (Catch-all)
     * 
     * Last resort for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(
        Exception ex
    ) {
        log.error("❌ Unexpected exception", ex);
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An unexpected error occurred"));
    }
    
    /**
     * 🎯 DETERMINE HTTP STATUS FROM ERROR MESSAGE
     * 
     * Maps error messages to appropriate HTTP status codes
     * 
     * @param message Error message
     * @return HttpStatus
     */
    private HttpStatus determineHttpStatus(String message) {
        String lowerMessage = message.toLowerCase();
        
        // 401 Unauthorized
        if (lowerMessage.contains("invalid email or password") ||
            lowerMessage.contains("unauthorized") ||
            lowerMessage.contains("invalid credentials")) {
            return HttpStatus.UNAUTHORIZED;
        }
        
        // 403 Forbidden
        if (lowerMessage.contains("access denied") ||
            lowerMessage.contains("permission") ||
            lowerMessage.contains("forbidden") ||
            lowerMessage.contains("role required")) {
            return HttpStatus.FORBIDDEN;
        }
        
        // 404 Not Found
        if (lowerMessage.contains("not found")) {
            return HttpStatus.NOT_FOUND;
        }
        
        // 409 Conflict
        if (lowerMessage.contains("already exists") ||
            lowerMessage.contains("already registered") ||
            lowerMessage.contains("already checked-in") ||
            lowerMessage.contains("duplicate")) {
            return HttpStatus.CONFLICT;
        }
        
        // 400 Bad Request (default)
        return HttpStatus.BAD_REQUEST;
    }
}


