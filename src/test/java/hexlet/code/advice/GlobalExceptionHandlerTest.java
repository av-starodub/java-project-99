package hexlet.code.advice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 401 and correct ErrorDto")
    void checkHandleUnauthorized() {
        var ex = new BadCredentialsException("Invalid credentials");

        var response = handler.handleUnauthorized(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        var body = response.getBody();
        assertThat(body).isNotNull()
                .matches(b -> b.getError().equals("Authentication error"))
                .matches(b -> b.getDetails().contains("Invalid credentials"));
    }

    @Test
    @DisplayName("Should return 403 and correct ErrorDto")
    void checkHandleAccessDenied() {
        var ex = new AccessDeniedException("Forbidden");

        var response = handler.handleAccessDenied(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        var body = response.getBody();
        assertThat(body).isNotNull()
                .matches(b -> b.getError().equals("Access denied"))
                .matches(b -> b.getDetails().contains("Forbidden"));
    }

}
