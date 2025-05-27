package hexlet.code.security;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class UserPermissionValidator {

    private final UserRepository userRepository;

    public boolean isOwner(Long userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id=%d not found".formatted(userId)));

        var email = authentication.getName();
        return email.equals(user.getEmail());
    }

}
