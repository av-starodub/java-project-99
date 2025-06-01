package hexlet.code.advice;

import hexlet.code.dto.ErrorDto;
import hexlet.code.exception.ResourceNotFoundException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public final class GlobalExceptionHandler {

    private static final String DEFAULT_MESSAGE = "No details";

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
                .body(ErrorDto.of("Resource not found", getErrorMessage(ex)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrity(DataIntegrityViolationException ex) {
        var specific = ex.getMostSpecificCause();
        if (specific instanceof JdbcSQLIntegrityConstraintViolationException cve) {
            return switch (cve.getSQLState()) {
                case "23505" -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorDto.of("Uniqueness violation", "Duplicate value breaks unique constraint"));
                case "23503" -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ErrorDto.of("Removing the resource used", "Entity is referenced by other objects"));
                default -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ErrorDto.of("Data integrity error", getErrorMessage(specific)));
            };
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorDto.of("Data integrity error", getErrorMessage(ex)));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleUnauthorized(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorDto.of("Authentication error", getErrorMessage(ex)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorDto.of("Access denied", getErrorMessage(ex)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpectedError(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorDto.of("Unexpected error", getErrorMessage(ex)));
    }

    private String getErrorMessage(Throwable ex) {
        return Optional.ofNullable(ex.getMessage()).orElse(DEFAULT_MESSAGE);
    }

}
