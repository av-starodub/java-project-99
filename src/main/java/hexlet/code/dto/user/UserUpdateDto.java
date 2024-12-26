package hexlet.code.dto.user;


import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.Size;
import lombok.Setter;

import java.util.Optional;

@Setter
public final class UserUpdateDto {

    public static final int MIN_PASSWORD_LENGTH = 3;

    private String firstName;

    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = MIN_PASSWORD_LENGTH, message = "Password must be at least 3 characters long")
    private String password;

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

}
