package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDto;

import hexlet.code.dto.user.UserUpdateDto;
import hexlet.code.exception.UniquenessViolationException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public User create(UserCreateDto createDto) {
        var email = createDto.getEmail();
        validateEmail(email);

        var passwordHash = encoder.encode(createDto.getPassword());
        var user = User.builder()
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .email(email)
                .passwordHash(passwordHash)
                .build();

        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> update(Long id, UserUpdateDto updateDto) {
        updateDto.getEmail().ifPresent(this::validateEmail);
        return getById(id)
                .map(user -> updateData(user, updateDto))
                .map(userRepository::save);
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UniquenessViolationException(List.of("Email %s already exists".formatted(email)));
        }
    }

    private User updateData(User user, UserUpdateDto updateDto) {
        return User.builder()
                .id(user.getId())
                .firstName(updateDto.getFirstName().orElse(user.getFirstName()))
                .lastName(updateDto.getLastName().orElse(user.getLastName()))
                .email(updateDto.getEmail().orElse(user.getEmail()))
                .passwordHash(updateDto.getPassword().map(encoder::encode).orElse(user.getPasswordHash()))
                .build();
    }

}
