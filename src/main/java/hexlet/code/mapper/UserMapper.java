package hexlet.code.mapper;

import hexlet.code.dto.user.UserCreateDto;
import hexlet.code.dto.user.UserResponseDto;
import hexlet.code.dto.user.UserUpdateDto;
import hexlet.code.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class UserMapper extends AbstractMapper<User, UserCreateDto, UserUpdateDto, UserResponseDto> {

    private final PasswordEncoder encoder;

    @Override
    public User toDomain(UserCreateDto dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .passwordHash(encoder.encode(dto.getPassword()))
                .build();
    }

    @Override
    public UserResponseDto domainTo(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public User update(User user, UserUpdateDto dto) {
        return User.builder()
                .id(user.getId())
                .firstName(dto.getFirstName().orElse(user.getFirstName()))
                .lastName(dto.getLastName().orElse(user.getLastName()))
                .email(dto.getEmail().orElse(user.getEmail()))
                .passwordHash(dto.getPassword().map(encoder::encode).orElse(user.getPasswordHash()))
                .build();
    }

}
