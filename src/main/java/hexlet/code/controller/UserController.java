package hexlet.code.controller;

import hexlet.code.dto.user.UserCreateDto;
import hexlet.code.dto.user.UserDto;
import hexlet.code.dto.user.UserUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public final class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDto>> index() {
        var users = userService.getAll();
        var userDtos = users.stream()
                .map(this::userToDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(userDtos.size()))
                .body(userDtos);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserCreateDto createDto) {
        var newUser = userService.create(createDto);
        return userToDto(newUser);
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto show(@PathVariable Long id) {
        return userService.getById(id)
                .map(this::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User with id=%d not found".formatted(id)));
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserUpdateDto updateDto) {
        return userService.update(id, updateDto)
                .map(this::userToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User with id=%d not found".formatted(id)));
    }

    private UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
