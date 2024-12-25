package hexlet.code.mapper;

import hexlet.code.dto.user.UserCreateDto;
import hexlet.code.dto.user.UserResponseDto;
import hexlet.code.dto.user.UserUpdateDto;
import hexlet.code.model.User;
import org.springframework.stereotype.Service;

@Service
public final class UserMapper extends AbstractMapper<User, UserCreateDto, UserUpdateDto, UserResponseDto> {

    @Override
    public User toDomain(UserCreateDto dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
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
        user.setFirstName(dto.getFirstName().orElse(user.getFirstName()));
        user.setLastName(dto.getLastName().orElse(user.getLastName()));
        user.setEmail(dto.getEmail().orElse(user.getEmail()));
        return user;
    }

}
