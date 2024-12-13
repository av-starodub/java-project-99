package hexlet.code.service;

import hexlet.code.dto.UserCreateDto;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    public User create(UserCreateDto createDto) {
        var passwordHash = encoder.encode(createDto.getPassword());
        var user = User.builder()
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .email(createDto.getEmail())
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

}
