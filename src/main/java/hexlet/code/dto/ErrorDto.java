package hexlet.code.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorDto {

    private final String error;

    private final List<String> details;

    public static ErrorDto of(String error, List<String> details) {
        return new ErrorDto(error, details);
    }

    public static ErrorDto of(String error, String... details) {
        return new ErrorDto(error, List.of(details));
    }

}
