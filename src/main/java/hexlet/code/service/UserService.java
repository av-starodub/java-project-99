package hexlet.code.service;

import hexlet.code.dto.UserCreateDto;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    public Optional<User> create(UserCreateDto createDto) {
        var passwordHash = encoder.encode(createDto.getPassword());
        var user = User.builder()
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .email(createDto.getEmail())
                .password(passwordHash)
                .build();
        System.out.println("Created user: " + user.toString());
        return Optional.of(userRepository.save(user));
    }

}
