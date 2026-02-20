package com.example.jobtracker.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===============================
    // RESOURCE NOT FOUND (404)
    // ===============================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError(404, ex.getMessage()));
    }

    // ===============================
    // VALIDATION ERRORS (400)
    // ===============================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return ResponseEntity
                .badRequest()
                .body(new ApiError(400, msg));
    }

    // ===============================
    // BAD REQUEST (400)
    // ===============================
   @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {

        String message = ex.getMessage();

        if (message != null && message.contains("No enum constant")) {
            message = "Invalid enum value provided";
        }

        return ResponseEntity
                .badRequest()
                .body(new ApiError(400, message));
    }


    // ===============================
    // FALLBACK (500)
    // ===============================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(500, ex.getMessage()));
    }

    // FALLBACK (403)
    @ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity
            .status(403)
            .body(new ApiError(403, ex.getMessage()));
}



}
