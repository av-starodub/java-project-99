package hexlet.code.advice;

import hexlet.code.dto.ErrorDto;
import hexlet.code.exception.ResourceInUseDeleteException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.exception.UniquenessViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.of("Input data validation failed", details));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.of("Resource not found", ex.getMessage()));
    }

    @ExceptionHandler(UniquenessViolationException.class)
    public ResponseEntity<ErrorDto> handleUniquenessViolation(UniquenessViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.of("Uniqueness violation", ex.getDetails()));
    }

    @ExceptionHandler(ResourceInUseDeleteException.class)
    public ResponseEntity<ErrorDto> handleResourceInUseDeleteException(ResourceInUseDeleteException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorDto.of("Removing the resource used", ex.getMessage()));
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> unauthorized(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorDto.of("Authentication error", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> accessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorDto.of("Access denied", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpectedError(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorDto.of("Unexpected error", ex.getMessage()));
    }

}
